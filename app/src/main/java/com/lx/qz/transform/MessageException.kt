package com.lx.qz.transform

class MessageException(var errorCode: Int = 0, var errorMsg : String = "") : Throwable()
{
    companion object {
        val DataError = 1
        val SocketError = 2
        val HeaderPrefixCheckError = 3
        val ContactPermissionGrantedError = 4
        val TextMessagePermissionGrantedError = 5
        val CallHistoryPermissionGrantedError = 6

        val ReadPhoneStatePermissionGrantedError = 7
        //        val AccessCoarseLocationPermissionGrantedError = 8
//        val AccessFineLocationPermissionGrantedError = 9
        val WriteExternalStoragePermissionGrantedError = 10
        val ReadExternalStoragePermissionGrantedError = 11

        //        val AskPermissionWaitForUser = 7
        val AskPermissionWaitForUser = 12

        val PackageUsageStatsPermissionGrantedError = 13
    }
}