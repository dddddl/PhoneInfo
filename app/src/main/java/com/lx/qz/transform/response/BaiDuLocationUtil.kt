package com.lx.qz.transform.response

import android.content.Context
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.dd.plist.NSDictionary

import java.lang.ref.WeakReference

object BaiDuLocationUtil {

    private val objects = Object()

    private val root = NSDictionary()

    @Synchronized
    fun getLocation(context: Context): NSDictionary {

        val mLocationClient = LocationClient(context.applicationContext)
        val weakLocation = WeakReference(mLocationClient)

        val option = LocationClientOption()

        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd0911")
        option.isOpenGps = true
        option.setScanSpan(0)
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.isOpenGps = true
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.isLocationNotify = true
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false)
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false)
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setEnableSimulateGps(false)
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        weakLocation.get()!!.setLocOption(option)
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

        weakLocation.get()!!.start()
        weakLocation.get()!!.registerLocationListener(object : BDAbstractLocationListener() {

            override fun onReceiveLocation(location: BDLocation) {
                weakLocation.get()!!.stop()
                val locationNS = NSDictionary()
                val latitude: Double = location.latitude
                val longitude: Double = location.longitude

                locationNS.put("latitude", latitude)//获取纬度信息
                locationNS.put("longitude", longitude)//获取经度信息
                root.put("location", locationNS)

                synchronized(objects) {
                    objects.notify()
                }
            }
        })
        synchronized(objects) {
            objects.wait()
        }

        return root
    }

}
