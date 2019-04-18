package com.dl.forensics.transform.response

import android.bluetooth.BluetoothAdapter
import android.util.SparseArray

import android.bluetooth.BluetoothClass.Device.Major.*
import com.dd.plist.NSDictionary


object AndroidBluetoothUtil {

    fun getBluetooth(): NSDictionary {

        val root = NSDictionary()
        val bluetoothArray = ArrayList<NSDictionary>()

        val adapter = BluetoothAdapter.getDefaultAdapter()
        val devices = adapter.bondedDevices
        if (devices != null) {
            val sparseArray = SparseArray<String>()
            addValue(sparseArray)
            for (device in devices) {
                val bluetooth = NSDictionary()
                bluetooth.put("type", sparseArray.get(device.bluetoothClass.majorDeviceClass))
                bluetooth.put("name", device.name)
                bluetooth.put("address", device.address)
                bluetoothArray.add(bluetooth)
            }
            sparseArray.clear()
        }
        root.put("bluetooth", bluetoothArray)
        return root
    }

    private fun addValue(sparseArray: SparseArray<String>) {
        sparseArray.put(MISC, "MISC")
        sparseArray.put(COMPUTER, "COMPUTER")
        sparseArray.put(PHONE, "PHONE")
        sparseArray.put(NETWORKING, "NETWORKING")
        sparseArray.put(AUDIO_VIDEO, "AUDIO_VIDEO")
        sparseArray.put(PERIPHERAL, "PERIPHERAL")
        sparseArray.put(IMAGING, "IMAGING")
        sparseArray.put(WEARABLE, "WEARABLE")
        sparseArray.put(TOY, "TOY")
        sparseArray.put(HEALTH, "HEALTH")
        sparseArray.put(UNCATEGORIZED, "UNCATEGORIZED")
    }

}
