package com.lx.qz.transform.constant

class GroupConstant {
    companion object {
        val HandShake = 1
        val Contacts = 2
        val MessageText = 3
        val CallHistory = 4
        val DeviceInfo = 5
        val Event = 255
        val Unknown = 65535
    }
}

class CommandConstant {
    companion object {
        val HandShake = 1
        val HandShakeAck = 2

        val ContactsCount = 1
        val ContactsCountReply = 2
        val ContactByIndex = 3
        val ContactByIndexReply = 4

        val MessageTextCount = 1
        val MessageTextCountReply = 2
        val MessageTextByIndex = 3
        val MessageTextByIndexReply = 4

        val CallHistoryCount = 1
        val CallHistoryCountReply = 2
        val CallHistoryByIndex = 3
        val CallHistoryByIndexReply = 4

        val GetDeviceInfo = 1
        val GetDeviceInfoReply = 2

        val EventPCtoAndroid = 1
        val EventAndroidToPC = 2
        val Unknown = 3
    }
}
