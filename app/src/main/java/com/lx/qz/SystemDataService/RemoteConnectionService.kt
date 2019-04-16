package com.lx.qz.SystemDataService

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.lx.qz.LXApplication
import com.lx.qz.R
import com.lx.qz.server.TCPServer

class RemoteConnectionService : Service() {
    val TAG = "Service"

    companion object {
        val MsgSocketDisconnect = 255
        var permissionDelegate: requestRuntimePermissionDelegate? = null
    }

    override fun onBind(intent: Intent): IBinder? {
        return Binder()
    }

    inner class Binder : android.os.Binder() {
        val myService: RemoteConnectionService
            get() = this@RemoteConnectionService
    }

    fun setRequestPermissionDelegate(delegate: requestRuntimePermissionDelegate) {
        Log.i(TAG, "setRequestPermissionDelegate")
        permissionDelegate = delegate
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "RemoteConnectionService onStartCommand")
        var actIntent = Intent(this, SystemDataServiceNoticeActivity::class.java)
        actIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(actIntent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        setServiceForegroud()
        Thread(TCPServer(applicationContext)).start()
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        super.onDestroy()
        stopForeground(1337)
        Log.i(TAG, "RemoteConnectionService OnDestroy")
    }

    private fun setServiceForegroud() {
        val notificationIntent = Intent(this, SystemDataServiceNoticeActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(this, LXApplication.NOTIFICATION_CHANNEL_ID_SERVICE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("正在扫描数据")
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(pendingIntent).build()

        startForeground(1337, notification)
    }
}
