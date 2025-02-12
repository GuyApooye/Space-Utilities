package com.github.guyapooye.spaceutilities.registries

import com.github.guyapooye.spaceutilities.SpaceUtilities.asResource
import com.github.guyapooye.spaceutilities.block.coupling.decoupler.DecouplerBlockEntity
import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import java.util.*
import java.util.function.Supplier

@Suppress("UNUSED_PARAMETER")
object BlockEntityRegistry {
    @JvmStatic
    @ExpectPlatform
    private fun <T : BlockEntity> register(key: String, factory: Supplier<BlockEntityType<T>>): BlockEntityEntry<T> {
        throw AssertionError()
    }

    private fun validBlocks(vararg blocks: BlockEntry<*>): Array<Block> {
        return Arrays.stream(blocks).map(BlockEntry<*>::get).toArray { arrayOfNulls<Block>(it) }
    }

    @JvmStatic
    val DECOUPLER = register("decoupler") {BlockEntityType.Builder.of(::DecouplerBlockEntity, BlockRegistry.DECOUPLER.get()).build(null)}

    fun register() {}

}

class BlockEntityEntry<T : BlockEntity>(private val factory: Supplier<BlockEntityType<T>>, key: String) {
    val key: ResourceLocation = asResource(key)

    fun get(): BlockEntityType<T> {
        return factory.get()
    }
}