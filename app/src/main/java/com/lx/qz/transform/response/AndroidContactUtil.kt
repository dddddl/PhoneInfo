package com.lx.qz.transform.response

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract.*
import android.util.Log
import com.dd.plist.NSData
import com.dd.plist.NSDictionary
import com.lx.qz.MainActivity
import com.lx.qz.transform.MessageException
import com.lx.qz.utils.LogHelper
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Travis on 2018/2/27.
 */

object AndroidContactsUtil {

    val TAG = "ContactsUtil"
    val contactIdArray : ArrayList<Int> = ArrayList()


    open fun getContactCount(context:Context) : Int  {
        Log.e(TAG, "getContactCount start")
        LogHelper.getInstance().saveLog("开始获取联系人总数...\n")
        MainActivity.permissionDelegate?.requestRuntimePermission(Manifest.permission.READ_CONTACTS)
        contactIdArray.clear()
        var cursor : Cursor? = null
        try {
            cursor = context.contentResolver.query(Contacts.CONTENT_URI, arrayOf(Contacts._ID), null, null, null)
            while (cursor!=null && cursor.moveToNext()) {
                contactIdArray.add(cursor.getInt(0))
            }
        }
        catch (e : Exception) {
            e.printStackTrace()
            LogHelper.getInstance().saveLog("AndroidContactUtil throw MessageException...\n")
            throw MessageException(MessageException.ContactPermissionGrantedError)
        }
        finally {
            cursor?.close()
        }

        contactIdArray.forEachIndexed {index, id ->
            Log.i(TAG, "contacts[$index] = $id")
        }
        Log.e(TAG, "getContactCount end")
        LogHelper.getInstance().saveLog("联系人总数===>${contactIdArray.size}\n")
        return contactIdArray.size
    }

