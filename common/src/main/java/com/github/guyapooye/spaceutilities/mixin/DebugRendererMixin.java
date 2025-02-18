package com.github.guyapooye.spaceutilities.mixin;

import com.github.guyapooye.spaceutilities.ship.ShipNetworkClientHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

import java.text.NumberFormat;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void postRender(PoseStack matrices, MultiBufferSource.BufferSource vertexConsumersIgnore,
                            double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {

        MultiBufferSource.BufferSource bufferSource =
                MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            ShipNetworkClientHandler.INSTANCE.getNetworks().forEach((id, network) -> {
                Vector3d centerAveragePos = new Vector3d(0);
                double renderRadius = .25;
                network.getNetwork().forEach(ship -> {
                    ShipTransform shipRenderTransform = ship.getRenderTransform();
                    Vector3dc shipRenderPosition = shipRenderTransform.getPositionInWorld();
                    centerAveragePos.add(shipRenderPosition.x(), shipRenderPosition.y(), shipRenderPosition.z());
                });

                centerAveragePos.div(network.getNetwork().size());

                System.out.println(centerAveragePos.toString(NumberFormat.getInstance()));

                AABB shipCenterAverageBox =
                        new AABB(centerAveragePos.x - renderRadius, centerAveragePos.y - renderRadius,
                                centerAveragePos.z - renderRadius, centerAveragePos.x + renderRadius,
                                centerAveragePos.y + renderRadius, centerAveragePos.z + renderRadius)
                                .move(-cameraX, -cameraY, -cameraZ);
                LevelRenderer
                        .renderLineBox(matrices, bufferSource.getBuffer(RenderType.lines()), shipCenterAverageBox,
                                19.0F / 255.0F, 250.0F / 255.0F, 19.0F / 255.0F, 1.0F);
            });
        }
        bufferSource.endBatch();
    }
}
