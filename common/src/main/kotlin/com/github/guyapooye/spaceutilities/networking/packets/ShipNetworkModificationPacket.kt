package com.github.guyapooye.spaceutilities.networking.packets

import com.github.guyapooye.spaceutilities.networking.ClientNetworkContext
import com.github.guyapooye.spaceutilities.ship.ShipNetworkClientHandler
import net.minecraft.network.FriendlyByteBuf

class ShipNetworkModificationPacket : S2CSUPacket {

    val graph: List<Long>
    val id: Int

    constructor(byteBuf: FriendlyByteBuf) {
        graph = byteBuf.readLongArray().asList()
        id = byteBuf.readInt()
    }

    constructor(graph: List<Long>, id: Int) {
        this.graph = graph
        this.id = id
    }

    override fun handle(ctx: ClientNetworkContext) {
        ShipNetworkClientHandler.updateNetwork(id, graph)
        ctx.handled()
    }

    override fun write(byteBuf: FriendlyByteBuf) {
        byteBuf.writeLongArray(graph.toLongArray())
        byteBuf.writeInt(id)
    }
}