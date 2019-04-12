package com.lx.qz.transform.command

import android.content.Context
import com.lx.qz.transform.response.AndroidPackageUtil
import com.lx.qz.utils.MsgUtil

class PackageInfoCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {

    override fun executor(): ByteArray {

        val packages = AndroidPackageUtil.getPackagesInfo(context)

        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            packages.toXMLPropertyList().toByteArray()
        )

    }
}