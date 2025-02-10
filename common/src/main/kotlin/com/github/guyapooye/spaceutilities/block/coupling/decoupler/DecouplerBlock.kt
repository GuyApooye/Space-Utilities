package com.github.guyapooye.spaceutilities.block.coupling.decoupler

import com.github.guyapooye.spaceutilities.block.entity.IEntityBlock
import net.minecraft.world.level.block.entity.BlockEntityType

class DecouplerBlock : IEntityBlock<Any?> {
    override fun getBlockEntityClass(): Class<Any?>? {
        return null
    }

    override fun getBlockEntityType(): BlockEntityType<*>? {
        return null
    }
}
