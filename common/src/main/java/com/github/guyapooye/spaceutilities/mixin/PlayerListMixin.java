package com.github.guyapooye.spaceutilities.mixin;

import com.github.guyapooye.spaceutilities.networking.packets.ShipNetworkModificationPacket;
import com.github.guyapooye.spaceutilities.ship.ShipNetwork;
import com.github.guyapooye.spaceutilities.util.PlatformUtils;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void sendShipNetworks(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        ShipNetwork.Companion.getAllNetworks().forEach(network -> {
            PlatformUtils.getPacketChannel().sendTo(new ShipNetworkModificationPacket(network.getNetwork().nodes().stream().toList(), network.getId()), player);
        });


    }
}
