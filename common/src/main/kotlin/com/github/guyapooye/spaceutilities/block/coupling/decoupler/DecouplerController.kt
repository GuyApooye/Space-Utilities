package com.github.guyapooye.spaceutilities.block.coupling.decoupler

import org.joml.Matrix4d
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.text.NumberFormat

@Suppress("DEPRECATION")
class DecouplerController(private val multiplier: Double, private val attachmentPoint: Vector3d?, private val massCenterOffset: Vector3d?, var attach: VSConstraintAndId?, var fixedOrientation: VSConstraintAndId?, var totalDecouplers: MutableList<Vector3i>?) : ShipForcesInducer {

    private var force = 1.0

    private var otherShipId = -1L

    private var normal: Vector3d = Vector3d()

    private var shouldDecouple = false

    fun decouple(normal: Vector3d, otherShip: ShipId) {
        if (otherShip == -1L) return
        this.normal = normal
        this.force = 1000.0 * multiplier
        this.otherShipId = otherShip
        shouldDecouple = true
    }

    override fun applyForces(physShip: PhysShip) {}

    override fun applyForcesAndLookupPhysShips(physShip: PhysShip, lookupPhysShip: (ShipId) -> PhysShip?) {
        if (shouldDecouple) {
            val otherShip = lookupPhysShip.invoke(otherShipId)

            physShip as PhysShipImpl

            val size = totalDecouplers!!.size.toDouble()

            val force = normal.mul(force * size * 1000, Vector3d())

            val decouplerTotal = Vector3i()

            totalDecouplers!!.forEach {
                decouplerTotal.add(it.x,it.y,it.z)
            }
            val averagePos = Vector3d(
                decouplerTotal.x/size,
                decouplerTotal.y/size,
                decouplerTotal.z/size,
            ).add(.5,.5,.5)

            val firstPos = averagePos.fma(.5, normal)

            if (otherShip != null) {
                otherShip.applyRotDependentForceToPos(force.negate(Vector3d()), firstPos.sub(otherShip.transform.positionInShip, Vector3d()))
                otherShip.transform.shipToWorld.transformPosition(averagePos)
            } else {
                force.mul(2.0)
            }


            val secondPos = physShip.transform.worldToShip.transformPosition(averagePos, Vector3d()).sub(physShip.transform.positionInShip, Vector3d())

            physShip.applyRotDependentForceToPos(force, secondPos)

            shouldDecouple = false
        }
    }
}