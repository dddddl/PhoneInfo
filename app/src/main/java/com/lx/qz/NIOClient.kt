package com.lx.qz

import android.util.Log
import com.google.gson.Gson
import com.lx.qz.utils.MsgUtil
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class NIOClient : Runnable {


    companion object {
        private val serverAddress = InetSocketAddress("192.168.10.118", 8999)
        /* 缓冲区大小 */
        private val blockSize = 4096
        /* 接收缓冲区 */
        private val sendBuffer = ByteBuffer.allocate(blockSize)
        /* 发送缓存区 */
        private val receiveBuffer = ByteBuffer.allocate(blockSize)


    }

    private var receiveTest: String? = null

    override fun run() {

        //打开socket通道
        val socketChannel = SocketChannel.open()
        //设置为阻塞方式
        socketChannel.configureBlocking(false)
        //打开选择器
        val selector = Selector.open()
        //注册连接服务端socket动作
        socketChannel.register(selector, SelectionKey.OP_CONNECT)
        //连接
        socketChannel.connect(serverAddress)

        var selectionKeys: Set<SelectionKey>
        var selectionKey: SelectionKey

        while (true) {
            // 连接一组键，其相应的通道已为I/O操作准备就绪
            // 此方法执行处于阻塞模式的选择操作
            selector!!.select()
            // 返回此选择器的已选集键集
            selectionKeys = selector.selectedKeys()
            val iterator = selectionKeys.iterator()
            var client: SocketChannel

            while (iterator.hasNext()) {
                selectionKey = iterator.next()
                if (selectionKey.isConnectable) {
                    println("client connect")
                    client = selectionKey.channel() as SocketChannel
                    // 判断此通道上是否正在进行连接操作
                    // 完成套接字通道的连接过程。
                    if (client.isConnectionPending) {
                        client.finishConnect()
                        println("客户端完成连接操作!")
                        sendBuffer.clear()
                        val dataLen = 1
                        val replyData = ByteArray(20)
                        replyData[8] = dataLen.shr(24).toByte()
                        replyData[9] = dataLen.shr(16).toByte()
                        replyData[10] = dataLen.shr(8).toByte()
                        replyData[11] = dataLen.toByte()
                        val msgGroup = 2
                        val msgOpCode = 3
                        replyData[12] = msgGroup.shr(8).toByte()
                        replyData[13] = msgGroup.toByte()
                        replyData[14] = msgOpCode.shr(8).toByte()
                        replyData[15] = msgOpCode.toByte()

                        val index = 1
                        val indexs = MsgUtil.intToBytes(index)
                        replyData[16] = indexs[0]
                        replyData[17] = indexs[1]
                        replyData[18] = indexs[2]
                        replyData[19] = indexs[3]
                        sendBuffer.put(replyData)
                        sendBuffer.flip()
                        client.write(sendBuffer)
                    }
                    client.register(selector, SelectionKey.OP_READ)

                } else if (selectionKey.isReadable) {
                    client = selectionKey.channel() as SocketChannel
                    // 读取服务器发送来的数据到缓存区中
                    receiveBuffer.clear()
                    val count = client.read(receiveBuffer)
                    if (count > 0) {
                        println("客户端接收到服务端的数据${Gson().toJson(receiveBuffer.array())}")
                        client.register(selector, SelectionKey.OP_WRITE)
                    }
                } else if (selectionKey.isWritable) {
//                    sendBuffer.clear()
//                    client = selectionKey.channel() as SocketChannel
//
//                    val dataLen = 1
//                    val replyData = ByteArray(17)
//                    replyData[8] = dataLen.shr(24).toByte()
//                    replyData[9] = dataLen.shr(16).toByte()
//                    replyData[10] = dataLen.shr(8).toByte()
//                    replyData[11] = dataLen.toByte()
//                    val msgGroup = 5
//                    val msgOpCode = 1
//                    replyData[12] = msgGroup.shr(8).toByte()
//                    replyData[13] = msgGroup.toByte()
//                    replyData[14] = msgOpCode.shr(8).toByte()
//                    replyData[15] = msgOpCode.toByte()
//                    replyData[16] = 1
//
//                    sendBuffer.put(replyData)
//                    sendBuffer.flip()
//                    client.write(sendBuffer)
//                    client.register(selector, SelectionKey.OP_READ)
                }
            }
            selectionKeys.clear()
        }
    }


}