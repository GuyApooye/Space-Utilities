package com.github.guyapooye.spaceutilities.util

import com.github.guyapooye.spaceutilities.networking.PacketChannel
import dev.architectury.injectables.annotations.ExpectPlatform

@Suppress("UNUSED_PARAMETERS")
object PlatformUtils {
    @JvmStatic
    @ExpectPlatform
    fun runWhenOn(e: Enum<*>, supplier: () -> Unit) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getPacketChannel(): PacketChannel {
        throw AssertionError()
    }
}
