package org.stevesea.relayserver

import mu.KLoggable
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentHashMap


/**
 * a relay listens on a specific port for client connections, and responds
 * by requesting the relay-requester create a new socket for that client's traffic.
 *
 * the relay-server will get that new-socket request, and direct it to the correct
 * relay's associateRequesterChannel() method.
 */
class Relay(val port : Int, // port for the Relay
            val relayId: String, // id of this relay (hostname:port)
            val bindname: String,
            // we don't 'own' the message channel back to the relay-requester, it's the relay-server's.
            // when the Relay is shutdown, we should not close the relay (maybe the relay-requester
            // started up multiple relays)
            val requesterMessageChannel: WeakReference<SocketChannel>
        ) : KLoggable, Runnable {

    override val logger = logger()

    @Volatile var shutdownRequested = false
    fun triggerShutdown() {
        shutdownRequested = true
        relaySelector.wakeup()
    }

    /**
     * the selector in the Relay holds:
     *  - a server socket listening for incoming connections
     *  - N client sockets that have been accepted
     *  - N relay-requester sockets that were accepted by relay-server and passed to the Relay to pair with the
     *    client socket that uses the same clientId
     *
     * the messaging socket back to the relay-requester is only used for writing when notifying the requester of
     * new client connections, but it is never read from (the selector in the relay-server is reading it however)
     */
    val relaySelector : Selector by lazy {
        Selector.open()
    }

    // maps of clientIds -> channels
    val clientChannels = ConcurrentHashMap<String, SocketChannel>()
    val requesterChannels = ConcurrentHashMap<String, SocketChannel>()

    // on construction of the relay, bind the port for the relay and listen for clients
    init {
        logger.info("Starting relay at $bindname:$port ...")
        val serverChannel = ServerSocketChannel.open()
        serverChannel.apply {
            bind(InetSocketAddress(bindname, port))
            configureBlocking(false)
            register(relaySelector, SelectionKey.OP_ACCEPT)
        }
    }

    private fun shutdown() {
        relaySelector.keys().forEach { k ->
            k.channel().close()
            k.cancel()
        }
        relaySelector.close()
    }

    override fun run() {
        try {
            while (!shutdownRequested) {
                relaySelector.select()

                val keys = relaySelector.selectedKeys()
                val iterator = keys.iterator()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    try {
                        when {
                            key.isValid && key.isAcceptable -> acceptNewClient(key)
                            key.isValid && key.isReadable -> relayTraffic(key)
                        }
                    } catch (e: IOException) {
                        // channels in our selector will either be:
                        //   the relay server's socket
                        //   or, client/requester pairs.
                        val relayChannelId = key.attachment() as? RelayChannelId
                        if (relayChannelId != null) {
                            logger.warn("$relayId: communication broken for client ${relayChannelId.clientId}")
                            requesterChannels[relayChannelId.clientId]?.close()
                            clientChannels[relayChannelId.clientId]?.close()
                        } else {
                            logger.warn(e.toString())
                            triggerShutdown()
                            throw e
                        }
                        key.cancel()
                    } finally {
                        iterator.remove()
                    }
                }
            }
        } finally {
            logger.debug("Shutting down relay $relayId")
            shutdown()
        }
    }

    // called either when data needs to be transferred between
    // relay-client and relay-requester, or vice-versa.
    @Throws(IOException::class)
    private fun relayTraffic(key: SelectionKey) {
        val relayId = key.attachment() as RelayChannelId
        val clientId = relayId.clientId
        val clientChannel = clientChannels[clientId]
        val requesterChannel = requesterChannels[clientId]

        // only do transferring if we have _both_ sides of the communication.
        // This could be an awful idea, but it seems to work in testing so far.
        if (clientChannel == null || requesterChannel == null)
            return

        if (relayId.isClient) {
            // read-able data is on client-side
            transferBetweenChannels(clientChannel, requesterChannel)
        } else {
            transferBetweenChannels(requesterChannel, clientChannel)
        }
    }

    // only called when a new client connects to this relay.
    // when this happens, we need to send an event back to the
    // relay-requester in order the relay-requester to create a new socket.
    @Throws(IOException::class)
    private fun acceptNewClient(key: SelectionKey) {
        val channel = key.channel() as ServerSocketChannel
        val sockChannel = channel.accept()
        val clientId = sockChannel.remoteAddress.toString()
        sockChannel.apply {
            configureBlocking(false)
            register(key.selector(), SelectionKey.OP_READ,
                    RelayChannelId(true, clientId))
            clientChannels.put(clientId, sockChannel)
            logger.debug("$relayId: Connection accepted: $remoteAddress -> $localAddress")
        }
        requesterMessageChannel.get()?.write("$relayId;$clientId".toByteBuffer())
    }

    // called by relay-server after the relay-requester has created a new socket
    // for a client of this relay.
    @Throws(IOException::class)
    fun associateRequesterChannel(clientId: String, channel: SocketChannel) {
        logger.debug("associating requester channel with $relayId;$clientId")
        if (requesterChannels.containsKey(clientId)) {
            // TODO: right way to handle this? Seems like heavy solution... but
            //   it'll prevent some other requester from trying to take over the client
            //   connection.
            throw IllegalArgumentException("requester socket for '$relayId;$clientId' already registered")
        }
        requesterChannels.put(clientId, channel)
        channel.register(relaySelector, SelectionKey.OP_READ,
                RelayChannelId(false, clientId))
    }

    /**
     * this data class identifies a channel for relay-ing TCP traffic. it'll be
     * attached to both client/requester channels in the Relay's selector.
     *
     * the isClient flag says whether it's a client channel or requester channel,
     * the clientId field identifies the channel in our lookup map(s).
     *
     */
    data class RelayChannelId(val isClient: Boolean, val clientId: String)
}