    open fun getContactByIndex(index: Int,context:Context) : NSDictionary {
        if (contactIdArray.size == 0) {
            getContactCount(context)
        }

        var mimeType: String

        var root = NSDictionary()
        val phoneArray = ArrayList<NSDictionary>()
        val addressArray = ArrayList<NSDictionary>()
        val emailArray = ArrayList<NSDictionary>()
        val eventArray = ArrayList<NSDictionary>()
        val imArray = ArrayList<NSDictionary>()
        val nameDic = NSDictionary()
        val noteDic = NSDictionary()
        val nickNameDic = NSDictionary()
        val orgDic = NSDictionary()
        val webSiteArray = ArrayList<NSDictionary>()
        val relationArray = ArrayList<NSDictionary>()
        val photoDic = NSDictionary()

        if (index >= contactIdArray.size) {
            return root
        }

        var selection = "${Data.CONTACT_ID} = ${contactIdArray[index]}"
        var cursor : Cursor? = null
        try {
            cursor = context.contentResolver.query(Data.CONTENT_URI, null, selection, null, null)

            while (cursor!=null && cursor.moveToNext()) {
                //contactId = cursor.getInt(cursor.getColumnIndex(Data.RAW_CONTACT_ID))
                mimeType = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE))
                when (mimeType) {
                    CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE -> {
                        val prefix = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredName.PREFIX))
                        nameDic.put("prefix", prefix)
                        val firstName =  cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredName.FAMILY_NAME))
                        nameDic.put("firstName", firstName)
                        val middleName = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredName.MIDDLE_NAME))
                        nameDic.put("firstName", firstName)
                        val lastname = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredName.GIVEN_NAME))
                        nameDic.put("lastname", lastname)
                        val suffix = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredName.SUFFIX))
                        nameDic.put("suffix", suffix)
                        val phoneticFirstName = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME))
                        nameDic.put("phoneticFirstName", phoneticFirstName)
                        val phoneticMiddleName = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME))
                        nameDic.put("phoneticMiddleName", phoneticMiddleName)
                        val phoneticLastName = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME))
                        nameDic.put("phoneticLastName", phoneticLastName)
                        val displayName = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredName.DISPLAY_NAME))
                        nameDic.put("displayName", displayName)
                    }

                    CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                        val phoneType = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Phone.TYPE))

                        val node = NSDictionary()
                        when(phoneType) {
                            CommonDataKinds.Phone.TYPE_MOBILE -> {
                                node.put("label", "mobile")
                                val mobile = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", mobile)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_HOME -> {
                                node.put("label", "homeNum")
                                val homeNum = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", homeNum)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_WORK -> {
                                node.put("label", "jobNum")
                                val jobNum = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", jobNum)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_FAX_WORK -> {
                                node.put("label", "workFax")
                                val workFax = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", workFax)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_FAX_HOME -> {
                                node.put("label", "homeFax")
                                val homeFax = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", homeFax)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_PAGER -> {
                                node.put("label", "pager")
                                val pager = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", pager)
                                phoneArray.add(node)
                            }

                            CommonDataKinds.Phone.TYPE_CALLBACK -> {
                                node.put("label", "quickNum")
                                val quickNum = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", quickNum)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_COMPANY_MAIN -> {
                                node.put("company_main", "pager")
                                val company_main = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", company_main)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_CAR -> {
                                node.put("label", "carNum")
                                val carNum = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", carNum)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_ISDN -> {
                                node.put("label", "isdn")
                                val isdn = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", isdn)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_MAIN -> {
                                node.put("label", "main")
                                val main = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", main)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_RADIO -> {
                                node.put("label", "wirelessDev")
                                val wirelessDev = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", wirelessDev)
                                phoneArray.add(node)
                            }

                            CommonDataKinds.Phone.TYPE_TELEX -> {
                                node.put("label", "telegram")
                                val telegram = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", telegram)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_TTY_TDD -> {
                                node.put("label", "tty_tdd")
                                val tty_tdd = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", tty_tdd)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_WORK_MOBILE -> {
                                node.put("label", "jobMobile")
                                val jobMobile = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", jobMobile)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_WORK_PAGER -> {
                                node.put("label", "jobPager")
                                val jobPager = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", jobPager)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_MMS -> {
                                node.put("label", "mms")
                                val mms = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", mms)
                                phoneArray.add(node)
                            }

                            CommonDataKinds.Phone.TYPE_OTHER -> {
                                node.put("label", "other")
                                val other = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", other)
                                phoneArray.add(node)
                            }
                            CommonDataKinds.Phone.TYPE_ASSISTANT -> {
                                node.put("label", "assistant")
                                val assistant = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", assistant)
                                phoneArray.add(node)
                            }

                            CommonDataKinds.Phone.TYPE_CUSTOM -> {
                                val label = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.LABEL))
                                node.put("label", label)
                                val assistant = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                                node.put("number", assistant)
                                phoneArray.add(node)
                            }
                        }
                    }

                    CommonDataKinds.Email.CONTENT_ITEM_TYPE -> {
                        val emailType = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Email.TYPE))
                        val node = NSDictionary()
                        when(emailType) {
                            CommonDataKinds.Email.TYPE_HOME -> {
                                node.put("label", "homeEmail")
                                val homeEmail = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.DATA))
                                node.put("email", homeEmail)
                                emailArray.add(node)
                            }

                            CommonDataKinds.Email.TYPE_MOBILE -> {
                                node.put("label", "mobile")
                                val email = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.DATA))
                                node.put("email", email)
                                emailArray.add(node)
                            }
                            CommonDataKinds.Email.TYPE_OTHER -> {
                                node.put("label", "other")
                                val other = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.DATA))
                                node.put("email", other)
                                emailArray.add(node)
                            }
                            CommonDataKinds.Email.TYPE_WORK -> {
                                node.put("label", "work")
                                val email = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.DATA))
                                node.put("email", email)
                                emailArray.add(node)
                            }

                            CommonDataKinds.Email.TYPE_CUSTOM-> {
                                val label = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.LABEL))
                                node.put("label", label)
                                val homeEmail = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.DATA))
                                node.put("email", homeEmail)
                                emailArray.add(node)
                            }
                        }
                    }

                    CommonDataKinds.Event.CONTENT_ITEM_TYPE -> {
                        val eventType = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Event.TYPE))
                        val node = NSDictionary()
                        when (eventType) {
                            CommonDataKinds.Event.TYPE_BIRTHDAY -> {
                                node.put("label", "birthday")
                                val dateStr = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Event.START_DATE))
