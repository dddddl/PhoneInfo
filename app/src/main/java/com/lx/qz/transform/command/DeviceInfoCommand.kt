package com.lx.qz.transform.command

import android.content.Context
import com.lx.qz.utils.MsgUtil
import com.lx.qz.transform.response.AndroidDeviceInfoUtil

class DeviceInfoCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {

    override fun executor(): ByteArray {

        val deviceInfo = AndroidDeviceInfoUtil.getDeviceInfo(context)

        return MsgUtil.envelopedData(true, commandGroup, commandOperation, deviceInfo.toXMLPropertyList().toByteArray())
    }

}