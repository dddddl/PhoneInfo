package com.dl.forensics.transform.response

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.dd.plist.NSDictionary
import com.dl.forensics.transform.bean.DeviceInfo
import com.dl.forensics.utils.DeviceInfoUtil
import com.dl.forensics.utils.SimUtils

/**
 * Created by Travis on 2018/3/9.
 */
object AndroidDeviceInfoUtil {

    fun getDeviceInfo(context: Context): NSDictionary {
        Log.e("qz","executor android device info")
        val root = NSDictionary()

        var imei1 = SimUtils.getSimImei(context, 0)
        var imei2 = SimUtils.getSimImei(context, 1)
        var phoneNumber1 = SimUtils.getSimPhonenumber(context, 0)
        var phoneNumber2 = SimUtils.getSimPhonenumber(context, 1)

        var deviceInfo: DeviceInfo? =
            DeviceInfo()
        if (!TextUtils.isEmpty(imei1)) {
            deviceInfo?.imei1 = imei1;
        } else {
            deviceInfo?.imei1 = "未知";
        }
        if (!TextUtils.isEmpty(imei2)) {
            deviceInfo?.imei2 = imei2;
        } else {
            deviceInfo?.imei2 = "未知";
        }
        deviceInfo?.meid = DeviceInfoUtil.getMEID(context)
        deviceInfo?.basebandVersion = DeviceInfoUtil.getBasebandVersion()
        deviceInfo?.bluetoothAddress = DeviceInfoUtil.getBtAddressByReflection()
        deviceInfo?.macAddress = DeviceInfoUtil.getMacAddressFromIp(context)
        deviceInfo?.timeZone = DeviceInfoUtil.getTimeZone()
        deviceInfo?.timeZoneId = DeviceInfoUtil.getTimeZoneId()
        deviceInfo?.sdCardInfo = DeviceInfoUtil.getStoragePath(context, true)
        deviceInfo?.defaultSDCardInfo = DeviceInfoUtil.getStoragePath(context, false)
        deviceInfo?.cpuABI = DeviceInfoUtil.getCPUABI()
        deviceInfo?.board = DeviceInfoUtil.getBoard()
        deviceInfo?.hardware = DeviceInfoUtil.getHardWare()
        deviceInfo?.androidId = DeviceInfoUtil.getAndroidId(context)
        deviceInfo?.totalUserSpace = DeviceInfoUtil.getTotalUserSpace(context)
        deviceInfo?.totalAvailableUserSpace = DeviceInfoUtil.getTotalAvailableUserSpace(context)
        deviceInfo?.totalInternalSpace = DeviceInfoUtil.getInternalToatalSpace(context)
        deviceInfo?.totalAvailableInternalSpace = DeviceInfoUtil.getAvailableInternalMemorySize(context)
        deviceInfo?.serialNumber = DeviceInfoUtil.getSerialNumber()
        deviceInfo?.operator = DeviceInfoUtil.getProvidersName(context)
        deviceInfo?.imsi = DeviceInfoUtil.getIMSI(context)
        if (!TextUtils.isEmpty(phoneNumber1)) {
            deviceInfo?.phoneNumber1 = phoneNumber1;
        } else {
            deviceInfo?.phoneNumber1 = "未知";
        }
        if (!TextUtils.isEmpty(phoneNumber2)) {
            deviceInfo?.phoneNumber2 = phoneNumber2;
        } else {
            deviceInfo?.phoneNumber2 = "未知";
        }

        deviceInfo?.iccid = DeviceInfoUtil.getIccid(context)
        deviceInfo?.brand = DeviceInfoUtil.getBrand()
        deviceInfo?.model = DeviceInfoUtil.getModel()
        deviceInfo?.ip = DeviceInfoUtil.getIPAddress(context)
        deviceInfo?.kernelVersion = DeviceInfoUtil.getKernelVersion()

        root.put("imei1", deviceInfo?.imei1)
        root.put("imei2", deviceInfo?.imei2)
        root.put("meid", deviceInfo?.meid)
        root.put("basebandVersion", deviceInfo?.basebandVersion)
        root.put("bluetoothAddress", deviceInfo?.bluetoothAddress)
        root.put("macAddress", deviceInfo?.macAddress)
        root.put("timeZone", deviceInfo?.timeZone)
        root.put("timeZoneId", deviceInfo?.timeZoneId)
        root.put("sdCardInfo", deviceInfo?.sdCardInfo)
        root.put("defaultSDCardInfo", deviceInfo?.defaultSDCardInfo)
        root.put("cpuABI", deviceInfo?.cpuABI)
        root.put("board", deviceInfo?.board)
        root.put("hardware", deviceInfo?.hardware)
        root.put("androidId", deviceInfo?.androidId)
        root.put("totalUserSpace", deviceInfo?.totalUserSpace)
        root.put("totalAvailableUserSpace", deviceInfo?.totalAvailableUserSpace)
        root.put("totalInternalSpace", deviceInfo?.totalInternalSpace)
        root.put("totalAvailableInternalSpace", deviceInfo?.totalAvailableInternalSpace)
        root.put("serialNumber", deviceInfo?.serialNumber)
        root.put("phoneNumber1", deviceInfo?.phoneNumber1)
        root.put("phoneNumber2", deviceInfo?.phoneNumber2)
        root.put("iccid", deviceInfo?.iccid)
        root.put("brand", deviceInfo?.brand)
        root.put("model", deviceInfo?.model)
        root.put("ip", deviceInfo?.ip)
        root.put("kernelVersion", deviceInfo?.kernelVersion)
        root.put("operator", deviceInfo?.operator)
        root.put("imsi", deviceInfo?.imsi)
        return root
    }
}