package com.lx.qz.transform.response

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.*
import android.os.Build
import android.os.RemoteException
import android.os.storage.StorageManager
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.util.Log
import com.dd.plist.NSDictionary
import com.lx.qz.utils.FileSizeUtil
import com.lx.qz.utils.RequestPermissionUtils
import java.io.File

import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal

object AndroidPackageUtil {

    @Throws(PackageManager.NameNotFoundException::class)
    fun getPackagesInfo(context: Context): NSDictionary {

        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Thread.sleep(10)

        val root = NSDictionary()

        val packageManager = context.applicationContext.packageManager
        val packageInfos = packageManager.getInstalledPackages(0)

        for (i in packageInfos.indices) {

            val packageNS = NSDictionary()
            val packageInfo = packageInfos[i]

            val appName = packageManager.getApplicationLabel(packageInfo.applicationInfo) as String
            packageNS.put("appName", appName)
            val packageName = packageInfo.packageName
            packageNS.put("packageName", packageName)
            val versionName = packageInfo.versionName
            packageNS.put("versionName", versionName)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                val versionCode = packageInfo.longVersionCode
                packageNS.put("versionCode", versionCode)
            } else {
                val versionCode = packageInfo.versionCode
                packageNS.put("versionCode", versionCode)
            }
            val publicSourceDir = packageInfo.applicationInfo.publicSourceDir
            packageNS.put("publicSourceDir", publicSourceDir)
            val FLAG_SYSTEM = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0   //非系统应用为true
            packageNS.put("FLAG_SYSTEM", FLAG_SYSTEM)
            val size = FileSizeUtil.getAutoFolderOrFileSize(publicSourceDir)
            Log.e("qz", "size" + size)
            packageNS.put("size", size)
            val firstInstallTime = packageInfo.firstInstallTime
            packageNS.put("firstInstallTime", firstInstallTime)
            val lastUpdateTime = packageInfo.lastUpdateTime
            packageNS.put("lastUpdateTime", lastUpdateTime)
            val requestedPermissions: Array<String>? =
                packageManager.getPackageInfo(packageInfo.packageName, PackageManager.GET_PERMISSIONS)
                    .requestedPermissions
            packageNS.put("requestedPermissions", requestedPermissions)
            root.put("package", packageNS)
        }

        return root
    }

    private fun getAppTotalSize(context: Context, packageName: String, filePath: String) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val packageManager = context.applicationContext.packageManager
            try {
                val method = PackageManager::class.java.getMethod(
                    "getPackageSizeInfo",
                    String::class.java,
                    IPackageStatsObserver::class.java
                )
                method.invoke(packageManager, packageName, object : IPackageStatsObserver.Stub() {
                    @Throws(RemoteException::class)
                    override fun onGetStatsCompleted(pStats: PackageStats, succeeded: Boolean) {
                        val appSizeL = pStats.cacheSize + pStats.dataSize + pStats.codeSize
                        Log.e("test", appSizeL.toString())
                    }
                })
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

        } else {
            val cheackUsage = checkUsageStats(context)
            if (cheackUsage) {
                val storageStatsManager =
                    context.applicationContext.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                val storageManager =
                    context.applicationContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager

                val uuid = storageManager.getUuidForPath(File(filePath))
                try {
                    val storageStats = storageStatsManager.queryStatsForUid(uuid, getUid(context, packageName))
                    //获取到App的总大小
                    val appSizeL = storageStats.appBytes + storageStats.cacheBytes + storageStats.dataBytes
                    Log.e("cheackUsage", appSizeL.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun checkUsageStats(context: Context): Boolean {
        var granted = false
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted =
                context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        } else {
            granted = mode == AppOpsManager.MODE_ALLOWED
        }
        return granted
    }

    private fun getUid(context: Context, pakName: String): Int {
        val pm = context.packageManager
        try {
            val ai = pm.getApplicationInfo(pakName, PackageManager.GET_META_DATA)
            return ai.uid
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return -1
    }

}
