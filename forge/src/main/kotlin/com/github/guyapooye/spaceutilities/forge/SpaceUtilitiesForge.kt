package com.github.guyapooye.spaceutilities.forge

import com.github.guyapooye.spaceutilities.SpaceUtilities
import com.github.guyapooye.spaceutilities.SpaceUtilities.MOD_ID
import com.github.guyapooye.spaceutilities.SpaceUtilities.init
import com.github.guyapooye.spaceutilities.SpaceUtilities.spaceUtilitiesTab
import com.github.guyapooye.spaceutilities.registries.forge.BlockEntityRegistryImpl
import com.github.guyapooye.spaceutilities.registries.forge.BlockRegistryImpl
import com.github.guyapooye.spaceutilities.registries.forge.ItemRegistryImpl
import com.github.guyapooye.spaceutilities.util.PlatformUtils.runWhenOn
import net.minecraft.core.registries.Registries
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.KotlinModLoadingContext
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.test.KotlinForForge

@Mod(MOD_ID)
class SpaceUtilitiesForge {
    init {
        val tabRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID)

        val eventBus = KotlinModLoadingContext.get().getKEventBus()

        init()

        tabRegister.register("general") { spaceUtilitiesTab }
        tabRegister.register(eventBus)

        BlockRegistryImpl.BLOCKS.register(eventBus)
        ItemRegistryImpl.ITEMS.register(eventBus)
        BlockEntityRegistryImpl.BLOCK_ENTITIES.register(eventBus)

        runWhenOn(Dist.CLIENT) {
            MOD_BUS.addListener(::initClient)
        }

    }

    private fun initClient(event: FMLClientSetupEvent) {
        SpaceUtilities.initClient()
    }

}
