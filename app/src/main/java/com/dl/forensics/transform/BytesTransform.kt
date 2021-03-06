package com.dl.forensics.transform

import android.content.Context
import android.util.Log
import com.dl.forensics.transform.command.*
import com.dl.forensics.transform.constant.CommandConstant
import com.dl.forensics.transform.constant.GroupConstant
import com.dl.forensics.transform.dto.PListDto
import com.dl.forensics.utils.LogHelper

class BytesTransform(private val context: Context) : Transform<Command, PListDto> {

    override fun map(bytes: ByteArray, bytesRead: Int): Command {

        if (bytesRead <= 0) {
            LogHelper.getInstance().saveLog("qz", "ServerThread throw MessageException...\n")
            throw MessageException(MessageException.SocketError)
        }

        (0 until bytesRead).forEach { index ->
            Log.d("qz", "header[$index]: ${bytes[index]}")
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
            GroupConstant.ProgramInfo -> {
                when (opCodeValue) {
                    CommandConstant.ProgramInfo -> PackageInfoCommand(
                        context,
                        GroupConstant.ProgramInfo,
                        CommandConstant.ProgramInfoReply
                    )
                    else -> throw MessageException()
                }
            }
            GroupConstant.BootInfo -> {
                when (opCodeValue) {
                    CommandConstant.GetBootInfo -> BootInfoCommand(
                        GroupConstant.BootInfo,
                        CommandConstant.GetBootInfoReply
                    )
                    else -> throw MessageException()
                }
            }
            GroupConstant.AccountInfo -> {
                when (opCodeValue) {
                    CommandConstant.GetAccountInfo -> AccountCommand(
                        context,
                        GroupConstant.AccountInfo,
                        CommandConstant.GetAccountInfoReply
                    )
                    else -> throw MessageException()
                }
            }
            GroupConstant.LocationInfo -> {
                when (opCodeValue) {
                    CommandConstant.GetLocationInfo -> LocationCommand(
                        context,
                        GroupConstant.LocationInfo,
                        CommandConstant.GetLocationInfoReply
                    )
                    else -> throw MessageException()
                }
            }
            GroupConstant.BluetoothInfo -> {
                when (opCodeValue) {
                    CommandConstant.GetBluetoothInfo -> BluetoothCommand(
                        GroupConstant.BluetoothInfo,
                        CommandConstant.GetLocationInfoReply
                    )
                    else -> throw MessageException()
                }
            }

            else -> {
                throw MessageException()
            }
        }

        return command
    }

    override fun parse(pListDto: PListDto): ByteArray {
        return ByteArray(0)
    }
}
