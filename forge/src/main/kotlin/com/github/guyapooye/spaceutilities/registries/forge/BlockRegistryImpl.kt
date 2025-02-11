package com.github.guyapooye.spaceutilities.registries.forge

import com.github.guyapooye.spaceutilities.SpaceUtilities.LOGGER
import com.github.guyapooye.spaceutilities.SpaceUtilities.MOD_ID
import com.github.guyapooye.spaceutilities.registries.BlockEntry
import com.github.guyapooye.spaceutilities.registries.BlockRegistry
import com.github.guyapooye.spaceutilities.registries.ItemRegistry
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.block.Block
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.tags.ITagManager
import java.util.function.Supplier

object BlockRegistryImpl {
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)
    @JvmStatic
    fun <T : Block> register(key: String, factory: Supplier<T>, showInTab: Boolean = true): BlockEntry<T> {
        val block = BLOCKS.register(key, factory)!!
        val entry: BlockEntry<T> = BlockEntry(block, key)
        ItemRegistry.registerBlockItem(key, block, showInTab)
        return entry
    }

    @Suppress("DEPRECATION")
    @JvmStatic
    fun registerRenderType(block: Block, renderType: RenderType) {
        ItemBlockRenderTypes.setRenderLayer(block, renderType)
    }
}

