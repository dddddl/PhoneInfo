package com.lx.qz.transform.command

import android.content.Context
import com.lx.qz.transform.response.AndroidContactsUtil
import com.lx.qz.utils.MsgUtil

class ContactCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int,
    private val rawData: ByteArray?
) :
    Command {
    override fun executor(): ByteArray {

        var index = 0
        if (rawData?.size == 4) {
            index = MsgUtil.bytesToInt(rawData)
        }

        val contact = AndroidContactsUtil.getContactByIndex(index, context)

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            contact.toXMLPropertyList().toByteArray()
        )
    }
}
