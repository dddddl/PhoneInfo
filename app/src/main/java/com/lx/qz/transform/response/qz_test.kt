package com.lx.qz.transform.response

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log

import com.lx.qz.utils.LogHelper
import java.util.*

object test {
    private val TAG = "test"
    private var threadIdList = ArrayList<Long>()
    fun sms_test(context: Context) : ArrayList<Long>{
        threadIdList.clear()
        sms(context)
        mms(context)
        return threadIdList
    }

    fun sms(context: Context) {
        val uri = Uri.parse("content://sms")
        val resolver = context.contentResolver
        var cursor : Cursor?=null
        try {
            cursor = resolver.query(uri, null, null, null, "date desc")

            while (cursor != null && cursor.moveToNext()) {

                val thread_id = cursor.getString(cursor.getColumnIndex("thread_id"))
                val id = cursor.getString(cursor.getColumnIndex("_id"))
                Log.e("qz", "cursor sms id : $id     thread_id: $thread_id")
                if (!threadIdList.contains(thread_id.toLong())){
                    threadIdList.add(thread_id.toLong())
                }
            }
        } catch (ex: Exception) {
            LogHelper.getInstance().saveLog("", "test::sms3...$ex")
        } finally {
            Log.e("qz", "threadIdList size : ${threadIdList.size}")
            Log.e("qz", "cursor sms size : ${cursor?.count}")
            cursor?.close()
        }
    }

    fun mms(context: Context) {
        val uri = Uri.parse("content://mms")
        val resolver = context.contentResolver

        LogHelper.getInstance().saveLog("", "test::mms0...\n")
        var cursor : Cursor? = null
        try {
             cursor = resolver.query(uri, null, null, null, "date desc")
            while (cursor != null && cursor.moveToNext()) {

                val thread_id = cursor.getString(cursor.getColumnIndex("thread_id"))
                if (!threadIdList.contains(thread_id.toLong())){
                    threadIdList.add(thread_id.toLong())
                }
            }
        } catch (ex: Exception) {
            LogHelper.getInstance().saveLog("", "test::mms3...$ex")
        }finally {
            cursor?.close()
            Log.e("qz", "threadIdList size : ${threadIdList.size}")
        }
    }

}
