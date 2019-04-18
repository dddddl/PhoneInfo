package com.dl.forensics.transform.command

import android.content.Context
import com.dl.forensics.transform.response.BaiduLocationUtil
import com.dl.forensics.utils.MsgUtil

class LocationCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    override fun executor(): ByteArray {

//        AndroidLocationUtil.getLocation(context)
        val location = BaiduLocationUtil.getLocation(context)

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            location.toXMLPropertyList().toByteArray()
        )
    }
}