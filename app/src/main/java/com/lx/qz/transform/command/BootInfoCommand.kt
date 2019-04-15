package com.lx.qz.transform.command

import com.lx.qz.utils.BootUtil
import com.lx.qz.utils.MsgUtil

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