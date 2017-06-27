package org.stevesea.echoserver

import com.google.common.util.concurrent.AbstractExecutionThreadService
import mu.KLoggable
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel


/**
 * this echo server connects to a specific sort of TCP Relay (listening on
 * relayhost:relayport)
 *
 * on startup, it connects with the relay and sends message 'NewRelay'
 * the relay-server responds with the "host:port" that echo-server clients
 * should use to communicate with that instance of the echo-server
 *
 * the relay-server may send new-client messages to the echo-server, which the
 * echo-server should respond to by opening another socket to the relay-server
 * (at relayhost:relayport).
 *
 * the message from the relay-server to the echo-server will be a client ID.
 * after opening the additional socket to the relay-server, the echo-server
 * must write that exact clientID to the socket. any further traffic is
 * transmitted from/to the client
 *
 */
class RelayedEchoService(val relayhost: String, val relayport: Int) :
        AbstractExecutionThreadService(), KLoggable {
    override val logger = logger()

    // the NIO Selector for the echo server contains both the one message-channel to the relay-server,
    // and also echo channels for the relay-clients. To know the difference, look at the SelectionKey's
    // attachment (a String (the clientid) if not null he socket shall be echoed).
    // in either case, these are all channels opened by the echo-server itself. echo-server
    // never accepts sockets, and doesn't bind to the local host.
    val selector : Selector = Selector.open()

    @Volatile var shutdownRequested = false

    override fun triggerShutdown() {
        shutdownRequested = true
        selector.wakeup()
    }

    override fun run() {
        while (!shutdownRequested) {
            selector.select()
            val keys = selector.selectedKeys()
            val iterator = keys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                try {
                    val clientId = key.attachment() as? String?
                    if (key.isValid && key.isReadable) {
                        // if channel is messsage-channel to relay-server, look at message
                        if (clientId.isNullOrBlank()) {
                            try {
                                handleMesssage(key)
                            } catch (e: IOException) {
                                // if it's an exception handling message, bail out
                                logger.warn("Lost connection to relay-server. Shutting down.")
                                logger.debug("${e.message}", e)
                                key.channel().close()
                                key.cancel()
                                triggerShutdown()
                            }
                        } else {
                            // otherwise, it's the echo channel
                            try {
                                echo(key.channel() as SocketChannel)
                            } catch (e: IOException) {
                                logger.warn("Lost connection to client ($clientId): ${e.message}")
                                // if it's an exception echoing to client, close
                                key.channel().close()
                                key.cancel()
                            }
                        }
                    }
                } finally {
                    iterator.remove()
                }
            }
        }
    }

    /**
     * the channel has readable data, read anything available and write it back the channel
     */
    private fun echo(chan: SocketChannel) {
        val buf = ByteBuffer.allocate(1024) // share a buffer?
        var bytesRead = chan.read(buf)
        while (bytesRead > 0) {
            buf.flip()
            chan.write(buf)
            buf.clear()

            bytesRead = chan.read(buf)
        }
    }

    @Throws(IOException::class)
    private fun handleMesssage(key: SelectionKey) {
        val channel = key.channel() as SocketChannel

        val buffer = ByteBuffer.allocate(512)
        val bytesRead = channel.read(buffer)
        if (bytesRead == -1) {
            return
        }
        buffer.flip()
        val message = Charsets.UTF_8.decode(buffer)
        logger.debug("Received message: $message")

        // if message is <host>:<port>, then it's the initial response.
        // other messages might be:
        //   <host>:<port>:<client>, which we need to respond to by starting a new socket
        val words = message.split(':')
        when (words.size) {
            2 -> {
                logger.info("established relay address: $message")
            }
            else -> {
                logger.info("New client: $message")
                val chan = createRelaySocket(message.toString())
                chan.write(Charsets.UTF_8.encode(message))
            }
        }
    }

    // creates a socket to the relay-server and registers it with the echo service's selector.
    // if userForEcho is true, that channel will echo anything sent to it
    fun createRelaySocket(clientId: String? = null) : SocketChannel {
        val chan = SocketChannel.open(InetSocketAddress(relayhost, relayport))
        chan.configureBlocking(false)
        chan.register(selector, SelectionKey.OP_READ, clientId)
        return chan
    }

    // on startup, connect to the relay-server and send a 'NewRelay' message
    override fun startUp() {
        logger.debug("Connecting to relay server at $relayhost:$relayport ...")
        val messageChannel = createRelaySocket()
        messageChannel.write(Charsets.UTF_8.encode("NewRelay"))
    }

    // on shutdown, make sure all channels are closed
    override fun shutDown() {
        selector.keys().forEach { k ->
            k.channel().close()
            k.cancel()
        }
        selector.close()
    }
}
