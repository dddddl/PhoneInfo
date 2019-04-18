package com.dl.forensics.transform.command

import com.dl.forensics.transform.response.AndroidBluetoothUtil
import com.dl.forensics.utils.MsgUtil

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