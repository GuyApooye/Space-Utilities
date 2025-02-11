package com.github.guyapooye.spaceutilities.registries.fabric

import com.github.guyapooye.spaceutilities.SpaceUtilities.asResource
import com.github.guyapooye.spaceutilities.registries.BlockEntry
import com.github.guyapooye.spaceutilities.registries.ItemRegistry
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import java.util.function.Supplier

object BlockRegistryImpl {
    @JvmStatic
    fun <T : Block> register(key: String, factory: Supplier<T>, showInTab: Boolean = true): BlockEntry<T> {
        val block: T = Registry.register(BuiltInRegistries.BLOCK, asResource(key), factory.get())
        ItemRegistry.registerBlockItem(key, { block }, showInTab)
        return BlockEntry({ block }, key)
    }

    @JvmStatic
    fun registerRenderType(block: Block, renderType: RenderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType)
    }
}