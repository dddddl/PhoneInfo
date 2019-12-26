package com.lx.qz.transform.command

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.lx.qz.SystemDataService.SystemDataServiceNoticeActivity
import com.lx.qz.transform.response.AndroidTextMessageUtilNew
import com.lx.qz.utils.LogHelper
import com.lx.qz.utils.MsgUtil

class MessageTextCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int,
    private val rawData: ByteArray
) : Command {

    val TAG = this.javaClass.simpleName

    override fun executor(): ByteArray {

        Log.e("qz", "MessageTextCommand execute...")
        LogHelper.getInstance().saveLog(javaClass.simpleName, "MessageTextCommand execute...")

        var index = 0
        if (rawData?.size == 4) {
            index = MsgUtil.bytesToInt(rawData)
        }
        LogHelper.getInstance().saveLog(javaClass.simpleName, " message index $index")
        val textMessage = AndroidTextMessageUtilNew.getTextMessageThreadByIndex(index, context)
        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            textMessage.toXMLPropertyList().toByteArray()
        )
    }
}
