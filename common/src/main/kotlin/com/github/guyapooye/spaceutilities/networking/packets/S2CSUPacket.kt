package com.github.guyapooye.spaceutilities.networking.packets

import com.github.guyapooye.spaceutilities.networking.ClientNetworkContext

interface S2CSUPacket : SUPacket {
    fun handle(ctx: ClientNetworkContext)
}
