package com.lx.qz.transform.command

import android.content.Context
import android.util.Log
import com.lx.qz.transform.response.AndroidCallHistoryUtil
import com.lx.qz.utils.MsgUtil

class CallHistoryCountCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    val TAG = this.javaClass.simpleName

    override fun executor(): ByteArray {
        Log.e("qz", "CallHistoryCountCommand execute...")
        val count = AndroidCallHistoryUtil.getCallHistoryCount(context)

        return MsgUtil.envelopedData(true, commandGroup, commandOperation, MsgUtil.intToBytes(count))

    }
}
