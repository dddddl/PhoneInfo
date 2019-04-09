package com.lx.qz.transform.command

import android.content.Context
import com.lx.qz.transform.response.AndroidContactsUtil
import com.lx.qz.utils.MsgUtil

class ContactsCountCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {

    override fun executor(): ByteArray {

        val contactCount = AndroidContactsUtil.getContactCount(context)

        return MsgUtil.envelopedData(true, commandGroup, commandOperation, MsgUtil.intToBytes(contactCount))
    }
}
