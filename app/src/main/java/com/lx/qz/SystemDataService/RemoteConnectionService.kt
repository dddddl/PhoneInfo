package com.lx.qz.SystemDataService

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.lx.qz.server.TCPServer

class RemoteConnectionService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Thread(TCPServer(applicationContext)).start()
    }
}
