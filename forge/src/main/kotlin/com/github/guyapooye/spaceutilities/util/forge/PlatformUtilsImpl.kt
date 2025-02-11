package com.github.guyapooye.spaceutilities.util.forge

import net.minecraftforge.api.distmarker.Dist

object PlatformUtilsImpl {
    @JvmStatic
    fun runWhenOn(e: Enum<*>, supplier: () -> Unit) {
        thedarkcolour.kotlinforforge.forge.runWhenOn(e as Dist, supplier)
    }
}