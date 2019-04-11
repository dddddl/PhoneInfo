package com.lx.qz.utils

import android.Manifest
import com.lx.qz.SystemDataService.RemoteConnectionService
import com.lx.qz.SystemDataService.SystemDataServiceNoticeActivity
import com.lx.qz.transform.MessageException

object RequestPermissionUtils {

    private val TAG = RequestPermissionUtils.javaClass.simpleName

    fun requestRuntimePermission(permission: String) {
        var ret = RemoteConnectionService.permissionDelegate?.requestRuntimePermission(permission)
        when (ret) {
            SystemDataServiceNoticeActivity.permissionStatusWait -> {
                throw MessageException(MessageException.AskPermissionWaitForUser)
            }
            SystemDataServiceNoticeActivity.permissionStatusForbidden -> {
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
