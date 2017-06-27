package org.stevesea.relayserver

import mu.KLoggable
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.annotation.Arg
import net.sourceforge.argparse4j.inf.ArgumentParserException


object RelayServerCLI : KLoggable {
    override val logger = logger()

    const val DEFAULT_RELAY_PORT = 8080
    const val DEFAULT_PORT_RANGE_MIN = 8081
    const val DEFAULT_PORT_RANGE_MAX = 9081

    class Options {
        @Arg
        var relayport: Int = DEFAULT_RELAY_PORT

        @Arg
        var minPort : Int = DEFAULT_PORT_RANGE_MIN

        @Arg
        var maxPort : Int = DEFAULT_PORT_RANGE_MAX

        @Arg
        var bindHost : String = "0.0.0.0"

        @Arg
        var remoteHostname : String = localHostname
    }


    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgumentParsers
                .newArgumentParser("relay")
                .description("start a TCP Relay")
                .defaultHelp(true)

        parser.addArgument("relayport")
                .metavar("RELAYPORT")
                .type(Int::class.java)
                .setDefault(DEFAULT_RELAY_PORT)
                .nargs("?")
                .help("The port # of the TCP Relay server")

        parser.addArgument("--bindhost")
                .metavar("BINDHOST")
                .type(String::class.java)
                .setDefault("0.0.0.0")
                .nargs("?")
                .help("the hostname to bind our server to")
        parser.addArgument("--remoteHostname")
                .metavar("HOSTNAME")
                .type(String::class.java)
                .setDefault(localHostname)
                .nargs("?")
                .help("the hostname relays should use to access the relay-server")

        parser.addArgument("--minPort")
                .metavar("MIN")
                .type(Int::class.java)
                .setDefault(DEFAULT_PORT_RANGE_MIN)
                .nargs("?")
                .help("the min port value to use for created relays")
        parser.addArgument("--maxPort")
                .metavar("MAX")
                .type(Int::class.java)
                .setDefault(DEFAULT_PORT_RANGE_MAX)
                .nargs("?")
                .help("the max port value to use for created relays")

        val opts = Options()
        try {
            parser.parseArgs(args, opts)
        } catch ( e: ArgumentParserException) {
            parser.handleError(e)
            System.exit(1)
        }

        val createdPortRange = opts.minPort..opts.maxPort
        if (createdPortRange.contains(opts.relayport)) {
            logger.error("relay port ${opts.relayport} cannot be in range $createdPortRange")
            parser.printUsage()
            System.exit(1)
        }
        
        RelayServer(opts.bindHost, opts.remoteHostname,
                opts.relayport, createdPortRange).use { rs ->
            rs.start()
        }
        
    }
}
