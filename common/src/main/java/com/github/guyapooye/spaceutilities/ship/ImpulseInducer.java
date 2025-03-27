package com.github.guyapooye.spaceutilities.ship;

import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ImpulseInducer extends ShipForcesInducer, BiConsumer<PhysShip, Function1<? super Long, ? extends PhysShip>> {

    @Override
    default void applyForces(@NotNull PhysShip physShip) {
    }

    @Override
    default void applyForcesAndLookupPhysShips(@NotNull PhysShip physShip, @NotNull Function1<? super Long, ? extends PhysShip> lookupPhysShip) {
        ShipForcesInducer.super.applyForcesAndLookupPhysShips(physShip, lookupPhysShip);
        accept(physShip, lookupPhysShip);
        ((ShipObjectServerWorld)VSGameUtilsKt.getShipObjectWorld(ValkyrienSkiesMod.getCurrentServer())).getShipObjects().get(physShip.getId()).getForceInducers().remove(this);
    }
}
