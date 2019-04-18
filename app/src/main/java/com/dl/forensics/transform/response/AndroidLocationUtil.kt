package com.dl.forensics.transform.response

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.dd.plist.NSDictionary
import com.dl.forensics.utils.RequestPermissionUtils
import java.io.IOException
import android.location.LocationManager
import android.os.Build
import android.location.Criteria


object AndroidLocationUtil {

    fun getLocation(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RequestPermissionUtils.requestRuntimePermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            RequestPermissionUtils.requestRuntimePermission(Manifest.permission.ACCESS_FINE_LOCATION)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }
        Log.e("qz", "定位")
        val root = NSDictionary()

        var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var locationProvider: String? = null


        // 查找到服务信息
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE // 高精度
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        criteria.powerRequirement = Criteria.POWER_LOW // 低功耗
        locationProvider = locationManager.getBestProvider(criteria, true)


        //获取所有可用的位置提供器
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null

        for (provider in providers) {
            val l = locationManager.getLastKnownLocation(provider) ?: continue
            if (l.accuracy < bestLocation!!.accuracy) {
                bestLocation = l
            }
        }

        Log.e("qz", "获取Location")
        //获取Location
//        val location = locationManager.getLastKnownLocation(locationProvider)
        if (bestLocation != null) {
            val latitude = bestLocation.latitude
            val longitude = bestLocation.longitude
            Log.e("qz", "latitude$latitude  longitude$longitude")
        }

        locationManager.requestLocationUpdates(locationProvider,
            1, 0f, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // 获取经纬度主要方法
                    val latitude = location.latitude
                    val longitude = location.longitude
                    var geocoder = Geocoder(context)
                    var addressList = ArrayList<Address>()

                    try {
                        // 返回集合对象泛型address
                        addressList = geocoder.getFromLocation(latitude, longitude, 1) as ArrayList<Address>
                        if (addressList.size > 0) {
                            val address = addressList.get(0)
//                        for (i in 0 until address.getMaxAddressLineIndex()) {
//                            sb.append(address.getAddressLine(i)).append("\n")
//                        }
//                        sb.append(address.getFeatureName())//周边地址
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    Log.e("qz", "onStatusChanged")
                }

                override fun onProviderEnabled(provider: String?) {
                    Log.e("qz", "onProviderEnabled")
                }

                override fun onProviderDisabled(provider: String?) {
                    Log.e("qz", "onProviderDisabled")
                }

            })

        return
    }

}