package com.lx.qz.SystemDataService

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.lx.qz.NIOClient
import com.lx.qz.R
import com.lx.qz.utils.LogHelper
import com.lx.qz.utils.RequestPermissionUtils
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_main.*
import java.util.HashMap
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

        start.setOnClickListener {
            LogHelper.getInstance().saveLog(TAG, "client start")
            Thread(NIOClient()).start()
        }
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
                        permissionMaptable[permission] =
                            permissionStatusForbidden
                        mutex.release()
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
}

interface requestRuntimePermissionDelegate {

    fun requestRuntimePermission(permission: String): Int

}
