package com.dl.forensics.transform.command

import android.content.Context
import android.util.Log
import com.dl.forensics.transform.response.AndroidCallHistoryUtil
import com.dl.forensics.utils.MsgUtil

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
