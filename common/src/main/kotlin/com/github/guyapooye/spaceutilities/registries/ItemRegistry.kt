package com.github.guyapooye.spaceutilities.registries

import com.github.guyapooye.spaceutilities.SpaceUtilities.asResource
import com.github.guyapooye.spaceutilities.registries.BlockRegistry.REPLACE_ME
import com.google.common.collect.ImmutableList
import dev.architectury.injectables.annotations.ExpectPlatform
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import java.util.function.Supplier

@Suppress("UNUSED_PARAMETER")
object ItemRegistry {

    val allTabItems: MutableList<ItemEntry<*>> = ArrayList()

    @JvmStatic
    @ExpectPlatform
    private fun <T : Item> register(key: String, factory: Supplier<T>): ItemEntry<T> {
        throw AssertionError()
    }

    private fun <T : Item> register(key: String, factory: Supplier<T>, renderType: RenderType = RenderType.solid(), showInTab: Boolean = true): ItemEntry<T> {
        val entry = register(key, factory)

        if (showInTab) allTabItems.add(entry)

        return entry
    }

    fun <T : Block?> registerBlockItem(key: String, value: Supplier<T>, showInTab: Boolean = true) {
        register(key, {
            BlockItem(
                value.get()!!,
                Item.Properties()
            )
        }, showInTab = showInTab)
    }

    fun register() {
    }

}

class ItemEntry<T : Item> (private val factory: Supplier<T>, key: String) : ItemLike{
    val key: ResourceLocation = asResource(key)

    fun asItemStack(): ItemStack {
        return ItemStack(get())
    }

    fun asItemStack(count: Int): ItemStack {
        return ItemStack(get(), count)
    }

    fun get(): T {
        return factory.get()
    }

    override fun asItem(): Item {
        return get()
    }
}