package com.lx.qz.transform.command

import android.graphics.BitmapFactory
import com.lx.qz.transform.response.AndroidBluetoothUtil
import com.lx.qz.utils.MsgUtil
import java.nio.ByteBuffer

class ScreenshotCommand(
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    override fun executor(): ByteArray {

        val bluetooth = AndroidBluetoothUtil.getBluetooth()
        val bitmap = BitmapFactory.decodeFile("")
        val bytes = bitmap.byteCount
        val buf = ByteBuffer.allocate(bytes)
        bitmap.copyPixelsToBuffer(buf)
        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            buf.array()
        )
    }
}