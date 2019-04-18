package com.dl.forensics;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import com.dl.forensics.utils.SharedPreferencesUtil;

public class DlApplication extends Application {

    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = "com.lx.qz.SystemDataService.RemoteConnectionService";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandle.getInstance(getApplicationContext());
        initPrefs();
        initChannel();
    }

    /**
     * 初始化SharedPreference
     */
    private void initPrefs() {
        SharedPreferencesUtil.init(this,  "lx_preference", Context.MODE_MULTI_PROCESS);
    }

    public void initChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_DEFAULT));
//            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_INFO, "Download Info", NotificationManager.IMPORTANCE_DEFAULT));
        }
    }
}
