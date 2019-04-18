package com.dl.forensics.transform.command


interface Command {

    fun executor(): ByteArray

}
