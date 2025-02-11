package com.github.guyapooye.spaceutilities.registries.fabric

import com.github.guyapooye.spaceutilities.SpaceUtilities.asResource
import com.github.guyapooye.spaceutilities.registries.BlockEntityEntry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import java.util.function.Supplier

object BlockEntityRegistryImpl {
    @JvmStatic
    fun <T : BlockEntity> register(
        key: String,
        factory: Supplier<BlockEntityType<T>>
    ): BlockEntityEntry<T> {
        val type: BlockEntityType<T> = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, asResource(key), factory.get())
        return BlockEntityEntry({ type }, key)
    }
}