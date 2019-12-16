package com.lx.qz.transform.command

import com.lx.antifraud.transform.response.AndroidCallRecordUtil
import com.lx.qz.utils.MsgUtil

class CallRecordCommand(
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    override fun executor(): ByteArray {

        val callRecordInfo = AndroidCallRecordUtil.getCallRecordInfo()

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            callRecordInfo.toXMLPropertyList().toByteArray()
        )
    }
}