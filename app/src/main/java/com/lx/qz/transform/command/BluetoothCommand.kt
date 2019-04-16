package com.lx.qz.transform.command

import com.lx.qz.transform.response.AndroidBluetoothUtil
import com.lx.qz.utils.MsgUtil

class BluetoothCommand(
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    override fun executor(): ByteArray {

        val bluetooth = AndroidBluetoothUtil.getBluetooth()

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            bluetooth.toXMLPropertyList().toByteArray()
        )
    }
}