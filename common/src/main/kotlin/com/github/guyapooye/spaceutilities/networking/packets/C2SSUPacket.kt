package com.github.guyapooye.spaceutilities.networking.packets

import com.github.guyapooye.spaceutilities.networking.ServerNetworkContext


interface C2SSUPacket : SUPacket {
    fun handle(ctx: ServerNetworkContext)
}
