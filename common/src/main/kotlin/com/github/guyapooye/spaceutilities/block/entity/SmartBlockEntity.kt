package com.github.guyapooye.spaceutilities.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

interface SmartBlockEntity {
    fun destroy() {}
//    fun place() {}
}
