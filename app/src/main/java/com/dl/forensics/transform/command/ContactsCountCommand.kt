package com.dl.forensics.transform.command

import android.content.Context
import android.util.Log
import com.dl.forensics.transform.response.AndroidContactsUtil
import com.dl.forensics.utils.MsgUtil

class ContactsCountCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {

    val TAG = this.javaClass.simpleName

    override fun executor(): ByteArray {

        Log.e("qz", "ContactsCountCommand execute...")

        val contactCount = AndroidContactsUtil.getContactCount(context)

        return MsgUtil.envelopedData(true, commandGroup, commandOperation, MsgUtil.intToBytes(contactCount))
    }
}
