package com.lx.qz.transform.command

import android.content.Context
import android.util.Log
import com.lx.qz.transform.response.AndroidWifiHistoryUtil
import com.lx.qz.utils.LogHelper
import com.lx.qz.utils.MsgUtil

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