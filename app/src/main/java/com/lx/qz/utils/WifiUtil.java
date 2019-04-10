package com.lx.qz.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.BitSet;
import java.util.List;

public class WifiUtil {
    private static final String TAG = WifiInfo.class.getSimpleName();

    public static void getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null && !configuredNetworks.isEmpty()) {
            for (WifiConfiguration configuredNetwork : configuredNetworks) {

                Log.e(TAG, configuredNetwork.SSID);
                Log.e(TAG, "" + getAuthType(configuredNetwork.allowedKeyManagement));
                Log.e(TAG, configuredNetwork.toString());

            }
        }


    }

    private static int getAuthType(BitSet allowedKeyManagement) {
        if (allowedKeyManagement.cardinality() > 1) {
            throw new IllegalStateException("More than one auth type set");
        }
        if (allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WifiConfiguration.KeyMgmt.WPA_PSK;
        } else if (allowedKeyManagement.get(4)) {
            return 4;
        } else if (allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)) {
            return WifiConfiguration.KeyMgmt.WPA_EAP;
        } else if (allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return WifiConfiguration.KeyMgmt.IEEE8021X;
        }
        return WifiConfiguration.KeyMgmt.NONE;
    }
}
