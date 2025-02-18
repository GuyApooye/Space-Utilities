package com.github.guyapooye.spaceutilities.util.forge

import com.github.guyapooye.spaceutilities.forge.SpaceUtilitiesForge
import com.github.guyapooye.spaceutilities.networking.PacketChannel
import net.minecraftforge.api.distmarker.Dist

object PlatformUtilsImpl {
    @JvmStatic
    fun runWhenOn(e: Enum<*>, supplier: () -> Unit) {
        thedarkcolour.kotlinforforge.forge.runWhenOn(e as Dist, supplier)
    }
    @JvmStatic
    fun getPacketChannel(): PacketChannel {
        return SpaceUtilitiesForge.PACKET_CHANNEL
    }
}
