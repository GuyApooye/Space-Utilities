package com.github.guyapooye.spaceutilities.util.fabric

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader

object PlatformUtilsImpl {
    @JvmStatic
    fun runWhenOn(e: Enum<*>, supplier: () -> Unit) {
        if (FabricLoader.getInstance().environmentType == e as EnvType) {
            supplier.invoke()
        }
    }
}