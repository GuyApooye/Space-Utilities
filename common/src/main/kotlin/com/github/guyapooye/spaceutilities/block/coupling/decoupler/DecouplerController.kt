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
class DecouplerController(private val attachmentPoint: Vector3d?, private val massCenterOffset: Vector3d?, var attach: VSConstraintAndId?, var fixedOrientation: VSConstraintAndId?, var totalDecouplers: MutableList<Vector3i>?) : ShipForcesInducer {

    private var force = 1.0

    private var otherShipId = -1L

    private var normal: Vector3d = Vector3d()

    private var shouldDecouple = false

    fun decouple(normal: Vector3d, force: Double, otherShip: ShipId) {
        if (otherShip == -1L) return
        this.normal = normal
        this.force = force
        this.otherShipId = otherShip
        shouldDecouple = true
    }

    override fun applyForces(physShip: PhysShip) {}

    override fun applyForcesAndLookupPhysShips(physShip: PhysShip, lookupPhysShip: (ShipId) -> PhysShip?) {
        if (shouldDecouple) {
            val otherShip = lookupPhysShip.invoke(otherShipId)

            physShip as PhysShipImpl

            val force = normal.mul(force * physShip.inertia.shipMass, Vector3d())

            val decouplerTotal = Vector3i()

            val size = totalDecouplers!!.size.toDouble()

            totalDecouplers!!.forEach {
                decouplerTotal.add(it.x,it.y,it.z)
            }
            val averagePos = Vector3d(
                decouplerTotal.x/size,
                decouplerTotal.y/size,
                decouplerTotal.z/size,
            ).add(.5,.5,.5)

            val firstPos = averagePos.fma(.5, normal)

            println(force.toString(NumberFormat.getInstance()))

            if (otherShip != null) {
                otherShip.applyRotDependentForceToPos(force.negate(Vector3d()), firstPos.sub(otherShip.transform.positionInShip, Vector3d()))
                println(firstPos.toString(NumberFormat.getInstance()))
                otherShip.transform.shipToWorld.transformPosition(averagePos)
            }


            val secondPos = physShip.transform.worldToShip.transformPosition(averagePos, Vector3d()).sub(physShip.transform.positionInShip, Vector3d())

            println(secondPos.toString(NumberFormat.getInstance()))

            physShip.applyRotDependentForceToPos(force, secondPos)

            shouldDecouple = false
        }
    }
}