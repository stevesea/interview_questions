package org.stevesea.relayserver

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.common.util.concurrent.*
import mu.KLoggable
import org.stevesea.relayserver.RelayServerCLI.DEFAULT_RELAY_PORT
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * relay server listens to bindhost:relayport, and accepts connections from
 * relay-requesters. messages on that incoming socket might be:
 *   * request for new relay to be created "NewRelay"
 *   * request to be connected to a relay-client (the requester returns the clientid)
 *
 */
class RelayServer(val bindname: String,
                  val remoteHostname: String,
                  val relayport : Int = DEFAULT_RELAY_PORT,
                  relayPortRange: IntRange) :
        AutoCloseable,
        KLoggable {
    override val logger = logger()

    // map of relayId -> Relay
    //   relayId is something like "remotehostname:port"
    val relays = ConcurrentHashMap<String, Relay>()
    // map of remoteAddr -> Relays
    //    a relay-requester _might_ request more than one relay. keep track so that when the socket that requested
    //    the relay dies, we can clean up any relays created for it.
    val remoteAddr2RelayIds : Multimap<String, String> = ArrayListMultimap.create<String,String>()

    // TODO: at the moment... each Relay is implemented to loop endlessly, checking the relay's selector.
    //   So... we'll create a execService that can have enough threads for all relays (dictated by the portrange).
    //   Maybe a better approach is a smaller threadpool. Instead of FixedThreadPool could use a ScheduledThreadPool,
    //   and implement the Relay's run() method so that it only does 1 iteration and then gives the thread back to
    //   the pool until the next scheduled run.
    //   That way we could still service all the relays but with just a small number of threads.
    val execService : ListeningExecutorService = MoreExecutors.listeningDecorator(
            Executors.newFixedThreadPool(relayPortRange.count(), ThreadFactoryBuilder()
                    .setNameFormat("tcp-relay-%d")
                    .setDaemon(true)
                    .build())
    )

    override fun close() {
        relays.values.forEach { v ->
            v.triggerShutdown()
        }
        execService.shutdown()
        serverChannel.close()
    }

    val availablePorts_ : LinkedList<Int> by lazy {
        val res = LinkedList<Int>()
        res.addAll(relayPortRange.asIterable())
        res
    }
    fun getAvailablePort() : Int {
        synchronized(availablePorts_)  {
            return availablePorts_.removeFirst()
        }

    }
    fun returnPortToAvailable(p: Int) {
        synchronized(availablePorts_)  {
            //TODO: Need to return ports after relay-requester dies
            availablePorts_.addLast(p)
        }
    }

    val selector : Selector by lazy {
        Selector.open()
    }
    val serverChannel : ServerSocketChannel by lazy {
        ServerSocketChannel.open()
    }

    init {
        logger.info("Starting relay server on $remoteHostname:$relayport (bind addr: $bindname) ...")

        serverChannel.apply {
            bind(InetSocketAddress(bindname, relayport))
            configureBlocking(false)
            register(selector, serverChannel.validOps())
        }
    }

    fun start() {
        while(true) {
            selector.select()

            val keys = selector.selectedKeys()
            val iterator = keys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                try {
                    when {
                        key.isValid && key.isAcceptable -> accept(key)
                        key.isValid && key.isReadable -> handleRequesterMessage(key)
                    }
                } catch (e: IOException) {
                    logger.warn(e.toString())
                    checkForRelayCleanup((key.channel() as SocketChannel).remoteAddress.toString())
                    key.channel().close()
                    key.cancel()
                }
                iterator.remove()
            }
        }
    }

    private fun checkForRelayCleanup(remoteAddrStr: String) {
        synchronized(remoteAddr2RelayIds) {
            remoteAddr2RelayIds[remoteAddrStr].forEach { it ->
                logger.warn("Triggering relay shutdown for: $it")
                relays[it]?.triggerShutdown()
            }
        }
    }

    /**
     * on new sockets -- configure them to be non-blocking, and register with selector
     */
    private fun accept(key: SelectionKey) {
        val channel = key.channel() as ServerSocketChannel
        val sockChannel = channel.accept()
        sockChannel.apply {
            configureBlocking(false)
            register(key.selector(), SelectionKey.OP_READ)
            logger.debug("New connection accepted: $localAddress")
        }
    }

    /**
     * when data is read from socket... it's going to a message from relay-requester.
     * it'll either be
     *  - "hey, set me up a relay"
     *  - or, "here's the socket to use to talk to a relay-client (and message is clientId sent by relay)"
     */
    private fun handleRequesterMessage(key: SelectionKey) {
        val channel = key.channel() as SocketChannel

        // TODO: what about a partial message?
        val buffer = ByteBuffer.allocate(512)
        val bytesRead = channel.read(buffer)

        if (bytesRead == -1) {
            logger.debug("Nothing read.")
            return
        }

        buffer.flip()
        val message = Charsets.UTF_8.decode(buffer).toString()
        logger.debug("Message received: $message")

        // if it's 'NewRelay', create a relay. Otherwise, try to parse it as a clientId
        when (message) {
            "NewRelay" -> createRelay(key)
            else -> associateRelayChannel(key, message)
        }
    }

    /**
     * create a relay for the requester.
     */
    private fun createRelay(key: SelectionKey) {
        val channel = key.channel() as SocketChannel
        try {
            val newPort = getAvailablePort()

            val relayId = "$remoteHostname:$newPort"

            // create the relay, add it to our collection, and start it up
            val relay = Relay(newPort, relayId, bindname, WeakReference(channel))
            relays.put(relayId, relay)

            synchronized(remoteAddr2RelayIds) {
                remoteAddr2RelayIds.put(channel.remoteAddress.toString(), relayId)
            }

            val future = execService.submit(relay)

            Futures.addCallback(future,
                    object : FutureCallback<Any> {
                        override fun onSuccess(result: Any?) {
                            // might reach this on forced-shutdown of relay, that process will log noisily
                            logger.debug("Relay done $relayId")
                            relays.remove(relayId)
                            returnPortToAvailable(newPort)
                        }

                        override fun onFailure(t: Throwable?) {
                            logger.warn("Relay $relayId failed. ${t?.message.orEmpty()}", t)
                            relays.remove(relayId)
                            returnPortToAvailable(newPort)
                        }
                    },
                    MoreExecutors.directExecutor() // run CB on the same thread as task had run
            )

            // tell the requester the host:port of its relay
            channel.write(relayId.toByteBuffer())
        } catch (e: NoSuchElementException) {
            // if no more relay ports are available
            logger.warn("Out of relay ports to allocate")
            channel.write("ERROR: Unable to allocate relay".toByteBuffer())
        } catch (e: Exception) {
            // could be IOException, AlreadyBoundException, UnsupportedAddressTypeException, SecurityException
            //   -- in any case, big trouble creating the relay. abort! abort!
            val msg = "Problem creating relay: ${e.message.orEmpty()}"
            logger.warn(msg,e)
            channel.write("ERROR: $msg".toByteBuffer())
        }
    }

    /**
     * if it wasn't new-relay request, it's a request to associate the channel with a relay-client
     */
    private fun associateRelayChannel(key: SelectionKey, message: String) {
        // validate message format
        //    expected: <relayId>;<clientId>
        val chan = key.channel() as SocketChannel

        val words = message.split(';', limit = 2)
        if (words.size < 2) {
            logger.warn("Received unknown message: $message")
            chan.close()
            key.cancel()
        }
        val relayId = words[0]
        val clientId = words[1]

        val relay = relays[relayId]
        if (relay != null) {
            logger.debug("Associate client: $message")
            // this socket is now 'owned' by the relay's selector, so remove the channel from relay-server's selector.
            key.cancel()
            relay.associateRequesterChannel(clientId, chan)
        } else {
            logger.warn("Unknown relay-requester: $relayId (from message: $message)")
            chan.close()
            key.cancel()
        }
    }
}

