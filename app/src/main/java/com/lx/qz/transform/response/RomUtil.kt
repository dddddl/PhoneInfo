package com.lx.qz.transform.response

import android.os.Build
import android.text.TextUtils

object RomUtil {

    val MANUFACTURER_XIAOMI = "Xiaomi"
    val device = Build.MANUFACTURER

    fun isMiUI() = MANUFACTURER_XIAOMI.equals(device, true)

}