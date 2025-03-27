package com.github.guyapooye.spaceutilities.event

import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.impl.util.events.EventEmitterImpl

object SUEvents {
    val shipDeleteHook = EventEmitterImpl<DeleteShipEvent>()

    data class DeleteShipEvent(val ship: ServerShip)
}