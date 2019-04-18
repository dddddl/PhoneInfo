package com.dl.forensics.utils

import android.Manifest
import com.dl.forensics.SystemDataService.RemoteConnectionService
import com.dl.forensics.SystemDataService.SystemDataServiceNoticeActivity
import com.dl.forensics.transform.MessageException

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
                        MessageException.PackageUsageStatsPermissionGrantedError
                    }
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION -> {
                        MessageException.PackageUsageStatsPermissionGrantedError
                    }
                    else -> {
                        MessageException.DataError
                    }
                }

                throw MessageException(errorCode)
            }
        }
    }

//    fun requestPackagePermission(permission: String){
//        var ret = RemoteConnectionService.permissionDelegate?.requestPackagePermission(permission)
//    }

}
