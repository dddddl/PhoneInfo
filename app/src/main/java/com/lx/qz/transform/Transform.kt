package com.lx.qz.transform

interface Transform<T, M> {

    @Throws(MessageException::class)
    fun map(bytes: ByteArray, bytesRead: Int): T

    fun parse(m: M): ByteArray

}
