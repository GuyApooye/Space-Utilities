package com.github.guyapooye.spaceutilities.util

import dev.architectury.injectables.annotations.ExpectPlatform

@Suppress("UNUSED_PARAMETERS")
object PlatformUtils {
    @JvmStatic
    @ExpectPlatform
    fun runWhenOn(e: Enum<*>, supplier: () -> Unit) {
        throw AssertionError()
    }
}
