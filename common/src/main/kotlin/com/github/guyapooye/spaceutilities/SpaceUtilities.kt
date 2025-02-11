package com.github.guyapooye.spaceutilities

import com.github.guyapooye.spaceutilities.registries.BlockEntityRegistry
import com.github.guyapooye.spaceutilities.registries.BlockRegistry
import com.github.guyapooye.spaceutilities.registries.ItemRegistry
import com.mojang.logging.LogUtils
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Items

object SpaceUtilities {
    const val MOD_ID = "spaceutilities"
    val LOGGER = LogUtils.getLogger()

    lateinit var spaceUtilitiesTab: CreativeModeTab

    @JvmStatic
    fun init() {
        ItemRegistry.register()
        BlockRegistry.register()
        BlockEntityRegistry.register()

        spaceUtilitiesTab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.spaceutilities")).icon { Items.IRON_BLOCK.defaultInstance }
            .displayItems { _, output -> ItemRegistry.allTabItems.forEach { output.accept(it) }}.build()
    }

    fun initClient() {
        BlockRegistry.registerRenderTypes()
    }

    fun asResource(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }
}