//                                node.put("date", dateStr)
                                node.put("date", getStringToDate(translateDate(dateStr), "yyyyMMdd"))
                                eventArray.add(node)
                            }

                            CommonDataKinds.Event.TYPE_ANNIVERSARY -> {
                                node.put("label", "anniversary")
                                val dateStr = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Event.START_DATE))
//                                node.put("date", dateStr)
                                node.put("date", getStringToDate(translateDate(dateStr), "yyyyMMdd"))
                                eventArray.add(node)
                            }

                            CommonDataKinds.Event.TYPE_OTHER -> {
                                node.put("label", "homeEmail")
                                val dateStr = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Event.START_DATE))
//                                node.put("date", dateStr)
                                node.put("date", getStringToDate(translateDate(dateStr), "yyyyMMdd"))
                                eventArray.add(node)
                            }

                            CommonDataKinds.Event.TYPE_CUSTOM -> {
                                val label = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Event.LABEL))
                                node.put("label", label)
                                val dateStr = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Event.START_DATE))
//                                node.put("date", dateStr)
                                node.put("date", getStringToDate(translateDate(dateStr), "yyyyMMdd"))
                                eventArray.add(node)
                            }
                        }
                    }

                    CommonDataKinds.Im.CONTENT_ITEM_TYPE -> {
                        val imType = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Im.PROTOCOL))
                        var label = ""
                        when (imType) {
                            CommonDataKinds.Im.PROTOCOL_ICQ -> {
                                label = "icq"
                            }
                            CommonDataKinds.Im.PROTOCOL_GOOGLE_TALK -> {
                                label = "google_talk"
                            }
                            CommonDataKinds.Im.PROTOCOL_JABBER -> {
                                label = "jabber"
                            }
                            CommonDataKinds.Im.PROTOCOL_MSN -> {
                                label = "msn"
                            }
                            CommonDataKinds.Im.PROTOCOL_NETMEETING -> {
                                label = "netmeeting"
                            }
                            CommonDataKinds.Im.PROTOCOL_QQ -> {
                                label = "qq"
                            }
                            CommonDataKinds.Im.PROTOCOL_SKYPE -> {
                                label = "skype"
                            }
                            CommonDataKinds.Im.PROTOCOL_YAHOO -> {
                                label = "yahoo"
                            }
                            CommonDataKinds.Im.TYPE_HOME -> {
                                label = "home"
                            }
                            CommonDataKinds.Im.TYPE_WORK -> {
                                label = "work"
                            }
                            CommonDataKinds.Im.TYPE_OTHER -> {
                                label = "other"
                            }
                            CommonDataKinds.Im.TYPE_CUSTOM -> {
                                label = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Im.LABEL))
                            }
                        }

                        val protocol = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Im.DATA))
                        val node = NSDictionary()
                        node.put("label", label)
                        node.put("protocol", protocol)
                        imArray.add(node)
                    }

                    CommonDataKinds.Note.CONTENT_ITEM_TYPE -> {
                        val note = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Note.NOTE))
                        noteDic.put("note", note)
                    }

                    CommonDataKinds.Nickname.CONTENT_ITEM_TYPE -> {
                        val nickName = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Nickname.NAME))
                        nickNameDic.put("nickName", nickName)
                    }

                    CommonDataKinds.Organization.CONTENT_ITEM_TYPE -> {
                        if (!cursor.isNull(cursor.getColumnIndex(CommonDataKinds.Organization.COMPANY))) {
                            var company = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Organization.COMPANY))
                            orgDic.put("company", company)
                        }
                        if (!cursor.isNull(cursor.getColumnIndex(CommonDataKinds.Organization.TITLE))) {
                            var title = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Organization.TITLE))
                            orgDic.put("title", title)
                        }
                        if (!cursor.isNull(cursor.getColumnIndex(CommonDataKinds.Organization.DEPARTMENT))) {
                            var department = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Organization.DEPARTMENT))
                            orgDic.put("department", department)
                        }
                        if (!cursor.isNull(cursor.getColumnIndex(CommonDataKinds.Organization.JOB_DESCRIPTION))) {
                            var data = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Organization.DEPARTMENT))
                            orgDic.put("job description", data)
                        }

                        if (!cursor.isNull(cursor.getColumnIndex(CommonDataKinds.Organization.OFFICE_LOCATION))) {
                            var data = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Organization.DEPARTMENT))
                            orgDic.put("office location", data)
                        }

                        if (!cursor.isNull(cursor.getColumnIndex(CommonDataKinds.Organization.SYMBOL))) {
                            var data = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Organization.DEPARTMENT))
                            orgDic.put("symbol", data)
                        }

                        if (!cursor.isNull(cursor.getColumnIndex(CommonDataKinds.Organization.SYMBOL))) {
                            var data = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Organization.DEPARTMENT))
                            orgDic.put("symbol", data)
                        }
                    }

                    CommonDataKinds.Website.CONTENT_ITEM_TYPE -> {
                        val webDic = NSDictionary()
                        var label = ""
                        val webType = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Website.TYPE))
                        when (webType)
                        {
                            CommonDataKinds.Website.TYPE_CUSTOM -> {
                                label = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Website.LABEL))
                            }
                            CommonDataKinds.Website.TYPE_BLOG -> {
                                label = "blog"
                            }
                            CommonDataKinds.Website.TYPE_FTP -> {
                                label = "ftp"
                            }
                            CommonDataKinds.Website.TYPE_HOME -> {
                                label = "home"
                            }
                            CommonDataKinds.Website.TYPE_HOMEPAGE -> {
                                label = "homepage"
                            }
                            CommonDataKinds.Website.TYPE_OTHER -> {
                                label = "other"
                            }
                            CommonDataKinds.Website.TYPE_PROFILE -> {
                                label = "profile"
                            }
                            CommonDataKinds.Website.TYPE_WORK -> {
                                label = "work"
                            }
                        }

                        val url = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Website.URL))
                        webDic.put("label", label)
                        webDic.put("url", url)
                        webSiteArray.add(webDic)
                    }

                    CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE -> {
                        val postalType = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.TYPE))
                        val addressNode = NSDictionary()
                        var label = ""
                        when (postalType) {
                            CommonDataKinds.StructuredPostal.TYPE_HOME -> {
                                label = "home"
                            }
                            CommonDataKinds.StructuredPostal.TYPE_WORK -> {
                                label = "work"
                            }
                            CommonDataKinds.StructuredPostal.TYPE_OTHER -> {
                                label = "other"
                            }
                            CommonDataKinds.StructuredPostal.TYPE_CUSTOM -> {
                                label = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.LABEL))
                            }
                        }
                        addressNode.put("label", label)

                        val homeStreet = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.STREET))
                        addressNode.put("homeStreet", homeStreet)
                        val homeCity = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.CITY))
                        addressNode.put("homeCity", homeCity)
                        val homeBox = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.POBOX))
                        addressNode.put("homeBox", homeBox)
                        val homeArea = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.NEIGHBORHOOD))
                        addressNode.put("homeArea", homeArea)
                        val homeState = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.REGION))
                        addressNode.put("homeState", homeState)
                        val homeZip = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.POSTCODE))
                        addressNode.put("homeZip", homeZip)
                        val homeCountry = cursor.getString(cursor.getColumnIndex(CommonDataKinds.StructuredPostal.COUNTRY))
                        addressNode.put("homeCountry", homeCountry)

                        addressArray.add(addressNode)
                    }

                    CommonDataKinds.Relation.CONTENT_ITEM_TYPE -> {
                        val relationType = cursor.getInt(cursor.getColumnIndex(CommonDataKinds.Relation.TYPE))
                        val relationDic = NSDictionary()
                        var label = ""
                        when (relationType) {
                            CommonDataKinds.Relation.TYPE_ASSISTANT -> {
                                label = "assistant"
                            }
                            CommonDataKinds.Relation.TYPE_BROTHER -> {
                                label = "brother"
                            }
                            CommonDataKinds.Relation.TYPE_CHILD -> {
                                label = "child"
                            }
                            CommonDataKinds.Relation.TYPE_DOMESTIC_PARTNER -> {
                                label = "domestic_partner"
                            }
                            CommonDataKinds.Relation.TYPE_FATHER -> {
                                label = "father"
                            }
                            CommonDataKinds.Relation.TYPE_FRIEND -> {
                                label = "friend"
                            }
                            CommonDataKinds.Relation.TYPE_MANAGER -> {
                                label = "manager"
                            }
                            CommonDataKinds.Relation.TYPE_MOTHER -> {
                                label = "mother"
                            }
                            CommonDataKinds.Relation.TYPE_PARENT -> {
                                label = "parent"
                            }
                            CommonDataKinds.Relation.TYPE_PARTNER -> {
                                label = "partner"
                            }
                            CommonDataKinds.Relation.TYPE_REFERRED_BY -> {
                                label = "referred_by"
                            }
                            CommonDataKinds.Relation.TYPE_RELATIVE -> {
                                label = "relative"
                            }
                            CommonDataKinds.Relation.TYPE_SISTER -> {
                                label = "sister"
                            }
                            CommonDataKinds.Relation.TYPE_SPOUSE -> {
                                label = "spouse"
                            }

                            CommonDataKinds.Relation.TYPE_CUSTOM -> {
                                label = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Relation.LABEL))
                            }
                        }
                        relationDic.put("label", label)
                        val relation = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Relation.DATA))
                        relationDic.put("relation", relation)
                        relationArray.add(relationDic)
                    }

                    CommonDataKinds.Photo.CONTENT_ITEM_TYPE -> {
                        val photoBytes = cursor.getBlob(cursor.getColumnIndex(CommonDataKinds.Photo.PHOTO))
                        val photoData = NSData(photoBytes)
                        Log.i("readAllContactData", "photoBytes len:${photoBytes.size}")
                        photoDic.put("photo", photoData)
                        photoDic.put("encoding", "bitmap")
                    }
                }
            }
        }
        catch (e : Exception) {
            e.printStackTrace()
        }

        finally {
            cursor?.close()
        }

        root.put("Name", nameDic)
        root.put("Telephones", phoneArray)
        root.put("Address", addressArray)
        root.put("Emails", emailArray)
        root.put("Events", eventArray)
        root.put("IMs", imArray)
        root.put("NickName", nickNameDic)
        root.put("Organization", orgDic)
        root.put("Photo", photoDic)
        root.put("Note", noteDic)
        //Log.d("readAllContactData", root.toXMLPropertyList())
        /*
        val rawData = root.toXMLPropertyList().toByteArray()
        val file = File("/sdcard/contact.plist")
        file.writeBytes(rawData)
        */

        var nameArray = nameDic.allKeys()
        nameArray.forEach {
            LogHelper.getInstance().saveLog("联系人内容：key=$it, value=${nameDic[it].toString()}\n")
        }

        return root
    }

    private fun translateDate(date: String): String {
        var result = date
        if (date.contains("/")) {
            result = date.replace("/".toRegex(), "")
        }
        if (date.contains("-")) {
            result = date.replace("-".toRegex(), "")
        }
        if (date.contains(" ")) {
            result = date.replace(" ".toRegex(), "")
        }
        return result
    }

    /**
     * 将字符串转为时间戳
     * @param dateString
     * @param pattern
     * @return
     */
    fun getStringToDate(dateString: String, pattern: String): Long {
        val dateFormat = SimpleDateFormat(pattern)
        var date = Date()
        try {
            date = dateFormat.parse(dateString)
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return date.time / 1000L
    }
}
