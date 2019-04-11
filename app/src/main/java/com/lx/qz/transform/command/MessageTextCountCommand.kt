package com.lx.qz.transform.command

import android.content.Context
import android.util.Log
import com.lx.qz.transform.response.AndroidTextMessageUtilNew
import com.lx.qz.utils.MsgUtil

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
