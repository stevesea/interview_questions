package org.stevesea.echoserver

import com.google.common.util.concurrent.Service
import com.google.common.util.concurrent.ServiceManager
import mu.KLoggable
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.annotation.Arg
import net.sourceforge.argparse4j.inf.ArgumentParserException
import java.util.concurrent.TimeUnit

/**
 * CLI interface to the relayed echo server
 */
object EchoServerCLI : KLoggable {
    const val DEFAULT_RELAY_PORT = 8080
    const val DEFAULT_RELAY_HOST = "localhost"

    class Options {
        @Arg
        var relayport = DEFAULT_RELAY_PORT
        @Arg
        var relayhost = DEFAULT_RELAY_HOST
    }

    override val logger = logger()

    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgumentParsers
                .newArgumentParser("echoserver")
                .description("start an echoserver which coordinates with a specific TCP relay implementation")
                .defaultHelp(true)

        parser.addArgument("relayhost")
                .metavar("RELAYHOST")
                .type(String::class.java)
                .setDefault(DEFAULT_RELAY_HOST)
                .nargs("?")
                .help("The hostname of the host implementing the TCP Relay")
        parser.addArgument("relayport")
                .metavar("RELAYPORT")
                .type(Int::class.java)
                .setDefault(DEFAULT_RELAY_PORT)
                .nargs("?")
                .help("The port # of the TCP Relay server")

        val opts = Options()
        try {
            parser.parseArgs(args, opts)
        } catch (e: ArgumentParserException) {
            parser.handleError(e)
            System.exit(1)
        }

        var shutdown = false

        val svc = RelayedEchoService(opts.relayhost, opts.relayport)

        val svcMgr =  ServiceManager(
                listOf(
                        svc
                )
        )

        svcMgr.addListener(object : ServiceManager.Listener() {
            override fun stopped() {
                logger.debug("Services stopped")
                shutdown = true
            }

            override fun healthy() {
                logger.debug("Services healthy")
            }

            override fun failure(service: Service?) {
                logger.debug("Services failed: $service")
                shutdown = true
            }
        })

        svcMgr.startAsync()
        while (!shutdown) {
            TimeUnit.MILLISECONDS.sleep(100)
        }
        svcMgr.awaitStopped(5, TimeUnit.SECONDS)
    }
}

