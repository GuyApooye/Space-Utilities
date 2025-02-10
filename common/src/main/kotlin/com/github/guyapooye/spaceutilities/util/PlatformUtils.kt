package com.github.guyapooye.spaceutilities.util

import dev.architectury.injectables.annotations.ExpectPlatform

object PlatformUtils {
    @ExpectPlatform
    fun runWhenOn(e: Enum<*>?, supplier: Runnable?) {
        throw AssertionError()
    }
}
