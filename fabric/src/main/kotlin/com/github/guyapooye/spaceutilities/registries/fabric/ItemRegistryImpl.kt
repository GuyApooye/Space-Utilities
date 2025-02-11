package com.github.guyapooye.spaceutilities.registries.fabric

import com.github.guyapooye.spaceutilities.SpaceUtilities.asResource
import com.github.guyapooye.spaceutilities.registries.ItemEntry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import java.util.function.Supplier

object ItemRegistryImpl {
    @JvmStatic
    fun <T : Item> register(key: String, factory: Supplier<T>): ItemEntry<T> {
        val item: T = Registry.register(BuiltInRegistries.ITEM, asResource(key), factory.get())
        return ItemEntry({ item }, key)
    }
}