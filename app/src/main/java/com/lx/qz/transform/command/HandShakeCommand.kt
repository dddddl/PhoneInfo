package com.lx.qz.transform.command

import com.lx.qz.utils.MsgUtil

class HandShakeCommand(private val commandGroup: Int, private val commandOperation: Int) : Command {

    override fun executor(): ByteArray {
        return MsgUtil.envelopedData(true, commandGroup, commandOperation, null)
    }
}
