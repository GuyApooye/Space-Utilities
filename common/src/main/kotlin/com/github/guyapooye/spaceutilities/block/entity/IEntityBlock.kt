package com.github.guyapooye.spaceutilities.block.entity

import com.github.guyapooye.spaceutilities.block.coupling.decoupler.DecouplerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import java.util.*
import java.util.function.Consumer
import java.util.function.Function



interface IEntityBlock<T : BlockEntity> : EntityBlock {
    val blockEntityClass: Class<T>

    fun getBlockEntityType(): BlockEntityType<out T>

    fun withBlockEntityDo(world: BlockGetter, pos: BlockPos, action: Consumer<T>?) {
        getBlockEntityOptional(world, pos).ifPresent(action!!)
    }

    fun onBlockEntityUse(world: BlockGetter, pos: BlockPos, action: Function<T, InteractionResult>): InteractionResult {
        return getBlockEntityOptional(world, pos).map(action).orElse(InteractionResult.PASS)
    }

    fun getBlockEntityOptional(world: BlockGetter, pos: BlockPos): Optional<T> {
        return Optional.ofNullable(this.getBlockEntity(world, pos))
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return getBlockEntityType().create(pos, state)
    }

    override fun <S : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntity: BlockEntityType<S>
    ): net.minecraft.world.level.block.entity.BlockEntityTicker<S>? {
        return if (level.isClientSide) null else if (ITickingBlockEntity::class.java.isAssignableFrom(this.blockEntityClass)) BlockEntityTicker() else null
    }

    @Suppress("UNCHECKED_CAST")
    fun getBlockEntity(worldIn: BlockGetter, pos: BlockPos): T? {
        val blockEntity = worldIn.getBlockEntity(pos)
        val expectedClass = this.blockEntityClass
        return if (blockEntity == null) {
            null
        } else {
            (if (!expectedClass.isInstance(blockEntity)) null else blockEntity) as T?
        }
    }

    companion object {
        fun onRemove(blockState: BlockState, level: Level, pos: BlockPos, newBlockState: BlockState) {
            if (blockState.hasBlockEntity()) {
                if (!blockState.`is`(newBlockState.block) || !newBlockState.hasBlockEntity()) {
                    val be = level.getBlockEntity(pos)
                    if (be is SmartBlockEntity) {

                        (be as SmartBlockEntity).destroy()
                    }

                    level.removeBlockEntity(pos)
                }
            }
        }
    }
}
