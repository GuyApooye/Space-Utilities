package com.github.guyapooye.spaceutilities

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.addDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.guyapooye.spaceutilities.jackson.UndirectedMutableShipIdGraphDeserializer
import com.github.guyapooye.spaceutilities.registries.BlockEntityRegistry
import com.github.guyapooye.spaceutilities.registries.BlockRegistry
import com.github.guyapooye.spaceutilities.registries.ItemRegistry
import com.github.guyapooye.spaceutilities.registries.PacketRegistry
import com.github.guyapooye.spaceutilities.ship.ShipNetworkClient
import com.github.guyapooye.spaceutilities.ship.ShipNetworkClientHandler
import com.google.common.graph.MutableGraph
import com.mojang.logging.LogUtils
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Items
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.core.util.toImmutableSet
import org.valkyrienskies.mod.common.shipObjectWorld


object SpaceUtilities {
    const val MOD_ID = "spaceutilities"
    val LOGGER = LogUtils.getLogger()

    lateinit var spaceUtilitiesTab: CreativeModeTab

    @JvmStatic
    val NETWORK_CHANNEL: ResourceLocation = asResource("main")

    @JvmStatic
    fun init() {

        val objectMapper = jacksonObjectMapper().apply {
            registerModule(SimpleModule().apply {
                val deserializer = UndirectedMutableShipIdGraphDeserializer()
                addDeserializer(MutableGraph::class.java, deserializer)
                addDeserializer(MutableGraph::class.javaObjectType, deserializer)
            })
            registerKotlinModule()  // Register Kotlin module
        }

        ItemRegistry.register()
        BlockRegistry.register()
        BlockEntityRegistry.register()
        PacketRegistry.register()

        spaceUtilitiesTab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.spaceutilities")).icon { Items.IRON_BLOCK.defaultInstance }
            .displayItems { _, output -> ItemRegistry.allTabItems.forEach { output.accept(it) }}.build()

    }

    fun initClient() {
        BlockRegistry.registerRenderTypes()
        VSEvents.shipLoadEventClient.on { event ->
            run {
                ShipNetworkClientHandler.queuedUpdates.forEach { pair ->
                    run {
                        val network = ShipNetworkClientHandler.networks[pair.second]?: ShipNetworkClient(pair.second)
                        val mutableSelf = network.network.toMutableList()
                        val ship = Minecraft.getInstance().level.shipObjectWorld.allShips.getById(pair.first) as ClientShip
                        mutableSelf.add(ship)
                        network.updateNetwork(mutableSelf.toImmutableSet().asList())
                    }
                }
            }
        };
    }

    fun asResource(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }
}
