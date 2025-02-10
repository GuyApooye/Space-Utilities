package com.github.guyapooye.spaceutilities.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState

class BlockEntityTicker<T : BlockEntity?> :
    BlockEntityTicker<T> {
    override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: T) {
        if (!blockEntity!!.hasLevel()) {
            blockEntity.level = level
        }

        (blockEntity as ITickingBlockEntity).tick()
    }
}
