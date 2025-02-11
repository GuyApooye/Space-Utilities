package com.github.guyapooye.spaceutilities.block.coupling.decoupler

import org.joml.Matrix4d
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl

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

            val force = normal.mul(force * totalDecouplers!!.size.toDouble())

//            println(attachmentPoint!!.add(.5,.5,.5, Vector3d()).toString(NumberFormat.getNumberInstance()))
//            println(force.toString(NumberFormat.getNumberInstance()))
//
//            println()
//
//            println(force.mul(-1.0, Vector3d()).toString(NumberFormat.getNumberInstance()))
//            println(otherShip?.transform?.worldToShip?.transformPosition(physShip.transform.shipToWorld.transformPosition(attachmentPoint, Vector3d()))?.sub(massCenterOffset)?.toString(NumberFormat.getNumberInstance()))


            val decouplerTotal = Vector3i()

            val size = totalDecouplers!!.size.toDouble()

            totalDecouplers!!.forEach {

                decouplerTotal.add(it.x,it.y,it.z)
            }
            val averagePos = Vector3d(
                decouplerTotal.x/size,
                decouplerTotal.y/size,
                decouplerTotal.z/size,
            )

            var shipOnToWorld = Matrix4d().identity()

            if (otherShip != null) {
                shipOnToWorld = otherShip.transform.shipToWorld as Matrix4d
                otherShip.applyInvariantForceToPos(force.mul(-1.0, Vector3d()).div((otherShip as PhysShipImpl).inertia.shipMass),averagePos.fma(-.5, normal))
            }

            physShip.applyInvariantForceToPos(force.div((physShip as PhysShipImpl).inertia.shipMass), physShip.transform.worldToShip.transformPosition(shipOnToWorld.transformPosition(averagePos)))

            shouldDecouple = false
        }
    }
}