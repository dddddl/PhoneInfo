package com.dl.forensics.transform.command

import android.content.Context
import com.dl.forensics.transform.response.AndroidAccountUtil
import com.dl.forensics.utils.MsgUtil


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