package com.github.guyapooye.spaceutilities.networking

import com.github.guyapooye.spaceutilities.networking.packets.C2SSUPacket
import com.github.guyapooye.spaceutilities.networking.packets.SUPacket
import com.github.guyapooye.spaceutilities.networking.packets.S2CSUPacket
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import java.util.function.Function

interface PacketChannel {
    fun <T : SUPacket> registerPacket(clazz: Class<T>, function: Function<FriendlyByteBuf, T>)
    fun sendToNear(level: Level, pos: BlockPos, range: Int, packet: S2CSUPacket)
    fun sendToServer(packet: C2SSUPacket)
    fun sendToClientsTracking(packet: S2CSUPacket, entity: Entity)
    fun sendToClientsTrackingAndSelf(packet: S2CSUPacket, player: ServerPlayer)
    fun sendTo(packet: S2CSUPacket, player: ServerPlayer)
}
