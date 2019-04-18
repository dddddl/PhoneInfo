package com.dl.forensics.transform.command

import android.util.Log
import com.dl.forensics.utils.MsgUtil

class HandShakeCommand(private val commandGroup: Int, private val commandOperation: Int) : Command {
    val TAG = HandShakeCommand::class.java.simpleName

    override fun executor(): ByteArray {
        Log.e("qz", "HandShakeCommand execute...")
        return MsgUtil.envelopedData(true, commandGroup, commandOperation, null)
    }
}
