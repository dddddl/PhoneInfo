package com.lx.qz.transform.command

import android.content.Context
import com.lx.qz.transform.response.BaiDuLocationUtil
import com.lx.qz.utils.MsgUtil

class LocationCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    override fun executor(): ByteArray {

//        AndroidLocationUtil.getLocation(context)
        val location = BaiDuLocationUtil.getLocation(context)

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            location.toXMLPropertyList().toByteArray()
        )
    }
}