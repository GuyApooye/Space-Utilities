package com.github.guyapooye.spaceutilities.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.impl.game.ships.ShipData
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.core.impl.networking.simple.sendToClient
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.mod.common.*
import org.valkyrienskies.mod.common.networking.PacketRestartChunkUpdates
import org.valkyrienskies.mod.common.networking.PacketStopChunkUpdates
import org.valkyrienskies.mod.common.util.toJOML

/**
 * Does not remove the blocks in [blocks].
 *
 * Creates a ship containing only [block]
 * */
fun simpleCreateShipsWithBlock(
    centerBlock: BlockPos, blocks: DenseBlockPosSet, level: ServerLevel, block: BlockState
): ServerShip {
    if (blocks.isEmpty()) throw IllegalArgumentException()

    val ship = level.shipObjectWorld.createNewShipAtBlock(centerBlock.toJOML(), false, 1.0, level.dimensionId)

    val shipChunkX = ship.chunkClaim.xMiddle
    val shipChunkZ = ship.chunkClaim.zMiddle

    val worldChunkX = centerBlock.x shr 4
    val worldChunkZ = centerBlock.z shr 4

    val deltaX = worldChunkX - shipChunkX
    val deltaZ = worldChunkZ - shipChunkZ

    val chunksToBeUpdated = mutableMapOf<ChunkPos, Pair<ChunkPos, ChunkPos>>()
    blocks.forEachChunk { x, _, z, _ ->
        val sourcePos = ChunkPos(x, z)
        val destPos = ChunkPos(x - deltaX, z - deltaZ)
        chunksToBeUpdated[sourcePos] = Pair(sourcePos, destPos)
    }
    val chunkPairs = chunksToBeUpdated.values.toList()
    val chunkPoses = chunkPairs.flatMap { it.toList() }
    val chunkPosesJOML = chunkPoses.map { it.toJOML() }

    // Send a list of all the chunks that we plan on updating to players, so that they
    // defer all updates until assembly is finished
    level.players().forEach { player ->
        PacketStopChunkUpdates(chunkPosesJOML).sendToClient(player.playerWrapper)
    }

    // Use relocateBlock to copy all the blocks into the ship
    blocks.forEachChunk { chunkX, chunkY, chunkZ, chunk ->
        val destChunk = level.getChunk(chunkX - deltaX, chunkZ - deltaZ)

        chunk.forEach { x, y, z ->
            val toPos = BlockPos((destChunk.pos.x shl 4) + x, (chunkY shl 4) + y, (destChunk.pos.z shl 4) + z)


            destChunk.setBlockState(toPos, block, false)
        }
    }

    // Calculate the position of the block that the player clicked after it has been assembled
    val centerInShip = Vector3d(
        ((shipChunkX shl 4) + (centerBlock.x and 15)).toDouble(),
        centerBlock.y.toDouble(),
        ((shipChunkZ shl 4) + (centerBlock.z and 15)).toDouble()
    )

    // The ship's position has shifted from the center block since we assembled the ship, compensate for that
    val centerBlockPosInWorld = ship.inertiaData.centerOfMassInShip.sub(centerInShip, Vector3d())
        .add(ship.transform.positionInWorld)

    // Put the ship into the compensated position, so that all the assembled blocks stay in the same place
    // TODO: AAAAAAAAA THIS IS HORRIBLE how can the API support this?
    (ship as ShipData).transform = (ship.transform as ShipTransformImpl).copy(positionInWorld = centerBlockPosInWorld)

    level.server.executeIf(
        // This condition will return true if all modified chunks have been both loaded AND
        // chunk update packets were sent to players
        { chunkPoses.all(level::isTickingChunk) }
    ) {
        // Once all the chunk updates are sent to players, we can tell them to restart chunk updates
        level.players().forEach { player ->
            PacketRestartChunkUpdates(chunkPosesJOML).sendToClient(player.playerWrapper)
        }
    }

    return ship
}