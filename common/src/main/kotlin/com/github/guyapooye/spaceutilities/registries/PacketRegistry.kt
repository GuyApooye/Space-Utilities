package com.github.guyapooye.spaceutilities.registries

import com.github.guyapooye.spaceutilities.networking.PacketChannel
import com.github.guyapooye.spaceutilities.networking.packets.SUPacket
import com.github.guyapooye.spaceutilities.networking.packets.ShipNetworkModificationPacket
import com.github.guyapooye.spaceutilities.util.PlatformUtils
import net.minecraft.network.FriendlyByteBuf
import java.util.function.Function

object PacketRegistry {

    @JvmStatic
    var SHIP_NETWORK_MODIFICATION: PacketEntry<ShipNetworkModificationPacket> = PacketEntry.register(
        ShipNetworkModificationPacket::class.java
    ) { ShipNetworkModificationPacket(it) }

    fun register() {
    }


}
data class PacketEntry<T : SUPacket?>(val type: Class<T>, val factory: Function<FriendlyByteBuf, T>) {
    companion object {
        fun <R : SUPacket> register(
            type: Class<R>,
            factory: Function<FriendlyByteBuf, R>
        ): PacketEntry<R> {
            val entry = PacketEntry(type, factory)
            val packetChannel: PacketChannel = PlatformUtils.getPacketChannel()
            val clazz = entry.type
            val packetFactory = entry.factory
            packetChannel.registerPacket(clazz, packetFactory)
            return entry
        }
    }
}
