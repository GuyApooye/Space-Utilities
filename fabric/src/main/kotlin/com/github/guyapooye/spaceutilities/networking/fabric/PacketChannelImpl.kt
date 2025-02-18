package com.github.guyapooye.spaceutilities.networking.fabric

import com.github.guyapooye.spaceutilities.SpaceUtilities.NETWORK_CHANNEL
import com.github.guyapooye.spaceutilities.networking.ClientNetworkContext
import com.github.guyapooye.spaceutilities.networking.PacketChannel
import com.github.guyapooye.spaceutilities.networking.ServerNetworkContext
import com.github.guyapooye.spaceutilities.networking.packets.C2SSUPacket
import com.github.guyapooye.spaceutilities.networking.packets.S2CSUPacket
import com.github.guyapooye.spaceutilities.networking.packets.SUPacket
import com.github.guyapooye.spaceutilities.util.PlatformUtils.runWhenOn
import io.netty.buffer.PooledByteBufAllocator
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.Function

class PacketChannelImpl : PacketChannel {
    private val c2sIdMap: MutableMap<Class<out C2SSUPacket>, Int> = HashMap()
    private val s2cIdMap: MutableMap<Class<out S2CSUPacket>, Int> = HashMap()
    private val c2sDecoderMap: Int2ObjectMap<Function<FriendlyByteBuf, out C2SSUPacket>> = Int2ObjectOpenHashMap()
    private val s2cDecoderMap: Int2ObjectMap<Function<FriendlyByteBuf, out S2CSUPacket>> = Int2ObjectOpenHashMap()
    private val bufAllocator = PooledByteBufAllocator(true)
    private var idCounter = 0

    init {
        if (!ServerPlayNetworking.registerGlobalReceiver(
                NETWORK_CHANNEL
            ) { server: MinecraftServer, player: ServerPlayer, handler: ServerGamePacketListenerImpl?, buf: FriendlyByteBuf, responseSender: PacketSender? ->
                val id = buf.readVarInt()
                val decoder = c2sDecoderMap[id]
                if (decoder == null) {
                    throw RuntimeException("Unknown packet id: $id")
                } else {
                    val packet = decoder.apply(buf)
                    packet.handle(this.serverContext(server, player))
                }
            }
        ) {
            throw RuntimeException("Failed to register server packet handler")
        } else {
            runWhenOn(EnvType.CLIENT) {
                if (!ClientPlayNetworking.registerGlobalReceiver(
                        NETWORK_CHANNEL
                    ) { client: Minecraft, handler: ClientPacketListener?, buf: FriendlyByteBuf, responseSender: PacketSender? ->
                        val id = buf.readVarInt()
                        val decoder = s2cDecoderMap[id]
                        if (decoder == null) {
                            throw RuntimeException("Unknown packet id: $id")
                        } else {
                            val packet = decoder.apply(buf)
                            packet.handle(this.clientContext(client))
                        }
                    }
                ) {
                    throw RuntimeException("Failed to register client packet handler")
                }
            }
        }
    }

    override fun <T : SUPacket> registerPacket(clazz: Class<T>, decode: Function<FriendlyByteBuf, T>) {
        if (C2SSUPacket::class.java.isAssignableFrom(clazz)) {
            c2sIdMap[clazz as Class<out C2SSUPacket>] = this.idCounter
            c2sDecoderMap.put(this.idCounter, decode as Function<FriendlyByteBuf, out C2SSUPacket>)
        } else {
            if (!S2CSUPacket::class.java.isAssignableFrom(clazz)) {
                throw RuntimeException()
            }
            s2cIdMap[clazz as Class<out S2CSUPacket>] = this.idCounter
            s2cDecoderMap.put(this.idCounter, decode as Function<FriendlyByteBuf, out S2CSUPacket>)
        }
        ++this.idCounter
    }

    private fun clientContext(executor: Executor): ClientNetworkContext {
        return object : ClientNetworkContext {
            override fun handled() {
            }

            override fun enqueueWork(runnable: Runnable) {
                executor.execute(runnable)
            }

            override fun setPacketHandled(value: Boolean) {
            }
        }
    }

    private fun serverContext(executor: Executor, player: ServerPlayer): ServerNetworkContext {
        return object : ServerNetworkContext {
            override fun handled() {
            }

            override val sender: ServerPlayer
                get() = player

            override fun enqueueWork(runnable: Runnable) {
                executor.execute(runnable)
            }

            override fun setPacketHandled(value: Boolean) {
            }
        }
    }

    override fun sendToNear(level: Level, pos: BlockPos, range: Int, message: S2CSUPacket) {
        PlayerLookup.around(level as ServerLevel, pos, range.toDouble()).forEach(
            Consumer { player: ServerPlayer -> this.sendTo(player, message) })
    }

    override fun sendToServer(packet: C2SSUPacket) {
        val buf = FriendlyByteBuf(bufAllocator.buffer())
        buf.writeVarInt(c2sIdMap[packet.javaClass]!!)
        packet.write(buf)
        ClientPlayNetworking.send(NETWORK_CHANNEL, buf)
    }

    override fun sendToClientsTracking(packet: S2CSUPacket, entity: Entity) {
        PlayerLookup.tracking(entity).forEach(Consumer { player: ServerPlayer -> this.sendTo(player, packet) })
    }

    override fun sendToClientsTrackingAndSelf(packet: S2CSUPacket, player: ServerPlayer) {
        PlayerLookup.tracking(player).forEach(Consumer { p: ServerPlayer -> this.sendTo(p, packet) })
        this.sendTo(player, packet)
    }

    private fun sendTo(player: ServerPlayer, packet: S2CSUPacket) {
        val buf = FriendlyByteBuf(bufAllocator.buffer())
        buf.writeVarInt(s2cIdMap[packet.javaClass]!!)
        packet.write(buf)
        ServerPlayNetworking.send(player, NETWORK_CHANNEL, buf)
    }

    override fun sendTo(packet: S2CSUPacket, player: ServerPlayer) {
        this.sendTo(player, packet)
    }
}
