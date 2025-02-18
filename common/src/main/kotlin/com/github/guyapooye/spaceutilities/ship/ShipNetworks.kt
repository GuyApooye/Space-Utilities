package com.github.guyapooye.spaceutilities.ship

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.guyapooye.spaceutilities.jackson.UndirectedMutableShipIdGraphDeserializer
import com.github.guyapooye.spaceutilities.networking.packets.ShipNetworkModificationPacket
import com.github.guyapooye.spaceutilities.util.PlatformUtils.getPacketChannel
import com.github.guyapooye.spaceutilities.util.getShipManagingPos
import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.ValkyrienSkiesMod.currentServer
import org.valkyrienskies.mod.common.shipObjectWorld
import java.util.concurrent.ConcurrentLinkedQueue


@Suppress("DEPRECATION")
class ShipNetwork {
    @Deprecated("Do not update directly!")
    @JsonDeserialize(using = UndirectedMutableShipIdGraphDeserializer::class)
    val network: MutableGraph<ShipId> = GraphBuilder.undirected().build()
    val id: Int

    val batteryPositions = ArrayList<Vector3i>()

    fun addNode(ship: ShipId) {
        network.addNode(ship)
        updateAllPlayers()
    }

    fun removeNode(ship: ShipId) {
        network.removeNode(ship)
        if (network.nodes().isEmpty()) {
            allNetworks.remove(this)
        }
        updateAllPlayers()
    }

    private fun updateAllPlayers() {
        currentServer!!.playerList.players.forEach { player ->
            getPacketChannel().sendTo(
                ShipNetworkModificationPacket(
                    network.nodes().toList(),
                    id
                ), player
            )
        }
    }

    fun addEdge(ship0: ShipId, ship1: ShipId) {
        network.putEdge(ship0, ship1)
        updateAllPlayers()
    }

    fun removeEdge(ship0: ShipId, ship1: ShipId) {
        network.removeEdge(ship0, ship1)
        updateAllPlayers()
    }

    fun mergeFrom(ship0: ServerShip, ship1: ServerShip) {
        val newNetwork = ShipNetwork()

        ship0.network.network.edges().forEach {
            newNetwork.addEdge(it.nodeU(), it.nodeV())
        }

        ship1.network.network.edges().forEach {
            newNetwork.addEdge(it.nodeU(), it.nodeV())
        }

        ship0.network = newNetwork
        ship1.network = newNetwork

        newNetwork.addEdge(ship0.id, ship1.id)
    }


    fun splitFrom(ship0: ServerShip, ship1: ServerShip) {
        removeEdge(ship0.id, ship1.id)

        val networkRed = ShipNetwork()
        val networkGreen = ShipNetwork()

        addNearbyToNetwork(null ,ship0, networkRed, currentServer!!.shipObjectWorld)
        addNearbyToNetwork(null, ship1, networkGreen, currentServer!!.shipObjectWorld)

        batteryPositions.forEach {
            val ship = currentServer!!.getShipManagingPos(it.x, it.y) ?: return
            if (ship.network == networkRed) {
                networkRed.batteryPositions.add(it)
            }
            else if (ship.network == networkGreen) {
                networkGreen.batteryPositions.add(it)
            }
        }
    }

    private fun addNearbyToNetwork(previousNode: ServerShip?, node: ServerShip, newNetwork: ShipNetwork, shipObjectWorld: ServerShipWorldCore) {
        if (node.network != this) return /*Shouldn't really happen*/
        node.network = newNetwork

        if (previousNode != null) newNetwork.addEdge(previousNode.id, node.id)
        else newNetwork.addNode(node.id)

        val adjacentNodes = network.adjacentNodes(node.id)
        removeNode(node.id)
        adjacentNodes.forEach { shipObjectWorld.allShips.getById(it)
            ?.let { it1 -> addNearbyToNetwork(node, it1, newNetwork, shipObjectWorld) } }
    }

    companion object {

        val allNetworks = ArrayList<ShipNetwork>()

        var lastId = 0

        var ServerShip.network: ShipNetwork
            get() {
                var attach = getAttachment<ShipNetwork>()
                if (attach == null) {
                    attach = ShipNetwork()
                    val loadedSelf = currentServer!!.shipObjectWorld.loadedShips.getById(this.id)
                    if (loadedSelf != null) {
                        loadedSelf.saveAttachment(attach)
                    } else {
                        this.saveAttachment(attach)
                    }
                }
                return attach
            }
            set(value) {
                val loadedSelf = currentServer!!.shipObjectWorld.loadedShips.getById(this.id)
                if (loadedSelf != null) {
                    loadedSelf.saveAttachment(value)
                } else {
                    this.saveAttachment(value)
                }
            }
    }

    init {
        allNetworks.add(this)
        id = lastId++
    }
}
class ShipNetworkClient(val id: Int) {
    lateinit var network: List<ClientShip>
        private set

    constructor(id: Int, newNetwork: List<ClientShip>) : this(id) {
        network = newNetwork
    }

    fun updateNetwork(newNetwork: List<ClientShip>) {
        network = newNetwork
    }
}

