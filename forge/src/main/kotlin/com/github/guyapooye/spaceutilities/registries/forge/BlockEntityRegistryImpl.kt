package com.github.guyapooye.spaceutilities.registries.forge

import com.github.guyapooye.spaceutilities.SpaceUtilities.MOD_ID
import com.github.guyapooye.spaceutilities.registries.BlockEntityEntry
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.Supplier

object BlockEntityRegistryImpl {
    val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID)

    @JvmStatic
    fun <T : BlockEntity> register(
        key: String,
        factory: Supplier<BlockEntityType<T>>
    ): BlockEntityEntry<T> {
        return BlockEntityEntry(BLOCK_ENTITIES.register(key, factory), key)
    }
}
