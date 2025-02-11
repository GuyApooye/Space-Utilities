package com.github.guyapooye.spaceutilities.registries.forge

import com.github.guyapooye.spaceutilities.SpaceUtilities.MOD_ID
import com.github.guyapooye.spaceutilities.registries.ItemEntry
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.Supplier

object ItemRegistryImpl {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)
    @JvmStatic
    fun <T : Item> register(key: String, factory: Supplier<T>): ItemEntry<T> {
        val item = ITEMS.register(key, factory)
        return ItemEntry(item, key)
    }
    fun registerRenderType(item: Item, renderType: RenderType) {
        throw AssertionError()
    }
}