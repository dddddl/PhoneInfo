package com.lx.qz.utils

import android.Manifest
import com.lx.qz.MainActivity
import com.lx.qz.transform.MessageException

object RequestPermissionUtils {

    fun requestRuntimePermission(permission: String) {
        var ret = MainActivity.permissionDelegate?.requestRuntimePermission(permission)
        when (ret) {
            MainActivity.permissionStatusWait -> {
                LogHelper.getInstance().saveLog("MessageParser throw MessageException 1...\n")
                throw MessageException(MessageException.AskPermissionWaitForUser)
            }
            MainActivity.permissionStatusForbidden -> {
                val errorCode = when (permission) {
                    Manifest.permission.READ_CONTACTS -> {
                        MessageException.ContactPermissionGrantedError
                    }
                    Manifest.permission.READ_SMS -> {
                        MessageException.TextMessagePermissionGrantedError
                    }
                    Manifest.permission.READ_CALL_LOG -> {
                        MessageException.CallHistoryPermissionGrantedError
                    }
                    Manifest.permission.READ_PHONE_STATE -> {
                        MessageException.ReadPhoneStatePermissionGrantedError
                    }
                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        MessageException.WriteExternalStoragePermissionGrantedError
                    }
                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                        MessageException.ReadExternalStoragePermissionGrantedError
                    }
                    Manifest.permission.PACKAGE_USAGE_STATS -> {
                        MessageException.ContactPermissionGrantedError  //todo package error
                    }
                    else -> {
                        MessageException.DataError
                    }
                }

                throw MessageException(errorCode)
            }
        }
    }

}
