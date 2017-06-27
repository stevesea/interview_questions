package org.stevesea.relayserver

import com.google.common.io.CharStreams
import org.apache.commons.lang3.SystemUtils
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel


val localHostname by lazy {
    getHostName()
}
fun getHostName() : String {
    try {
        // most reliable way to get the hostname is via 'hostname'
        return execReadToString("hostname").trim()
    } catch (e: IOException) {
        // but, if that doesn't work read it from environment
        return System.getenv(if (SystemUtils.IS_OS_WINDOWS) "COMPUTERNAME" else "HOSTNAME")
    }
}

// ext function to convert a string into a UTF-8 encoded byte buffer
fun String.toByteBuffer(): ByteBuffer = Charsets.UTF_8.encode(this)

@Throws(IOException::class)
internal fun execReadToString(vararg execCommand: String): String {
    val pb = ProcessBuilder(*execCommand)
    pb.redirectErrorStream(true)
    val proc = pb.start()
    InputStreamReader(proc.inputStream, Charsets.UTF_8).use {
        isr -> return CharStreams.toString(isr).trim()
    }
}


/**
 * transfer data between incoming channel and outgoing channel.
 */
@Throws(IOException::class)
fun transferBetweenChannels(chanIncoming: SocketChannel, chanOutgoing: SocketChannel, bufSize : Int = 1024) {
    val buf = ByteBuffer.allocate(bufSize)

    var bytesRead = chanIncoming.read(buf)
    while (bytesRead > 0) {

        buf.flip()

        chanOutgoing.write(buf)

        buf.clear() //make buffer ready for writing
        bytesRead = chanIncoming.read(buf)
    }
}