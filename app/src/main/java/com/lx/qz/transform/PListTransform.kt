package com.lx.qz.transform

import android.content.Context
import android.util.Log
import com.lx.qz.transform.command.*
import com.lx.qz.transform.constant.CommandConstant
import com.lx.qz.transform.constant.GroupConstant
import com.lx.qz.transform.dto.PListDto
import com.lx.qz.utils.LogHelper

class PListTransform(private val context: Context) : Transform<Command, PListDto> {

    override fun map(bytes: ByteArray, bytesRead: Int): Command {

        if (bytesRead <= 0) {
            LogHelper.getInstance().saveLog("qz", "ServerThread throw MessageException...\n")
            throw MessageException(MessageException.SocketError)
        }

        (0 until bytesRead).forEach{ index ->
            Log.d("qz","header[$index]: ${bytes[index]}")
        }

        var dataLength = 0
        (0 until 4).forEach { index ->
            dataLength += bytes[index + 8].toInt().shl(8 * (3 - index))
        }
        Log.d("qz", "dataLength: $dataLength")

        val header = bytes.copyOfRange(0, 12)
        val data = bytes.copyOfRange(12, bytesRead)

        val groupValue = data[0].toInt().shl(8) + data[1]
        val opCodeValue = data[2].toInt().shl(8) + data[3]
        Log.d("qz", "groupValue:$groupValue, opCodeValue:$opCodeValue")

        val rawData = data.copyOfRange(4, data.size)

        val command: Command?
        command = when (groupValue) {
            GroupConstant.HandShake -> {
                when (opCodeValue) {
                    CommandConstant.HandShake -> HandShakeCommand(GroupConstant.HandShake, CommandConstant.HandShakeAck)
                    else -> throw MessageException()
                }
            }
            GroupConstant.Contacts -> {
                when (opCodeValue) {
                    CommandConstant.ContactsCount -> ContactsCountCommand(
                        context,
                        GroupConstant.Contacts,
                        CommandConstant.ContactsCountReply
                    )
                    CommandConstant.ContactByIndex -> ContactCommand(
                        context,
                        GroupConstant.Contacts,
                        CommandConstant.ContactByIndexReply,
                        rawData
                    )
                    else -> throw MessageException()
                }
            }
            GroupConstant.MessageText -> {
                when (opCodeValue) {
                    CommandConstant.MessageTextCount -> MessageTextCountCommand(
                        context,
                        GroupConstant.MessageText,
                        CommandConstant.MessageTextCountReply
                    )
                    CommandConstant.MessageTextByIndex -> MessageTextCommand(
                        context,
                        GroupConstant.MessageText,
                        CommandConstant.MessageTextByIndexReply,
                        rawData
                    )
                    else -> throw MessageException()
                }
            }
            GroupConstant.CallHistory -> {
                when (opCodeValue) {
                    CommandConstant.CallHistoryCount -> CallHistoryCountCommand(
                        context,
                        GroupConstant.CallHistory,
                        CommandConstant.CallHistoryCountReply
                    )
                    CommandConstant.CallHistoryByIndex -> CallHistoryCommand(
                        context,
                        GroupConstant.CallHistory,
                        CommandConstant.CallHistoryByIndexReply,
                        rawData
                    )
                    else -> throw MessageException()
                }
            }
            GroupConstant.DeviceInfo -> {
                when (opCodeValue) {
                    CommandConstant.GetDeviceInfo -> DeviceInfoCommand(
                        context,
                        GroupConstant.DeviceInfo,
                        CommandConstant.GetDeviceInfoReply
                    )
                    else -> throw MessageException()
                }
            }
            GroupConstant.WifiHistory -> {
                when (opCodeValue) {
                    CommandConstant.WifiHistory -> WifiHistoryCommand(
                        context,
                        GroupConstant.WifiHistory,
                        CommandConstant.WifiHistoryReply
                    )
                    else -> throw MessageException()
                }
            }
            else -> {
                null
            }
        }

        return command!!
    }

    override fun parse(pListDto: PListDto): ByteArray {
        return ByteArray(0)
    }
}
