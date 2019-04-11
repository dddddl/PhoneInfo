package com.lx.qz.utils

import android.util.Log
import java.security.MessageDigest

object MsgUtil {
    val packageHeaderSize = 12
    private var messagePrefixBytes : ByteArray

    init {
        val devSerial = android.os.Build.SERIAL
        val md5 = MessageDigest.getInstance("MD5")
        val md5Bytes = md5.digest(devSerial.toByteArray())
        md5Bytes.forEachIndexed { index, byte ->
            Log.d("MD5", "md5Bytes[$index]:$byte")
        }
        messagePrefixBytes = md5Bytes.copyOfRange(md5Bytes.size - 7, md5Bytes.size)
    }


    fun envelopedData(
        withHeader: Boolean = true,
        commandGroup: Int,
        commandOperation: Int,
        data: ByteArray?
    ): ByteArray {

        var totalByte = packageHeaderSize

        var dataLen = 4

        if (data != null) {
            dataLen += data.size
        }

        totalByte += dataLen
        val replyData = ByteArray(totalByte)
        replyData[8] = dataLen.shr(24).toByte()
        replyData[9] = dataLen.shr(16).toByte()
        replyData[10] = dataLen.shr(8).toByte()
        replyData[11] = dataLen.toByte()

        replyData[12] = commandGroup.shr(8).toByte()
        replyData[13] = commandGroup.toByte()
        replyData[14] = commandOperation.shr(8).toByte()
        replyData[15] = commandOperation.toByte()

        if (data != null) {
            System.arraycopy(data, 0, replyData, 16, data.size)
        }

        if (withHeader) {
            initPacketHeaderPrefix(replyData)
        }

        return replyData

    }

    private fun initPacketHeaderPrefix(packet: ByteArray) {
        /*
        val devSerial = android.os.Build.SERIAL
        val md5 = MessageDigest.getInstance("MD5")
        val md5Bytes = md5.digest(devSerial.toByteArray())
        md5Bytes.forEachIndexed { index, byte ->
            Log.d("MD5", "md5Bytes[$index]:$byte")
        }
        val md5Suffix = md5Bytes.copyOfRange(md5Bytes.size - 7, md5Bytes.size)
        md5Suffix.forEachIndexed { index, byte ->
            Log.d("MD5", "md5Suffix[$index]:$byte")
        }
        */
        System.arraycopy(messagePrefixBytes, 0, packet, 0, messagePrefixBytes.size)
        packet[7] = 0
        val checkSum = packet.sum().toByte()
        packet[7] = checkSum
        packet.forEachIndexed { index, byte ->
//            Log.d("Packet", "packet[$index]:$byte")
        }
    }

    open fun bytesToInt(bytes:ByteArray) : Int {
        var intVal = 0
        intVal += bytes[0].toInt().and(0xff).shl(24)
        intVal += bytes[1].toInt().and(0xff).shl(16)
        intVal += bytes[2].toInt().and(0xff).shl(8)
        intVal += bytes[3].toInt().and(0xff)
        return intVal
    }

    open fun intToBytes(value: Int) : ByteArray{
        val bytes = ByteArray(4)
        bytes[3] = value.toByte()
        bytes[2] = value.shr(8).toByte()
        bytes[1] = value.shr(16).toByte()
        bytes[0] = value.shr(24).toByte()
        return bytes
    }


}
