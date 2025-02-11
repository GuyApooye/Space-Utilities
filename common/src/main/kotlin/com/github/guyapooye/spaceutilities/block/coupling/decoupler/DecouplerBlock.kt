package com.github.guyapooye.spaceutilities.block.coupling.decoupler

import com.github.guyapooye.spaceutilities.block.entity.IEntityBlock
import com.github.guyapooye.spaceutilities.registries.BlockEntityRegistry
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.piston.PistonBaseBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.phys.BlockHitResult

class DecouplerBlock(properties: Properties) : DirectionalBlock(properties), IEntityBlock<DecouplerBlockEntity> {
    override val blockEntityClass: Class<DecouplerBlockEntity> = DecouplerBlockEntity::class.java

    override fun getBlockEntityType(): BlockEntityType<DecouplerBlockEntity> = BlockEntityRegistry.DECOUPLER.get()

    @Deprecated("Deprecated in Java")
    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (!player.mayBuild()) return InteractionResult.FAIL
        if (player.isShiftKeyDown) return InteractionResult.FAIL
        if (!player.getItemInHand(hand).isEmpty) return InteractionResult.PASS
        if (!level.isClientSide) {
            withBlockEntityDo(level, pos) {
                if (it.assembled) it.decouple()
                else {
                    it.assemble()
                }
            }
            return InteractionResult.PASS
        }
        return InteractionResult.SUCCESS
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return (defaultBlockState().setValue(
            FACING,
            context.nearestLookingDirection.opposite
        ) as BlockState)
    }

    @Deprecated("Deprecated in Java")
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        IEntityBlock.onRemove(state, level, pos, newState)
    }


    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(*arrayOf<Property<*>>(FACING))
    }

}
