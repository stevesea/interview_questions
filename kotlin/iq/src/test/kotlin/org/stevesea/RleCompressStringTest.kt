package org.stevesea

import org.junit.Assert
import org.junit.Test

class RleCompressStringTest {

    @Test
    fun compressTest1() {
        Assert.assertEquals("cat", rle_compress("cat"))
    }
    @Test
    fun compressTest2() {
        Assert.assertEquals("s3c2o5p3s1", rle_compress("sssccoooooppps"))
    }
    @Test
    fun compressEmptyStr() {
        Assert.assertEquals("", rle_compress(""))
    }
    @Test
    fun compress1LengthStr() {
        Assert.assertEquals("g", rle_compress("g"))
    }

    @Test
    fun decompress() {
        Assert.assertEquals("sssccoooooppps", rle_decompress("s3c2o5p3s1"))
    }

    @Test
    fun decompress2() {
        Assert.assertEquals("cat", rle_decompress("cat"))
    }
    @Test
    fun decompressEmpty() {
        Assert.assertEquals("", rle_decompress(""))
    }
}
