package com.lx.qz.transform.command

import com.lx.qz.transform.response.AndroidQQVoiceUtil
import com.lx.qz.utils.MsgUtil

class QQVoiceCommand(
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    override fun executor(): ByteArray {

        val qqVoiceInfo = AndroidQQVoiceUtil.getQQVoiceInfo()

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            qqVoiceInfo.toXMLPropertyList().toByteArray()
        )
    }
}