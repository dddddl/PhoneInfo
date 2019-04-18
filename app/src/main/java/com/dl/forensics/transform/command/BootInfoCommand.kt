package com.dl.forensics.transform.command

import com.dl.forensics.utils.BootUtil
import com.dl.forensics.utils.MsgUtil

class BootInfoCommand(
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {

    override fun executor(): ByteArray {

        val bootTime = BootUtil.getBootTime()

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            MsgUtil.intToBytes(bootTime)
        )
    }
}