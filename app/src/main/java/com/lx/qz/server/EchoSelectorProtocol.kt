package com.lx.qz.server

import android.content.Context
import android.util.Log
import com.lx.qz.transform.MessageException
import com.lx.qz.transform.PListTransform
import com.lx.qz.transform.Transform
import com.lx.qz.transform.command.Command
import com.lx.qz.transform.constant.CommandConstant
import com.lx.qz.transform.constant.GroupConstant
import com.lx.qz.utils.LogHelper
import com.lx.qz.utils.MsgUtil

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class EchoSelectorProtocol internal constructor(private val context: Context) : TCPProtocol {
    /*缓冲区大小*/
    private val BLOCK = 1024
    /*接受数据缓冲区*/
//    private val sendbuffer = ByteBuffer.allocate(1024 * 1024)
    /*发送数据缓冲区*/
    private val receivebuffer = ByteBuffer.allocate(BLOCK)
    private var transform: Transform<*, *>? = null
    private val TAG = "qz"

    init {
        transform = PListTransform(context)
    }

    @Throws(IOException::class)
    override fun handleAccept(key: SelectionKey) {
        val channel = (key.channel() as ServerSocketChannel).accept()
        channel.configureBlocking(false)
        //将选择器注册到连接到的客户端信道，并指定该信道key值的属性为OP_READ，同时为该信道指定关联的附件
        Log.e(TAG, "handleAccept")
        channel.register(key.selector(), SelectionKey.OP_READ)
    }

    @Throws(IOException::class)
    override fun handleRead(key: SelectionKey) {
        Log.e(TAG, "handleRead")

        val channel = key.channel() as SocketChannel
        //获取该信道所关联的附件，这里为缓冲区
        //        ByteBuffer buf = (ByteBuffer) key.attachment();
        receivebuffer.clear()

        val bytesRead = channel.read(receivebuffer)
        if (bytesRead == -1) {
            Log.e(TAG, "channel.close()")
            channel.close()
        } else if (bytesRead > 0) {
            Log.e(TAG, receivebuffer.array().toString())
            try {
                val command = transform!!.map(receivebuffer.array(), bytesRead) as Command?
                val response = command?.executor()

                channel.register(key.selector(), SelectionKey.OP_WRITE, ByteBuffer.wrap(response))

            } catch (exp: MessageException) {
                Log.e("qz", "error: ${exp.errorCode}, msg: ${exp.errorMsg}")
                LogHelper.getInstance().saveLog(TAG, "ServerThread error: ${exp.errorCode}, msg: ${exp.errorMsg}\n")
                val replayData =
                    MsgUtil.envelopedData(
                        false,
                        GroupConstant.Event,
                        CommandConstant.EventAndroidToPC,
                        MsgUtil.intToBytes(exp.errorCode)
                    )
                LogHelper.getInstance().saveLog(TAG, "catch exception socket close...\n")
                channel.register(key.selector(), SelectionKey.OP_WRITE, ByteBuffer.wrap(replayData))
            }
            key.interestOps(SelectionKey.OP_READ or SelectionKey.OP_WRITE)
        }
    }

    @Throws(IOException::class)
    override fun handleWrite(key: SelectionKey) {

        val channel = key.channel() as SocketChannel
        val buf = key.attachment() as ByteBuffer

        val sendbuffer = ByteBuffer.allocate(buf.remaining())

        sendbuffer.put(buf)
        sendbuffer.flip()

        channel.write(sendbuffer)
        if (!sendbuffer.hasRemaining()) {
            //如果缓冲区中的数据已经全部写入了信道，则将该信道感兴趣的操作设置为可读
            key.interestOps(SelectionKey.OP_READ)
        }
        //为读入更多的数据腾出空间
        sendbuffer.compact()
        sendbuffer.clear()
    }
}
