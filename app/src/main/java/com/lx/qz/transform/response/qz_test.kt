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
    private var threadIdList = ArrayList<Int>()
    fun sms_test(context: Context) : ArrayList<Int>{
        mms(context)
        sms(context)

        Collections.sort(threadIdList)
        return threadIdList
    }

    fun sms(context: Context) {
        val uri = Uri.parse("content://sms")
        val resolver = context.contentResolver

        try {
            val cursor = resolver.query(uri, null, null, null, "date desc")

            while (cursor != null && cursor.moveToNext()) {

                val thread_id = cursor.getString(cursor.getColumnIndex("thread_id"))
                if (!threadIdList.contains(thread_id.toInt())){
                    threadIdList.add(thread_id.toInt())
                }
                Log.e("qz", "threadIdList size : ${threadIdList.size}")
            }
        } catch (ex: Exception) {
            LogHelper.getInstance().saveLog("", "test::sms3...$ex")
        }



    }

    fun mms(context: Context) {
        val uri = Uri.parse("content://mms")
        val resolver = context.contentResolver

        LogHelper.getInstance().saveLog("", "test::mms0...\n")
        try {
            val cursor = resolver.query(uri, null, null, null, "date desc")
            while (cursor != null && cursor.moveToNext()) {

                val thread_id = cursor.getString(cursor.getColumnIndex("thread_id"))
                if (!threadIdList.contains(thread_id.toInt())){
                    threadIdList.add(thread_id.toInt())
                }
                Log.e("qz", "threadIdList size : ${threadIdList.size}")
            }
        } catch (ex: Exception) {
            LogHelper.getInstance().saveLog("", "test::mms3...$ex")
        }

    }

}
