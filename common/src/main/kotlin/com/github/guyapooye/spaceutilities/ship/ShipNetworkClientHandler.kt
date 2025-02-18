package com.github.guyapooye.spaceutilities.ship

import com.github.guyapooye.spaceutilities.networking.packets.ShipNetworkModificationPacket
import net.minecraft.client.Minecraft
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.mod.common.shipObjectWorld
import java.util.LinkedList

object ShipNetworkClientHandler {
    val networks = HashMap<Int, ShipNetworkClient>()

    val queuedUpdates = LinkedList<Pair<ShipId, Int>>()

    fun updateNetwork(id: Int, newNetwork: List<Long>) {

        val shipsNetwork = ArrayList<ClientShip>()

        for (i in newNetwork) {
            val ship = Minecraft.getInstance().level.shipObjectWorld.allShips.getById(i)
            if (ship == null) {
                queuedUpdates.add(Pair(i, id))
                continue
            }
            shipsNetwork.add(ship)
        }

        if (!networks.contains(id)) {
            networks[id] = ShipNetworkClient(id, shipsNetwork)
            return
        }
        networks[id]?.updateNetwork(shipsNetwork)
    }
}