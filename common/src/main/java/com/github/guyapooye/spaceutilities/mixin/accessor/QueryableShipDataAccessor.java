package com.github.guyapooye.spaceutilities.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.impl.datastructures.ChunkClaimMap;
import org.valkyrienskies.core.impl.game.ships.QueryableShipDataImpl;

@Mixin(value = QueryableShipDataImpl.class, remap = false)
public interface QueryableShipDataAccessor {
    @Accessor("chunkClaimToShipData")
    ChunkClaimMap<Ship> getChunkClaimToShipData();

}
