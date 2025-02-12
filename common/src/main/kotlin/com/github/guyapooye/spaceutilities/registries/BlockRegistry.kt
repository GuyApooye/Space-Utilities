package com.github.guyapooye.spaceutilities.registries

import com.github.guyapooye.spaceutilities.SpaceUtilities.asResource
import com.github.guyapooye.spaceutilities.block.coupling.decoupler.DecouplerBlock
import com.github.guyapooye.spaceutilities.util.PlatformUtils
import dev.architectury.injectables.annotations.ExpectPlatform
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import java.util.function.Supplier

@Suppress("UNUSED_PARAMETER")
object BlockRegistry {

    @JvmStatic
    @ExpectPlatform
    private fun <T : Block> register(key: String, factory: Supplier<T>, showInTab: Boolean = true): BlockEntry<T> {
        throw AssertionError()
    }

    private fun <T : Block> register(key: String, factory: Supplier<T>, renderType: RenderType = RenderType.solid(), showInTab: Boolean = true): BlockEntry<T> {
        val entry = register(key, factory, showInTab)

        PlatformUtils.runWhenOn(EnvType.CLIENT) {
            BLOCK_TO_RENDERTYPE[entry] = renderType
        }

        return entry
    }

    @Environment(EnvType.CLIENT)
    private val BLOCK_TO_RENDERTYPE: HashMap<BlockEntry<*>, RenderType> = HashMap()

    @JvmStatic
    @ExpectPlatform
    fun registerRenderType(block: Block, renderType: RenderType) {
         throw AssertionError()
    }

    fun registerRenderTypes() {
        for (entry in BLOCK_TO_RENDERTYPE) {
            registerRenderType(entry.key.get(), entry.value)
        }
    }

    @JvmStatic
    val REPLACE_ME = register("replace_me", {
        Block(
            BlockBehaviour.Properties.of()
                .sound(SoundType.GLASS)
                .replaceable()
                .strength(25.0F, 600.0F)
                .noOcclusion()
//                .noCollission() /*doesnt work*/
                .isRedstoneConductor(this::never)
                .isSuffocating(this::never)
                .isViewBlocking(this::never)
                .isValidSpawn(this::never)
        )
    }, RenderType.translucent(), false)

    @JvmStatic
    val DECOUPLER = register("decoupler", {
        DecouplerBlock(
            BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .isValidSpawn(::never)
                .strength(1.5F)
                .mapColor(MapColor.COLOR_LIGHT_GRAY)
                .pushReaction(PushReaction.BLOCK)
        )
    }, RenderType.cutoutMipped(), true)

    fun register() {}

    fun never(a: BlockState, b: BlockGetter, c: BlockPos): Boolean {
        return false
    }
    fun always(a: BlockState, b: BlockGetter, c: BlockPos): Boolean {
        return true
    }
    fun <A> never(a: BlockState, b: BlockGetter, c: BlockPos, d: A): Boolean {
        return false
    }
    fun <A> always(a: BlockState, b: BlockGetter, c: BlockPos, d: A): Boolean {
        return true
    }

}

class BlockEntry<T : Block>(private val factory: Supplier<T>, key: String) : ItemLike {
    val key: ResourceLocation = asResource(key)

    override fun asItem(): Item {
        return get().asItem()
    }

    fun asItemStack(): ItemStack {
        return ItemStack(get())
    }

    fun asItemStack(count: Int): ItemStack {
        return ItemStack(get(), count)
    }

    fun get(): T {
        return factory.get()
    }
}