package com.github.guyapooye.spaceutilities.networking.packets

import net.minecraft.network.FriendlyByteBuf

interface SUPacket {
    fun write(byteBuf: FriendlyByteBuf)
}
