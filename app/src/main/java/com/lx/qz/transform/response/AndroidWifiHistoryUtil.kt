package com.lx.qz.transform.response

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.dd.plist.NSDictionary
import com.google.gson.Gson
import com.lx.qz.transform.bean.WifiBean
import java.util.*
import kotlin.collections.ArrayList

object AndroidWifiHistoryUtil {

    fun getWifiHistory(context: Context): NSDictionary {

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val configuredNetworks = wifiManager.configuredNetworks

        val root = NSDictionary()
        val wifiArray = ArrayList<NSDictionary>()

        if (configuredNetworks != null && !configuredNetworks.isEmpty()) {
            for (configuredNetwork in configuredNetworks) {

                val configNS = NSDictionary()
                val configuredNetworkStr = Gson().toJson(configuredNetwork)
                val wifiBean = Gson().fromJson<WifiBean>(configuredNetworkStr, WifiBean::class.java)
                configNS.put("SSID", wifiBean.ssid)
                configNS.put("AuthType", getAuthType(configuredNetwork.allowedKeyManagement))
                configNS.put("HasEverConnected", wifiBean.mNetworkSelectionStatus.isMHasEverConnected)

                wifiArray.add(configNS)
            }
            root.put("configuredNetworks",wifiArray)
        }

        return root
    }

    private fun getAuthType(allowedKeyManagement: BitSet): String {
        if (allowedKeyManagement.cardinality() > 1) {
            throw IllegalStateException("More than one auth type set")
        }
        return when {
            allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK) -> "WPA_PSK"
            allowedKeyManagement.get(4) -> "WPA2_PSK"
            allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) -> "WPA_EAP"
            allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X) -> "IEEE8021X"
            else -> "NONE"
        }
    }

}