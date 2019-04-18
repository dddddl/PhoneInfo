package com.dl.forensics.transform.response

import android.accounts.AccountManager
import android.content.Context
import com.dd.plist.NSDictionary

object AndroidAccountUtil {

    fun getAccountInfo(context: Context): NSDictionary {

        val root = NSDictionary()
        val accountArray = ArrayList<NSDictionary>()

        val accountManager = AccountManager.get(context)
        val accounts = accountManager.accounts

        for (account in accounts) {

            val accountNS = NSDictionary()
            accountNS.put("name", account.name)
            accountNS.put("type", account.type)
//            accountNS.put("accessId", account.type)
            accountArray.add(accountNS)
        }
        root.put("accounts", accountArray)

        return root
    }


}