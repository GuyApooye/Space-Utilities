package com.github.guyapooye.spaceutilities.block.coupling.decoupler

import com.github.guyapooye.spaceutilities.SpaceUtilities.LOGGER
import com.github.guyapooye.spaceutilities.block.entity.ITickingBlockEntity
import com.github.guyapooye.spaceutilities.registries.BlockEntityRegistry
import com.github.guyapooye.spaceutilities.registries.BlockRegistry
import com.github.guyapooye.spaceutilities.util.simpleCreateShipsWithBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.joml.Matrix4d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSConstraintAndId
import org.valkyrienskies.core.apigame.constraints.VSFixedOrientationConstraint
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl.Companion.create
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import java.util.ArrayList
import java.util.Collections

class DecouplerBlockEntity(pos: BlockPos, blockState: BlockState, type: BlockEntityType<*> = BlockEntityRegistry.DECOUPLER.get()) : BlockEntity(type, pos,
    blockState
), ITickingBlockEntity {
    var assembled = false
    private var decoupled = false
    private var shipId = -1L
    private var shouldRefresh = false

    override fun saveAdditional(tag: CompoundTag) {
        tag.putBoolean("Assembled", assembled)
        tag.putBoolean("Decoupled", decoupled)
        tag.putLong("ShipId", shipId)
    }

    override fun load(tag: CompoundTag) {
        assembled = tag.getBoolean("Assembled")
        decoupled = tag.getBoolean("Decoupled")
        shipId = tag.getLong("ShipId")
        if (assembled) shouldRefresh = true
    }

    override fun tick() {
        if (level!!.isClientSide) return
        if (shouldRefresh) refresh()
    }

    private fun refresh() {
        (level as ServerLevel).run {
            val ship = shipObjectWorld.loadedShips.getById(shipId) ?: return

            val controller = ship.getAttachment<DecouplerController>() ?: return

            shipObjectWorld.removeConstraint(controller.attach!!.constraintId)
            shipObjectWorld.removeConstraint(controller.fixedOrientation!!.constraintId)

            val newAttach: VSAttachmentConstraint
            val newFixed: VSFixedOrientationConstraint

            (controller.attach!!.vsConstraint as VSAttachmentConstraint).run {
                newAttach = VSAttachmentConstraint(shipId0, shipId1, compliance, localPos0, localPos1, maxForce, fixedDistance)
            }

            (controller.fixedOrientation!!.vsConstraint as VSFixedOrientationConstraint).run {
                newFixed = VSFixedOrientationConstraint(shipId0, shipId1, compliance, localRot0, localRot1, maxTorque)
            }

            val newAttachId = shipObjectWorld.createNewConstraint(newAttach)
            val newFixedId = shipObjectWorld.createNewConstraint(newFixed)

            if (newAttachId == null || newFixedId == null) return

            controller.attach = VSConstraintAndId(newAttachId , newAttach)
            controller.fixedOrientation = VSConstraintAndId(newFixedId , newFixed)


            controller.totalDecouplers!!.forEach {
                getBlockEntity(BlockPos(it.x, it.y, it.z), BlockEntityRegistry.DECOUPLER.get()).get().shouldRefresh = false
            }

        }


    }

    override fun destroy() {
        if (!assembled || level!!.isClientSide) return

        (level as ServerLevel).run {
            val ship = shipObjectWorld.loadedShips.getById(shipId)!!

            val controller = ship.getAttachment<DecouplerController>()!!

            controller.totalDecouplers!!.remove(worldPosition.toJOML())

            if (controller.totalDecouplers!!.isEmpty()) {
                shipObjectWorld.removeConstraint(controller.attach!!.constraintId)
                shipObjectWorld.removeConstraint(controller.fixedOrientation!!.constraintId)
            }
        }
    }

    fun assemble() {

        if (level!!.isClientSide) {
            LOGGER.warn("DecouplerBlockEntity.assemble() called from Client side! This should not happen!")
        }

        (level as ServerLevel).run {
            val normal = blockState.getValue(DirectionalBlock.FACING)
            val directions = when (normal) {
                Direction.UP, Direction.DOWN  -> Pair(Direction.NORTH,Direction.WEST)
                Direction.NORTH, Direction.SOUTH -> Pair(Direction.UP,Direction.WEST)
                Direction.WEST, Direction.EAST -> Pair(Direction.UP,Direction.NORTH)
                else -> {
                    LOGGER.warn("Invalid direction for decoupler assembly, not assembling ship!")
                    return
                }
            }

            val totalDecouplers = DenseBlockPosSet()
            collectDecouplers(worldPosition, normal, directions.first, directions.second, this, totalDecouplers)

            val center = worldPosition.relative(normal)

            val ship = simpleCreateShipsWithBlock(center, totalDecouplers, this, BlockRegistry.REPLACE_ME.get().defaultBlockState())

            shipId = ship.id

            val shipOn = getShipManagingPos(worldPosition)

            var shipOnId = shipObjectWorld.dimensionToGroundBodyIdImmutable[this.dimensionId]

            val worldPosition = worldPosition.toJOMLD().add(.5, .5, .5)

            val worldNormal = normal.normal.toJOMLD()

            val shipNormal = Vector3d(worldNormal)

            var rotationInWorld = Quaterniond()


            val shipCenter = Vector3d(
                ((ship.chunkClaim.xMiddle shl 4) + (center.x and 15)).toDouble(),
                center.y.toDouble(),
                ((ship.chunkClaim.zMiddle shl 4) + (center.z and 15)).toDouble()
            )


            val worldCenter = center.toJOMLD().add(.5,.5,.5)

            var scaling = Vector3d(1.0)

            var worldToShipOn = Matrix4d().identity()

            val massCenterOffset = ship.inertiaData.centerOfMassInShip.sub(shipCenter, Vector3d())

            val centerBlockPosInWorld = massCenterOffset.add(worldCenter, Vector3d())


            if (shipOn != null) {


                shipOn.shipToWorld.transformPosition(worldPosition)
                shipOn.transform.shipToWorldRotation.transform(worldNormal)
                shipOn.shipToWorld.transformPosition(centerBlockPosInWorld)


                shipOnId = shipOn.id

                worldToShipOn = shipOn.worldToShip as Matrix4d?

                shipOn.shipToWorld.transformPosition(worldCenter)

                scaling = shipOn.transform.shipToWorldScaling as Vector3d
                rotationInWorld = shipOn.transform.shipToWorldRotation as Quaterniond
            }


            (ship as ShipDataCommon).transform = create(
                centerBlockPosInWorld,
                shipCenter,
                rotationInWorld,
                scaling
            )


            val localOrientation = Vector3d(1.0, 0.0, 0.0).rotationTo(shipNormal, Quaterniond())


            val attachmentPoint = ship.inertiaData.centerOfMassInShip.fma(-.5, shipNormal, Vector3d())


            val attachmentPointOnShip = Vector3d()


            val attachmentConstraint =
                VSAttachmentConstraint(shipId, shipOnId!!, 0.0, attachmentPoint.add(.5,.5,.5, attachmentPointOnShip), worldToShipOn.transformPosition(ship.shipToWorld.transformPosition(attachmentPoint, Vector3d())).sub(massCenterOffset), 10E10, 0.0)

            val attachId = shipObjectWorld.createNewConstraint(attachmentConstraint)

            val fixedOrientationConstraint = VSFixedOrientationConstraint(
                shipId,
                shipOnId,
                0.0,
                localOrientation,
                localOrientation,
                10E10
            )

            val fixedOrientId = shipObjectWorld.createNewConstraint(fixedOrientationConstraint)

            if (attachId == null || fixedOrientId == null) return


            val decouplersList: ArrayList<Vector3i> = ArrayList()

            totalDecouplers.forEach { x, y, z ->
                val pos = BlockPos(x, y, z).relative(normal, -1)
                val decoupler = getBlockEntity(pos, BlockEntityRegistry.DECOUPLER.get()).get()
                decouplersList.add(Vector3i(pos.x,pos.y,pos.z))
                decoupler.assembled = true
                decoupler.shipId = shipId

            }



            ship.saveAttachment(DecouplerController(attachmentPoint, massCenterOffset, VSConstraintAndId(attachId, attachmentConstraint), VSConstraintAndId(fixedOrientId, fixedOrientationConstraint), Collections.synchronizedList(decouplersList)))



        }


    }

    fun decouple() {
        if (!assembled || level!!.isClientSide || decoupled) return

        (level as ServerLevel).run {
            val ship = shipObjectWorld.loadedShips.getById(shipId)!!

            val controller = ship.getAttachment<DecouplerController>()!!

            shipObjectWorld.removeConstraint(controller.attach!!.constraintId)
            shipObjectWorld.removeConstraint(controller.fixedOrientation!!.constraintId)

            val shipOn = getShipManagingPos(worldPosition)

            var shipId = shipObjectWorld.dimensionToGroundBodyIdImmutable[dimensionId]

            if (shipOn != null) shipId = shipOn.id

            controller.decouple(blockState.getValue(DirectionalBlock.FACING).normal.toJOMLD(), 50.0, shipId!!)

            controller.totalDecouplers!!.forEach {
                getBlockEntity(BlockPos(it.x, it.y, it.z), BlockEntityRegistry.DECOUPLER.get()).get().decoupled = true
            }
        }
    }

    companion object {

        private fun collectDecouplers(currentPos: BlockPos, normal: Direction, direction1: Direction, direction2: Direction, level: Level, totalDecouplers: DenseBlockPosSet) {
            totalDecouplers.add(currentPos.relative(normal).toJOML())
            for (i in -1..1) {
                for (j in -1..1) {
                    val newPos = currentPos.relative(direction1, i).relative(direction2, j)
                    if (newPos.asLong() == currentPos.asLong() || totalDecouplers.contains(newPos.relative(normal).toJOML())) continue
                    if (level.getBlockState(newPos) == level.getBlockState(currentPos)) collectDecouplers(newPos, normal, direction1, direction2, level, totalDecouplers)
                }
            }
        }

    }
}
