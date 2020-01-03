package com.lx.qz.transform.response

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.dd.plist.NSData
import com.dd.plist.NSDictionary
import com.lx.qz.utils.LogHelper
import com.lx.qz.utils.RequestPermissionUtils
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by Travis on 2018/3/7.
 */

object AndroidTextMessageUtilNew {
    val TAG = "TextMsgUtil"
    val conversationArray: ArrayList<Long> = ArrayList()
    val smsArray: ArrayList<Int> = ArrayList()
    var msmCount = 0
    var mmsCount = 0

    open fun getTextMessageThreadCount(context: Context): Int {
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_SMS)
        var count = getMsgThreadCount(context)
        msmCount = 0
        mmsCount = 0
        return count
    }

    open fun getTextMessageThreadByIndex(index: Int, context: Context): NSDictionary {
        Log.e("qz", "msmCount : $msmCount   mmsCount : $mmsCount   index: $index")
        LogHelper.getInstance().saveLog("qz", " 通过index取出的短信总和 $msmCount")
        val root = NSDictionary()
        val conversation: ArrayList<NSDictionary> = ArrayList()
        val description = NSDictionary()

        if (index >= conversationArray.size || index < 0) {
            return root
        }

        var smsCursor: Cursor? = null

        val threadID: Long = conversationArray[index]
        var addressList: ArrayList<String> = ArrayList()
        var maxDate: String = "0"
        var snippet: String = ""


        val message = Uri.parse("content://sms/")
        smsCursor =
            context.contentResolver.query(
                message,
                null,
                "thread_id=?",
                arrayOf("$threadID"),
                "date desc"
            )

        while (smsCursor != null && smsCursor.moveToNext()) {
            if (!smsCursor.isNull(smsCursor.getColumnIndex("address"))) {
                val address = smsCursor.getString(smsCursor.getColumnIndex("address"))
                if (!addressList.contains(address)) {
                    addressList.add(address)
                }
            }
            msmCount++

            //val person = smsCursor.getString(smsCursor.getColumnIndex("person"))
            val date = smsCursor.getString(smsCursor.getColumnIndex("date"))
            val date_sent = smsCursor.getString(smsCursor.getColumnIndex("date_sent"))
            val read = smsCursor.getString(smsCursor.getColumnIndex("read"))
            val status = smsCursor.getString(smsCursor.getColumnIndex("status"))
            Log.d(TAG, "status:$status")
            val type = smsCursor.getString(smsCursor.getColumnIndex("type"))
            Log.d(TAG, "type:$type")
            val body = smsCursor.getString(smsCursor.getColumnIndex("body"))
            Log.d(TAG, "body:$body")

            if (maxDate.toLong() <= date.toLong()) {
                maxDate = date
                snippet = body
            }

            val sms = NSDictionary()
            sms.put("content_type", "sms")
            sms.put("date", date)
            sms.put("read", read)
            sms.put("status", status)
            sms.put("type", type)
            sms.put("body", body)
            conversation.add(sms)
        }




        smsCursor.close()
        val pduPath = Uri.parse("content://mms/")
        val selection = "thread_id=$threadID"
        val attachmentCursor =
            context.contentResolver.query(pduPath, null, selection, null, "date desc")
        if (attachmentCursor != null) {
            while (attachmentCursor != null && attachmentCursor.moveToNext()) {
                Log.d(TAG, "----------------------------------------------------")
                val attachId = attachmentCursor.getLong(attachmentCursor.getColumnIndex("_id"))
                Log.d("mms", "attachment id: $attachId")
                val ct_t = attachmentCursor.getString(attachmentCursor.getColumnIndex("ct_t"))
                Log.d("mms", "attachment ct_t: $ct_t")
                val thread = attachmentCursor.getLong(attachmentCursor.getColumnIndex("thread_id"))
                Log.d("mms", "attachment thread_id: $thread")
                val date = attachmentCursor.getString(attachmentCursor.getColumnIndex("date"))


                val addrPath = Uri.parse("content://mms/$attachId/addr")
                val addrCursor =
                    context.contentResolver.query(addrPath, null, "type != 151", null, null)
                if (addrCursor != null) {
                    while (addrCursor.moveToNext()) {
                        val address = addrCursor.getString(addrCursor.getColumnIndex("address"))

                        if (!addressList.contains(address)) {
                            addressList.add(address)
                        }
                    }
                }
                addrCursor?.close()

                var msg_box = -1
                if (attachmentCursor.getColumnIndex("msg_box") != -1) {
                    msg_box = attachmentCursor.getInt(attachmentCursor.getColumnIndex("msg_box"))
                }
                Log.d("mms/part", "msg_box: $msg_box")
                mmsCount++

                val partPath = Uri.parse("content://mms/part")
                val partCursor =
                    context.contentResolver.query(partPath, null, "mid = $attachId", null, null)
                if (partCursor != null) {
                    while (partCursor.moveToNext()) {
                        val mid = partCursor.getLong(partCursor.getColumnIndex("_id"))
                        Log.d("mms/part", "mms/part mid: $mid")
                        val ct = partCursor.getString(partCursor.getColumnIndex("ct"))
                        Log.d("mms/part", "mms/part ct: $ct")

                        when (ct) {
                            "text/plain" -> {
                                var body: String
                                val data = partCursor.getString(partCursor.getColumnIndex("_data"))
                                if (data != null) {
                                    body = getMmsText(mid, context)
                                } else {
                                    body = partCursor.getString(partCursor.getColumnIndex("text"))
                                }
                                val mms = NSDictionary()
                                mms.put("content_type", "mms")
                                mms.put("date", date)
                                mms.put("msg_box", msg_box.toString())
                                mms.put("file_type", ct)
                                mms.put("body", body)
                                conversation.add(mms)
                            }
                            "application/smil" -> {
                                LogHelper.getInstance().saveLog(TAG, "application/smil")
                            }
                            else -> {
                                val data = partCursor.getString(partCursor.getColumnIndex("_data"))
                                if (data != null) {
                                    val dataBytes = getMmsAttachmentData(mid, context)
                                    if (dataBytes != null) {
                                        val body = NSData(dataBytes)
                                        val mms = NSDictionary()
                                        mms.put("content_type", "mms")
                                        mms.put("date", date)
                                        mms.put("msg_box", msg_box.toString())
                                        mms.put("file_type", ct)
                                        mms.put("file_path", data)
                                        mms.put("body", body)
                                        conversation.add(mms)
                                    }
                                }
                            }
                        }
                    }
                    partCursor.close()
                } else {
                    LogHelper.getInstance().saveLog(TAG, "content://mms/part is null 添加主题mms")
                    val sub = smsCursor.getString(smsCursor.getColumnIndex("sub"))

                    val mms = NSDictionary()
                    mms.put("content_type", "mms")
                    mms.put("date", date)
                    mms.put("msg_box", msg_box.toString())
                    mms.put("file_type", "text/plain")
                    mms.put("file_path", "")
                    mms.put("body", sub)
                    conversation.add(mms)
                }
                Log.d(TAG, "----------------------------------------------------")
            }
            attachmentCursor.close()
        }

        val addresses = ArrayList<NSDictionary>()
        addressList.forEachIndexed { index, addr ->
            var addrNode = NSDictionary()
            addrNode.put("address", addr)
            addresses.add(addrNode)
        }

        description.put("date", maxDate)
        description.put("snippet", snippet)
        description.put("addresses", addresses)
        root.put("description", description)

        root.put("conversation", conversation)
        //Log.i("readAllContactData", root.toXMLPropertyList())

//        conversation.forEach { ns ->
//            ns.allKeys()?.forEach {
//                LogHelper.getInstance().saveLog(TAG, "短信内容：key=$it, value=${ns[it].toString()}\n")
//            }
//        }
        return root
    }

    fun getMmsText(mid: Long, context: Context): String {
        val uri = Uri.parse("content://mms/part/" + mid)
        var input: InputStream? = null
        val stringBuilder = StringBuilder()

        try {
            input = context.contentResolver.openInputStream(uri)
            if (input != null) {
                val inputReader = InputStreamReader(input, "UTF-8")
                val bufReader = BufferedReader(inputReader)
                var line = bufReader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = bufReader.readLine()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            input?.close()
        }
        return stringBuilder.toString()
    }

    fun getMmsAttachmentData(mid: Long, context: Context): ByteArray? {
        val uri = Uri.parse("content://mms/part/" + mid)
        var input: InputStream? = null
        val stringBuilder = StringBuilder()
        var dataBytes: ByteArray? = null

        try {
            input = context.contentResolver.openInputStream(uri)
            dataBytes = readBytes(input)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            input?.close()
        }
        return dataBytes
    }

    fun readBytes(input: InputStream): ByteArray {
        val byteOutStream = ByteArrayOutputStream()

        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len = input.read(buffer)
        while (len != -1) {
            byteOutStream.write(buffer, 0, len)
            len = input.read(buffer)
        }
        return byteOutStream.toByteArray()
    }

    fun getMsgThreadCount(context: Context): Int {
        conversationArray.clear()
        LogHelper.getInstance().saveLog(TAG, "开始获取短信总数...\n")

        val test = test.sms_test(context)
        conversationArray.addAll(test)
        LogHelper.getInstance().saveLog(TAG, "短信总数===>${conversationArray.size}\n")
        return conversationArray.size
    }
}