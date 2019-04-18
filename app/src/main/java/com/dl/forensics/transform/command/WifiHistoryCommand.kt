package com.dl.forensics.transform.command

import android.content.Context
import com.dl.forensics.transform.response.AndroidWifiHistoryUtil
import com.dl.forensics.utils.LogHelper
import com.dl.forensics.utils.MsgUtil

class WifiHistoryCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {

    override fun executor(): ByteArray {

        LogHelper.getInstance().saveLog("qz", "WifiHistoryCommand execute...")

        val wifiHistory = AndroidWifiHistoryUtil.getWifiHistory(context)

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            wifiHistory.toXMLPropertyList().toByteArray()
        )
    }
}