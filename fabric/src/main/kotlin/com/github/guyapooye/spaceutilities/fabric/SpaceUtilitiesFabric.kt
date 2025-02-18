package com.github.guyapooye.spaceutilities.fabric

import com.github.guyapooye.spaceutilities.SpaceUtilities.asResource
import com.github.guyapooye.spaceutilities.SpaceUtilities.init
import com.github.guyapooye.spaceutilities.SpaceUtilities.initClient
import com.github.guyapooye.spaceutilities.SpaceUtilities.spaceUtilitiesTab
import com.github.guyapooye.spaceutilities.networking.PacketChannel
import com.github.guyapooye.spaceutilities.networking.fabric.PacketChannelImpl
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries

class SpaceUtilitiesFabric : ModInitializer {

    override fun onInitialize() {
        init()
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, asResource("general"), spaceUtilitiesTab)
    }

    companion object {
        @JvmStatic
        val PACKET_CHANNEL: PacketChannel = PacketChannelImpl()
    }
}

class SpaceUtilitiesClientFabric : ClientModInitializer {
    override fun onInitializeClient() {
        initClient()
    }
}
