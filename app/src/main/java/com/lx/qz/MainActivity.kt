package com.lx.qz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.lx.qz.SystemDataService.RemoteConnectionService
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_main.*
import java.util.HashMap
import java.util.concurrent.Semaphore

class MainActivity : AppCompatActivity(), requestRuntimePermissionDelegate {


    companion object {
        val permissionStatusWait = 0
        val permissionStatusOK = 1
        val permissionStatusForbidden = 2

        val MsgSocketDisconnect = 255
        var permissionDelegate: requestRuntimePermissionDelegate? = null

        val TAG = "MainActivity"
    }

    val permissionMaptable: HashMap<String, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionDelegate = this

        startService(Intent(this, RemoteConnectionService::class.java))

        start.setOnClickListener {
            Log.e("", "client start")
            Thread(NIOClient()).start()
        }
    }


    override fun requestRuntimePermission(permission: String): Int {
        val mutex = Semaphore(0)
        runOnUiThread {
            if (AndPermission.hasPermissions(this, permission)) {
                permissionMaptable[permission] = permissionStatusOK
            } else {
                if (permissionMaptable[permission] == null) {
                    permissionMaptable[permission] = permissionStatusWait
                }
                AndPermission.with(this)
                    .runtime()
                    .permission(permission)
                    .onGranted {
                        permissionMaptable[permission] = permissionStatusOK
                    }
                    .onDenied {
                        permissionMaptable[permission] = permissionStatusForbidden
                    }
                    .start()
            }
            mutex.release()
        }
        try {
            mutex.acquire()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Log.e(TAG, "requestRuntimePermission${permissionMaptable[permission]!!}")
        return permissionMaptable[permission]!!
    }
}

interface requestRuntimePermissionDelegate {

    fun requestRuntimePermission(permission: String): Int

}
