package com.github.guyapooye.spaceutilities.networking

import net.minecraft.server.level.ServerPlayer

interface ServerNetworkContext : NetworkContext {
    val sender: ServerPlayer?
}
