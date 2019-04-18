package com.dl.forensics.transform.command

import android.content.Context
import android.util.Log
import com.dl.forensics.transform.response.AndroidTextMessageUtilNew
import com.dl.forensics.utils.MsgUtil

class MessageTextCountCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {

    val TAG = this.javaClass.simpleName

    override fun executor(): ByteArray {
        Log.e("qz", "MessageTextCountCommand execute...")

        val count = AndroidTextMessageUtilNew.getTextMessageThreadCount(context)

        return MsgUtil.envelopedData(true, commandGroup, commandOperation, MsgUtil.intToBytes(count))
    }
}
