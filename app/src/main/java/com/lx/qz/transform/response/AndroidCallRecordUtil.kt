package com.lx.antifraud.transform.response

import android.os.Environment
import com.dd.plist.NSDictionary
import java.io.File


object AndroidCallRecordUtil {

    private lateinit var accountArray: java.util.ArrayList<NSDictionary>
    private var pathScan: ArrayList<String> = arrayListOf(
        "/Voice Recorder/",
        "/Call/",
        "/Sounds/",
        "/sound_recorder/",
        "/Recordings/",
        "/smartisan/Recorder/",
        "/360OS/My Records/",
        "/Record/",
        "/Recorder/",
        "/录音/",
        "/MIUI/sound_recorder/"
    )

    private var stuffMap = HashMap<String, Boolean>()

    private fun initStuff() {
        val localBoolean = true
        stuffMap[".m4a"] = localBoolean
        stuffMap[".mp3"] = localBoolean
        stuffMap[".aac"] = localBoolean
        stuffMap[".wav"] = localBoolean
        stuffMap[".amr"] = localBoolean
        stuffMap[".ogg"] = localBoolean
    }

    fun getCallRecordInfo(): NSDictionary {

        val root = NSDictionary()
        accountArray = ArrayList<NSDictionary>()
        initStuff()
        getFolder()
        root.put("callRecord", accountArray)
        return root
    }


    private fun getFolder() {
        val path = Environment.getExternalStorageState()

        pathScan.forEach {
            val file = File(path + it)
            if (file.exists() && file.listFiles() != null) {
                getFiles(path + it)
            }
        }
    }

    private fun getFiles(path: String) {
        val files = File(path).listFiles()
        files.forEach {
            if (it.isFile) {
                val name = it.name
                val stuff = name.substring(name.lastIndexOf(".")).toLowerCase()
                if (stuffMap.containsKey(stuff)) {
                    val configNS = NSDictionary()
                    configNS.put("name", it.name)
                    configNS.put("path", it.absolutePath)
                    configNS.put("size", it.length())
                    configNS.put("time", it.lastModified())
                    accountArray.add(configNS)
                }
            } else {
                getFiles(it.absolutePath)
            }
        }
    }

}