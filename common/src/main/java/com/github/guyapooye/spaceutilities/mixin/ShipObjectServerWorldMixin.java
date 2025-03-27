package com.github.guyapooye.spaceutilities.mixin;

import com.github.guyapooye.spaceutilities.event.SUEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;

/**@deprecated sus vscore reference*/
@Deprecated
@Mixin(value = ShipObjectServerWorld.class, remap = false)
public class ShipObjectServerWorldMixin {
    @Inject(method = "deleteShip", at = @At("HEAD"))
    private void hookDeleteShip(ServerShip ship, CallbackInfo ci) {
        SUEvents.INSTANCE.getShipDeleteHook().emit(new SUEvents.DeleteShipEvent(ship));
    }
}
