package com.dl.forensics.server

import android.content.Context
import android.os.Build
import android.util.Log

import java.io.IOException
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

class TCPServer(context: Context) : Runnable {

    private var protocol: EchoSelectorProtocol? = null
    private var selector: Selector? = null
    private val TAG = "qz"

    init {
        try {
            Log.e(TAG, "TCPServer start...")
            val server = ServerSocketChannel.open()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                server.bind(InetSocketAddress(8999))
            } else {
                server.socket().bind(InetSocketAddress(8999))
            }
            server.configureBlocking(false)

            selector = Selector.open()
            server.register(selector, SelectionKey.OP_ACCEPT)
            protocol = EchoSelectorProtocol(context)

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    override fun run() {
        while (true) {
            try {
                if (selector!!.select() == 0) {
                    continue
                }
                Log.e(TAG, "handle selector ...")
                val keyIterator = selector!!.selectedKeys().iterator()
                while (keyIterator.hasNext()) {
                    val selectionKey = keyIterator.next()

                    if (selectionKey.isAcceptable) {
                        protocol!!.handleAccept(selectionKey)
                    }
                    if (selectionKey.isReadable) {
                        protocol!!.handleRead(selectionKey)
                    }
                    if (selectionKey.isValid && selectionKey.isWritable) {
                        protocol!!.handleWrite(selectionKey)
                    }
                    keyIterator.remove()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }

        }
    }
}
