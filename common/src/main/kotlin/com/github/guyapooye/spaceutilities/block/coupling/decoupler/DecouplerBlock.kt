package com.github.guyapooye.spaceutilities.block.coupling.decoupler

import com.github.guyapooye.spaceutilities.block.entity.IEntityBlock
import com.github.guyapooye.spaceutilities.block.properties.Tier
import com.github.guyapooye.spaceutilities.registries.BlockEntityRegistry
import com.github.guyapooye.spaceutilities.registries.BlockRegistry
import com.github.guyapooye.spaceutilities.util.SUShapes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction

import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

abstract class DecouplerBlock(properties: Properties) : DirectionalBlock(properties), IEntityBlock<DecouplerBlockEntity> {
    override val blockEntityClass: Class<DecouplerBlockEntity> = DecouplerBlockEntity::class.java

    constructor() : this(Properties.of()
        .sound(SoundType.STONE)
        .isValidSpawn(BlockRegistry::never)
        .strength(1.5F)
        .mapColor(MapColor.COLOR_LIGHT_GRAY)
        .pushReaction(PushReaction.BLOCK))

    override fun getBlockEntityType(): BlockEntityType<DecouplerBlockEntity> = BlockEntityRegistry.DECOUPLER.get()

    abstract val tier: Tier

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
        return defaultBlockState().setValue(FACING, context.nearestLookingDirection.opposite) as BlockState
    }

    @Deprecated("Deprecated in Java")
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        IEntityBlock.onRemove(state, level, pos, newState)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val dir = state.getValue(FACING)
        return when (dir) {
            Direction.UP -> SUShapes.SLAB_UP
            Direction.DOWN -> SUShapes.SLAB_DOWN
            Direction.NORTH -> SUShapes.SLAB_NORTH
            Direction.SOUTH -> SUShapes.SLAB_SOUTH
            Direction.EAST -> SUShapes.SLAB_EAST
            Direction.WEST -> SUShapes.SLAB_WEST
            else -> SUShapes.SLAB_UP
        }
    }

    @Deprecated("Deprecated in Java")
    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        if (!level.isClientSide) {
            if (level.hasNeighborSignal(pos)) {
                withBlockEntityDo(level, pos) {
                    if (it.assembled) it.decouple()
                }
            }
        }
    }
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder.add(FACING))
    }
}
