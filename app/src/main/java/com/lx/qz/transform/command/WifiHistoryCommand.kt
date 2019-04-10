package com.lx.qz.transform.command

import android.content.Context
import com.lx.qz.utils.MsgUtil
import com.lx.qz.utils.WifiUtil

class WifiHistoryCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    override fun executor(): ByteArray {

        WifiUtil.getWifiInfo(context)

        return MsgUtil.envelopedData(true, commandGroup, commandOperation, ByteArray(1))
    }
}