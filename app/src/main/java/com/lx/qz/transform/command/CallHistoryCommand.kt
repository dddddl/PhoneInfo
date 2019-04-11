package com.lx.qz.transform.command

import android.content.Context
import android.util.Log
import com.lx.qz.transform.response.AndroidCallHistoryUtil
import com.lx.qz.utils.MsgUtil

class CallHistoryCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int,
    private val rawData: ByteArray?
) : Command {

    val TAG = this.javaClass.simpleName

    override fun executor(): ByteArray {
        Log.e("qz", "CallHistoryCommand execute...")
        var index = 0

        if (rawData?.size == 4) {
            index = MsgUtil.bytesToInt(rawData)
        }

        val callHistory = AndroidCallHistoryUtil.getCallHistoryByIndex(index, context)

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            callHistory.toXMLPropertyList().toByteArray()
        )
    }
}
