package com.lx.qz.transform.response

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.Telephony.Threads
import android.text.TextUtils
import android.util.Log
import com.dd.plist.NSData
import com.dd.plist.NSDictionary
import com.lx.qz.MainActivity
import com.lx.qz.transform.MessageException
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

    open fun getTextMessageThreadCount(context: Context): Int {
        RequestPermissionUtils.requestRuntimePermission(Manifest.permission.READ_SMS)
        var count = getMsgThreadCount(context)
        return count
    }

    open fun getTextMessageThreadByIndex(index: Int, context: Context): NSDictionary {
        val root = NSDictionary()
        val conversation: ArrayList<NSDictionary> = ArrayList()
        val description = NSDictionary()

        if (index >= conversationArray.size || index < 0) {
            return root
        }

        var threadCursor: Cursor? = null
        var smsCursor: Cursor? = null
        var attachmentCursor: Cursor? = null
        try {
            val threadCursor: Cursor
            val selection = "_id = ${conversationArray[index]}"
            val uri: Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uri = Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build()
                val ALL_THREADS_PROJECTION = arrayOf(
                    Threads._ID,
                    Threads.DATE,
                    Threads.MESSAGE_COUNT,
                    Threads.RECIPIENT_IDS,
                    Threads.SNIPPET,
                    Threads.SNIPPET_CHARSET,
                    Threads.READ,
                    Threads.ERROR,
                    Threads.HAS_ATTACHMENT
                )
                threadCursor = context.contentResolver.query(uri, ALL_THREADS_PROJECTION, selection, null, "date DESC")
            } else {
                uri = Uri.parse("content://mms-sms/conversations/")
                val projection = arrayOf("_id", "thread_id", "address", "person", "date", "body", "type")
                threadCursor = context.contentResolver.query(uri, projection, selection, null, null)
            }

            if (threadCursor != null && threadCursor.moveToNext()) {
                val threadID: Long
                var address: ArrayList<String> = ArrayList()
                var date: String
                var snippet: String
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    threadID = threadCursor.getLong(threadCursor.getColumnIndex(Threads._ID))
                    if (!threadCursor.isNull(threadCursor.getColumnIndex(Threads.SNIPPET))) {
                        snippet = threadCursor.getString(threadCursor.getColumnIndex(Threads.SNIPPET))
                    } else {
                        snippet = ""
                    }

                    val recipientIDs = threadCursor.getString(threadCursor.getColumnIndex(Threads.RECIPIENT_IDS))
                    val recipientArray = recipientIDs.split(" ")
                    Log.d(TAG, "recipientIDs:$recipientIDs")
                    recipientArray.forEach { recipientID ->
                        Log.d(TAG, "recipientID:$recipientID")
                        val selection = "_id = $recipientID"
                        val projection = arrayOf("_id", "address")
                        val addressCursor = context.contentResolver.query(
                            Uri.parse("content://mms-sms/canonical-addresses"),
                            projection,
                            selection,
                            null,
                            null
                        )
                        if (addressCursor != null && addressCursor.moveToNext()) {
                            val addr = addressCursor.getString(addressCursor.getColumnIndex("address"))
                            address.add(addr)
                        }
                    }
                    date = threadCursor.getString(threadCursor.getColumnIndex(Threads.DATE))
                } else {
                    val threadColumns = arrayOf("address", "person", "date", "body", "type")
                    threadID = threadCursor.getLong(threadCursor.getColumnIndex("thread_id"))
                    val addr = threadCursor.getString(threadCursor.getColumnIndex(threadColumns[0]))
                    Log.d(TAG, "addr:$addr")
                    address.add(addr)
                    date = threadCursor.getString(threadCursor.getColumnIndex(threadColumns[2]))
                    snippet = threadCursor.getString(threadCursor.getColumnIndex(threadColumns[3]))
                }

                Log.d(TAG, "thread id: $threadID")
                Log.d(TAG, "snippet: $snippet")
                Log.d(TAG, "date: $date")

                val addresses = ArrayList<NSDictionary>()
                address.forEachIndexed { index, addr ->
                    var addrNode = NSDictionary()
                    addrNode.put("address", addr)
                    addresses.add(addrNode)
                }

                description.put("date", date)
                description.put("snippet", snippet)
                description.put("addresses", addresses)

                if (checkConversationIsValid(snippet, address)) {
                    root.put("description", description)
                }

                val message = Uri.parse("content://sms/")
                smsCursor =
                    context.contentResolver.query(message, null, "thread_id=?", arrayOf("$threadID"), "date desc")
                while (smsCursor != null && smsCursor.moveToNext()) {
                    if (!smsCursor.isNull(smsCursor.getColumnIndex("address"))) {
                        val address = smsCursor.getString(smsCursor.getColumnIndex("address"))
                    }
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
                val attachmentCursor = context.contentResolver.query(pduPath, null, selection, null, "date desc")
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

                        var msg_box = -1
                        if (attachmentCursor.getColumnIndex("msg_box") != -1) {
                            msg_box = attachmentCursor.getInt(attachmentCursor.getColumnIndex("msg_box"))
                        }
                        Log.d("mms/part", "msg_box: $msg_box")

                        val partPath = Uri.parse("content://mms/part")
                        val partCursor = context.contentResolver.query(partPath, null, "mid = $attachId", null, null)
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
                        }
                        Log.d(TAG, "----------------------------------------------------")
                    }
                    attachmentCursor.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            threadCursor?.close()
        }
        /*
        conversationArray.forEachIndexed { index, threadId ->
            Log.d(TAG, "conversationArray[$index]: $threadId")
        }
        */
        root.put("conversation", conversation)
        //Log.i("readAllContactData", root.toXMLPropertyList())

        conversation.forEach { ns ->
            var nameArray = ns.allKeys()
            nameArray.forEach {
                LogHelper.getInstance().saveLog("短信内容：key=$it, value=${ns[it].toString()}\n")
            }
        }
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
        LogHelper.getInstance().saveLog("开始获取短信总数...\n")

        var threadCursor: Cursor? = null
        val uri: Uri
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uri = Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build()
                val ALL_THREADS_PROJECTION = arrayOf(
                    Threads._ID,
                    Threads.DATE,
                    Threads.MESSAGE_COUNT,
                    Threads.RECIPIENT_IDS,
                    Threads.SNIPPET,
                    Threads.SNIPPET_CHARSET,
                    Threads.READ,
                    Threads.ERROR,
                    Threads.HAS_ATTACHMENT
                )
                threadCursor = context.contentResolver.query(uri, ALL_THREADS_PROJECTION, null, null, "date DESC")
            } else {
                uri = Uri.parse("content://mms-sms/conversations/")
                val projection = arrayOf("_id", "thread_id", "address", "person", "date", "body", "type")
                threadCursor = context.contentResolver.query(uri, projection, null, null, null)
            }

            if (threadCursor != null && threadCursor.count > 0) {
                while (threadCursor != null && threadCursor.moveToNext()) {
//                    val threadID : Long
                    var address: ArrayList<String> = ArrayList()
                    var snippet: String
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        threadID = threadCursor.getLong(threadCursor.getColumnIndex(Threads._ID))
                        if (!threadCursor.isNull(threadCursor.getColumnIndex(Threads.SNIPPET))) {
                            snippet = threadCursor.getString(threadCursor.getColumnIndex(Threads.SNIPPET))
                        } else {
                            snippet = ""
                        }

                        val recipientIDs = threadCursor.getString(threadCursor.getColumnIndex(Threads.RECIPIENT_IDS))
                        val recipientArray = recipientIDs.split(" ")
                        Log.d(TAG, "recipientIDs:$recipientIDs")
                        recipientArray.forEach { recipientID ->
                            Log.d(TAG, "recipientID:$recipientID")
                            val selection = "_id = $recipientID"
                            val projection = arrayOf("_id", "address")
                            val addressCursor = context.contentResolver.query(
                                Uri.parse("content://mms-sms/canonical-addresses"),
                                projection,
                                selection,
                                null,
                                null
                            )
                            if (addressCursor != null && addressCursor.moveToNext()) {
                                val addr = addressCursor.getString(addressCursor.getColumnIndex("address"))
                                address.add(addr)
                            }
                        }
                    } else {
                        val threadColumns = arrayOf("address", "person", "date", "body", "type")
//                        threadID = threadCursor.getLong(threadCursor.getColumnIndex("thread_id"))
                        val addr = threadCursor.getString(threadCursor.getColumnIndex(threadColumns[0]))
                        Log.d(TAG, "addr:$addr")
                        address.add(addr)
                        snippet = threadCursor.getString(threadCursor.getColumnIndex(threadColumns[3]))
                    }

                    val threadId = threadCursor.getLong(threadCursor.getColumnIndex(Threads._ID))
                    Log.d(TAG, "thread id: $threadId")
                    /*
                    val snippet = threadCursor.getString(threadCursor.getColumnIndex(Threads.SNIPPET))
                    Log.d(TAG, "snippet: $snippet")
                    */
//                    conversationArray.add(threadId)
                    if (checkConversationIsValid(snippet, address)) {
                        conversationArray.add(threadId)
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("sss", e.toString())
            LogHelper.getInstance().saveLog("AndroidTextMessageUtilNew throw MessageException...\n")
            throw MessageException(MessageException.TextMessagePermissionGrantedError)
        } finally {
            threadCursor?.close()
        }
        /*
        conversationArray.forEachIndexed { index, id ->
            getTextMessageThreadByIndex(index)
        }
        */
        LogHelper.getInstance().saveLog("短信总数===>${conversationArray.size}\n")
        return conversationArray.size
    }

    fun checkConversationIsValid(snippet: String, address: ArrayList<String>): Boolean {
        if (!TextUtils.isEmpty(snippet)) {
            return true;
        } else {
            if (address != null && address.size == 0) {
                return false;
            }
            if (address.contains("ʼUNKNOWN_SENDER!ʼ")) {
                return false
            } else {
                return true
            }
        }
    }

}