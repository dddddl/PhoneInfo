package com.lx.qz.transform.response

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.CallLog
import android.util.Log
import com.dd.plist.NSDictionary
import com.lx.qz.transform.MessageException
import com.lx.qz.utils.LogHelper
import com.lx.qz.utils.RequestPermissionUtils
import java.util.*

/**
 * Created by Travis on 2018/3/9.
 */
object AndroidCallHistoryUtil {
    val TAG = "CallHis"
    private val callHistoryArray: ArrayList<Long> = ArrayList()


    @SuppressLint("MissingPermission")
    open fun getCallHistoryCount(context: Context): Int {
        LogHelper.getInstance().saveLog(TAG,"开始获取通话记录总数...\n")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_CALL_LOG)
        }
        callHistoryArray.clear()
        try {
            val cursor =
                context.contentResolver.query(CallLog.Calls.CONTENT_URI, arrayOf(CallLog.Calls._ID), null, null, null)
            while (cursor != null && cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID))
                callHistoryArray.add(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogHelper.getInstance().saveLog(TAG,"AndroidCallHistoryUtil throw MessageException...\n")
            throw MessageException(MessageException.CallHistoryPermissionGrantedError)
        }
        LogHelper.getInstance().saveLog(TAG,"通话记录总数===>${callHistoryArray.size}\n")
        return callHistoryArray.size
    }

    @SuppressLint("MissingPermission")
    open fun getCallHistoryByIndex(index: Int, context: Context): NSDictionary {
        val root = NSDictionary()
        if (callHistoryArray.size == 0) {
            getCallHistoryCount(context)
        }

        if (index < 0 || index >= callHistoryArray.size) {
            return root
        }
        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                "_id = ${callHistoryArray[index]}",
                null,
                null
            )
            while (cursor != null && cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID))
                val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                val date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))
                val duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
                val type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))
                val name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))

                LogHelper.getInstance()
                    .saveLog(TAG,"通话记录内容：id=$id, number=$number, date=$date, duration=$duration, type=$type, name=$name\n")

                Log.d(TAG, "id:$id")
                Log.d(TAG, "number:$number")
                Log.d(TAG, "date:$date")
                Log.d(TAG, "duration:$duration")
                Log.d(TAG, "type:$type")
                Log.d(TAG, "name:$name")

                root.put("number", number)
                root.put("name", name)
                root.put("date", date)
                root.put("duration", duration)
                root.put("type", type) //1.incoming 2. dial out 3. miss 5. reject
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //Log.d(TAG, "root:${root.toXMLPropertyList()}")
        return root
    }
}