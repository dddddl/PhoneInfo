package com.lx.qz.transform.command

import android.content.Context
import com.lx.qz.transform.response.AndroidAccountUtil
import com.lx.qz.utils.MsgUtil


class AccountCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    override fun executor(): ByteArray {

        val account = AndroidAccountUtil.getAccountInfo(context)

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            account.toXMLPropertyList().toByteArray()
        )
    }
}