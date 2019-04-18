package com.dl.forensics.server

import java.io.IOException
import java.nio.channels.SelectionKey

interface TCPProtocol {
    //accept I/O形式
    @Throws(IOException::class)
    fun handleAccept(key: SelectionKey)

    //read I/O形式
    @Throws(IOException::class)
    fun handleRead(key: SelectionKey)

    //write I/O形式
    @Throws(IOException::class)
    fun handleWrite(key: SelectionKey)
}
