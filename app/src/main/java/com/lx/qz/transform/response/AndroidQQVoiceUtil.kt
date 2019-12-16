package com.lx.qz.transform.response

import android.os.Environment
import com.dd.plist.NSDictionary
import java.io.File
import java.util.regex.Pattern

object AndroidQQVoiceUtil {

    private lateinit var voicesByNumber: java.util.ArrayList<NSDictionary>

    fun getQQVoiceInfo(): NSDictionary {
        val root = NSDictionary()
        val qqVoiceArray = getSlkFolder()
        root.put("qqVoice", qqVoiceArray)
        return root

    }

    private fun getSlkFolder(): ArrayList<NSDictionary> {
        val localArrayList = ArrayList<FolderInfo>()
        val qqVoiceArray = ArrayList<NSDictionary>()

        val file = File( "/sdcard/tencent/MobileQQ/")

        if (!file.exists()) {
            return qqVoiceArray
        }

        if (file.listFiles() == null) {
            return qqVoiceArray
        }

        file.listFiles().forEach {
            if (it.isDirectory && isAllNumer(it.name)) {
                localArrayList.add(FolderInfo(it.name, it.absolutePath))
            }
        }

        for (folderInfo in localArrayList) {
            val numberNS = NSDictionary()
            voicesByNumber = ArrayList<NSDictionary>()
            getSlkFiles(folderInfo.mFilePath)
            numberNS.put("qqnum", folderInfo.mFileName)
            numberNS.put("list", voicesByNumber)
            qqVoiceArray.add(numberNS)
        }
        return qqVoiceArray
    }

    fun isAllNumer(paramString: String): Boolean {
        return Pattern.compile("[0-9]*").matcher(paramString).matches()
    }

    private fun getSlkFiles(path: String) {
        val files = File(path).listFiles()
        files.forEach {
            if (it.isFile && (it.name.endsWith(".slk")
                        || it.name.endsWith(".amr")
                        || it.name.endsWith(".pcm"))
            ) {
                val configNS = NSDictionary()
                configNS.put("name", it.name)
                configNS.put("path", it.absolutePath)
                configNS.put("size", it.length().toString())
                configNS.put("time", it.lastModified().toString())
                voicesByNumber.add(configNS)
            } else if (it.isDirectory) {
                getSlkFiles(it.absolutePath)
            }
        }
    }

}

class FolderInfo(var mFileName: String, var mFilePath: String)