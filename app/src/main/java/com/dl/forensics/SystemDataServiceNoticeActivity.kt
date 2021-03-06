package com.dl.forensics

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.dl.forensics.R
import com.yanzhenjie.permission.AndPermission
import java.util.*
import java.util.concurrent.Semaphore

class SystemDataServiceNoticeActivity : AppCompatActivity(), requestRuntimePermissionDelegate, ServiceConnection {

    companion object {
        val permissionStatusWait = 0
        val permissionStatusOK = 1
        val permissionStatusForbidden = 2
        val TAG = "SystemDataServiceNoticeActivity"
    }

    val permissionMaptable: HashMap<String, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, RemoteConnectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(intent, this, Context.BIND_AUTO_CREATE)

//        start.setOnClickListener {
//            LogHelper.getInstance().saveLog(TAG, "client start")
//            Thread(NIOClient()).start()
//            AndroidLocationUtil.getLocation(this)
//            Thread{
//                FileUtils.createFolder("sdcard/qz/account/")
//                FileUtils.createFolder("sdcard/qz/packages/")
//                FileUtils.createFolder("sdcard/qz/wifi/")
//                FileUtils.createFolder("sdcard/qz/location/")
//                FileUtils.createFolder("sdcard/qz/bluetooth/")
//                val account = AndroidAccountUtil.getAccountInfo(this)
//                PropertyListParser.saveAsXML(account, File("sdcard/qz/account/0.plist"))
//                Log.e("qz", "account execute ...")
//                val packages = AndroidPackageUtil.getPackagesInfo(this)
//                PropertyListParser.saveAsXML(packages, File("sdcard/qz/packages/0.plist"))
//                Log.e("qz", "packages execute ...")
//                val wifi = AndroidWifiHistoryUtil.getWifiHistory(this)
//                PropertyListParser.saveAsXML(wifi, File("sdcard/qz/wifi/0.plist"))
//                Log.e("qz", "wifi execute ...")
//                val location = BaiduLocationUtil.getLocation(this)
//                PropertyListParser.saveAsXML(location, File("sdcard/qz/location/0.plist"))
//                Log.e("qz", "location execute ...")
//                val bluetooth = AndroidBluetoothUtil.getBluetooth()
//                PropertyListParser.saveAsXML(bluetooth, File("sdcard/qz/bluetooth/0.plist"))
//                Log.e("qz", "bluetooth execute ...")
//            }.start()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {

    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        if (binder is RemoteConnectionService.Binder) {
            val service = binder.myService
            service.setRequestPermissionDelegate(this)
        }
    }


    override fun requestRuntimePermission(permission: String): Int {
        val mutex = Semaphore(0)
        runOnUiThread {
            if (AndPermission.hasPermissions(this, permission)) {
                permissionMaptable[permission] = permissionStatusOK
                mutex.release()
            } else {
                if (permissionMaptable[permission] == null) {
                    permissionMaptable[permission] =
                        permissionStatusWait
                }
                AndPermission.with(this)
                    .runtime()
                    .permission(permission)
                    .onGranted {
                        permissionMaptable[permission] =
                            permissionStatusOK
                        mutex.release()
                    }
                    .onDenied {
                        if (TextUtils.equals(permission, "android.permission.PACKAGE_USAGE_STATS")) {

                        } else {
                            permissionMaptable[permission] =
                                permissionStatusForbidden
                            mutex.release()
                        }
                    }
                    .start()
            }
        }
        try {
            mutex.acquire()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
//        LogHelper.m_instance.saveLog(TAG, "requestRuntimePermission${permissionMaptable[permission]!!}")
        return permissionMaptable[permission]!!
    }

//    override fun requestPackagePermission(permission: String): Int {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val checkUsage = checkUsageStats(this)
//            if (checkUsage) {
//                permissionMaptable[permission] = permissionStatusOK
//            } else {
//                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                startActivity(intent)
//            }
//        } else {
//            permissionMaptable[permission] = permissionStatusOK
//        }
//        return permissionMaptable[permission]!!
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private fun checkUsageStats(context: Context): Boolean {
//        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        val mode =
//            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
//
//        return if (mode == AppOpsManager.MODE_DEFAULT) {
//            context.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
//        } else {
//            mode == AppOpsManager.MODE_ALLOWED
//        }
//    }
}

interface requestRuntimePermissionDelegate {

    fun requestRuntimePermission(permission: String): Int

//    fun requestPackagePermission(permission: String): Int

}
