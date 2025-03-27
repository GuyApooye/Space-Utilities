package com.github.guyapooye.spaceutilities.util

import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.shapes.VoxelShape

object SUShapes {
    val SLAB_UP: VoxelShape = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
    val SLAB_DOWN: VoxelShape = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0)
    val SLAB_SOUTH: VoxelShape = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0)
    val SLAB_NORTH: VoxelShape = Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0)
    val SLAB_EAST: VoxelShape = Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0)
    val SLAB_WEST: VoxelShape = Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 16.0)
}
