package com.lx.qz.transform.command

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import com.lx.qz.utils.MsgUtil
import com.lx.qz.transform.response.AndroidDeviceInfoUtil
import com.lx.qz.utils.LogHelper
import com.lx.qz.utils.RequestPermissionUtils

class DeviceInfoCommand(
    private val context: Context,
    private val commandGroup: Int,
    private val commandOperation: Int
) : Command {

    val TAG = this.javaClass.simpleName

    override fun executor(): ByteArray {
        Log.e("qz", "DeviceInfoCommand execute...")
        Thread.sleep(50)
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Thread.sleep(50)
        LogHelper.getInstance().saveLog(TAG, "DeviceInfoCommand execute...")
        Thread.sleep(50)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_CALL_LOG)
        }
        Thread.sleep(50)
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_PHONE_STATE)
        Thread.sleep(50)
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_CONTACTS)
        Thread.sleep(50)
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_SMS)
        Thread.sleep(50)
//        RequestPermissionUtils.requestPackagePermission(Manifest.permission.PACKAGE_USAGE_STATS)
//        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.GET_ACCOUNTS)
//        Thread.sleep(10)
        val deviceInfo = AndroidDeviceInfoUtil.getDeviceInfo(context)
        LogHelper.getInstance().saveLog(TAG, " get deviceInfo finish")
        return MsgUtil.envelopedData(
            true,
            commandGroup,
            commandOperation,
            deviceInfo.toXMLPropertyList().toByteArray()
        )
    }

}
