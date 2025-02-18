package com.github.guyapooye.spaceutilities.networking

interface NetworkContext {
    fun enqueueWork(var1: Runnable)

    fun handled() {
        setPacketHandled(true)
    }

    fun setPacketHandled(var1: Boolean)
}
