package com.github.guyapooye.spaceutilities.networking.forge

import com.github.guyapooye.spaceutilities.SpaceUtilities
import com.github.guyapooye.spaceutilities.networking.ClientNetworkContext
import com.github.guyapooye.spaceutilities.networking.PacketChannel
import com.github.guyapooye.spaceutilities.networking.ServerNetworkContext
import com.github.guyapooye.spaceutilities.networking.packets.C2SSUPacket
import com.github.guyapooye.spaceutilities.networking.packets.S2CSUPacket
import com.github.guyapooye.spaceutilities.networking.packets.SUPacket
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.Function
import java.util.function.Supplier

class PacketChannelImpl : PacketChannel {
    private val channel: SimpleChannel = NetworkRegistry.newSimpleChannel(SpaceUtilities.NETWORK_CHANNEL,
        { "1" },
        { anObject: String? -> "1" == anObject },
        { anObject: String? -> "1" == anObject })
    private var id = 0

    override fun <T : SUPacket> registerPacket(clazz: Class<T>, decode: Function<FriendlyByteBuf, T>) {
        channel.registerMessage(
            id++, clazz,
            { obj: T, var1: FriendlyByteBuf? ->
                obj!!.write(
                    var1!!
                )
            }, decode,
            { packet: T, ctx: Supplier<NetworkEvent.Context> ->
                if (ctx.get().direction == NetworkDirection.PLAY_TO_SERVER) {
                    (packet as C2SSUPacket).handle(this.serverContext(ctx.get()))
                } else {
                    (packet as S2CSUPacket).handle(this.clientContext(ctx.get()))
                }
            })
    }

    private fun clientContext(ctx: NetworkEvent.Context): ClientNetworkContext {
        return object : ClientNetworkContext {
            override fun handled() {
            }

            override fun enqueueWork(runnable: Runnable) {
                ctx.enqueueWork(runnable)
            }

            override fun setPacketHandled(value: Boolean) {
                ctx.packetHandled = value
            }
        }
    }

    private fun serverContext(ctx: NetworkEvent.Context): ServerNetworkContext {
        return object : ServerNetworkContext {
            override fun handled() {
            }

            override val sender: ServerPlayer?
                get() = ctx.sender

            override fun enqueueWork(runnable: Runnable) {
                ctx.enqueueWork(runnable)
            }

            override fun setPacketHandled(value: Boolean) {
                ctx.packetHandled = value
            }
        }
    }

    override fun sendToNear(world: Level, pos: BlockPos, range: Int, message: S2CSUPacket) {
        channel.send(
            PacketDistributor.NEAR.with(
                PacketDistributor.TargetPoint.p(
                    pos.x.toDouble(),
                    pos.y.toDouble(),
                    pos.z.toDouble(),
                    range.toDouble(),
                    world.dimension()
                )
            ), message
        )
    }

    override fun sendToServer(packet: C2SSUPacket) {
        channel.send(PacketDistributor.SERVER.noArg(), packet)
    }

    override fun sendToClientsTracking(packet: S2CSUPacket, entity: Entity) {
        channel.send(PacketDistributor.TRACKING_ENTITY.with { entity }, packet)
    }

    override fun sendToClientsTrackingAndSelf(packet: S2CSUPacket, player: ServerPlayer) {
        channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with { player }, packet)
    }

    override fun sendTo(packet: S2CSUPacket, player: ServerPlayer) {
        channel.send(PacketDistributor.PLAYER.with { player }, packet)
    }
}
