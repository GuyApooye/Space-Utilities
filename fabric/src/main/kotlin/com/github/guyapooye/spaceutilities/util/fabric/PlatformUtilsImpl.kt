package com.github.guyapooye.spaceutilities.util.fabric

import com.github.guyapooye.spaceutilities.fabric.SpaceUtilitiesFabric
import com.github.guyapooye.spaceutilities.networking.PacketChannel
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader

object PlatformUtilsImpl {
    @JvmStatic
    fun runWhenOn(e: Enum<*>, supplier: () -> Unit) {
        if (FabricLoader.getInstance().environmentType == e as EnvType) {
            supplier.invoke()
        }
    }
    @JvmStatic
    fun getPacketChannel(): PacketChannel {
        return SpaceUtilitiesFabric.PACKET_CHANNEL
    }
}