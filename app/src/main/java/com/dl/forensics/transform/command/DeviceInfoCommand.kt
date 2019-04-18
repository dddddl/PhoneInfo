package com.dl.forensics.transform.command

import android.Manifest
import android.content.Context
import android.util.Log
import com.dl.forensics.utils.MsgUtil
import com.dl.forensics.transform.response.AndroidDeviceInfoUtil
import com.dl.forensics.utils.RequestPermissionUtils

class DeviceInfoCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {

    val TAG = this.javaClass.simpleName

    override fun executor(): ByteArray {
        Log.e("qz", "DeviceInfoCommand execute...")

        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_PHONE_STATE)
        Thread.sleep(10)
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Thread.sleep(10)
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_CONTACTS)
        Thread.sleep(10)
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_SMS)
        Thread.sleep(10)
//        RequestPermissionUtils.requestPackagePermission(Manifest.permission.PACKAGE_USAGE_STATS)
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.GET_ACCOUNTS)

        val deviceInfo = AndroidDeviceInfoUtil.getDeviceInfo(context)

        return MsgUtil.envelopedData(true, commandGroup, commandOperation, deviceInfo.toXMLPropertyList().toByteArray())
    }

}
