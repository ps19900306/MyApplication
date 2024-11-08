package com.nwq.baseutils

object ByteToIntUtils {
    /**
     * 将四个字节转换为一个整数。
     *
     * @param bytes 四个字节的数组
     * @return 转换后的整数
     */
    fun bytesToInt(bytes: ByteArray): Int {
        require(bytes.size == 4) { "Byte array must have exactly 4 elements" }
        return ((bytes[0].toInt() and 0xFF) shl 24) or
                ((bytes[1].toInt() and 0xFF) shl 16) or
                ((bytes[2].toInt() and 0xFF) shl 8) or
                (bytes[3].toInt() and 0xFF)
    }

    /**
     * 将一个整数转换为四个字节。
     *
     * @param value 要转换的整数
     * @return 包含四个字节的数组
     */
    fun intToBytes(value: Int): ByteArray {
        return byteArrayOf(
            ((value ushr 24) and 0xFF).toByte(),
            ((value ushr 16) and 0xFF).toByte(),
            ((value ushr 8) and 0xFF).toByte(),
            (value and 0xFF).toByte()
        )
    }

    /**
     * 从一个整数中提取指定位置的字节。
     *
     * @param value 整数
     * @param index 0 到 3，表示第几个字节
     * @return 提取的字节
     */
    fun getByteFromInt(value: Int, index: Int): Byte {
        require(!(index < 0 || index > 3)) { "Index must be between 0 and 3" }
        return ((value ushr (24 - (index * 8))) and 0xFF).toByte()
    }

    /**
     * 从一个整数中提取指定位置的字节。
     *
     * @param value 整数
     * @param index 0 到 3，表示第几个字节
     * @return 提取的字节
     */
    fun getByteFromInt2(value: Int, index: Int): Int {
        require(!(index < 0 || index > 3)) { "Index must be between 0 and 3" }
        return ((value ushr (24 - (index * 8))) and 0xFF).toByte() + 128
    }

    /**
     * 将一个字节设置到整数的指定位置。
     *
     * @param value 整数
     * @param index 0 到 3，表示第几个字节
     * @param byteValue 要设置的字节
     * @return 更新后的整数
     */
    fun setByteToInt(value: Int, index: Int, byteValue: Byte): Int {
        require(!(index < 0 || index > 3)) { "Index must be between 0 and 3" }
        val mask = (0xFF shl (24 - (index * 8))).inv()
        return (value and mask) or ((byteValue.toInt() and 0xFF) shl (24 - (index * 8)))
    }

    /**
     * 将一个字节设置到整数的指定位置。
     *
     * @param value 整数
     * @param index 0 到 3，表示第几个字节
     * @param byteValue2 要设置的字节
     * @return 更新后的整数
     */
    fun setByteToInt2(value: Int, index: Int, byteValue2: Int): Int {
        val byteValue = byteValue2 - 255
        require(!(index < 0 || index > 3)) { "Index must be between 0 and 3" }
        val mask = (0xFF shl (24 - (index * 8))).inv()
        return (value and mask) or ((byteValue.toInt() and 0xFF) shl (24 - (index * 8)))
    }


    /**
     * 将字节数组转换为十六进制字符串，用于调试。
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private fun bytesToHex(bytes: ByteArray): String {
        val hexString = StringBuilder()
        for (b in bytes) {
            val hex = Integer.toHexString(0xFF and b.toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex).append(" ")
        }
        return hexString.toString().trim { it <= ' ' }
    }
}