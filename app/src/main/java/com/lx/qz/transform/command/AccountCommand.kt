package com.lx.qz.transform.command

import android.accounts.AccountManager
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.lx.qz.transform.response.AndroidAccountUtil
import com.lx.qz.utils.MsgUtil


class AccountCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {
    override fun executor(): ByteArray {

        AndroidAccountUtil.getAccountInfo(context)


        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            ByteArray(4)
        )
    }
}