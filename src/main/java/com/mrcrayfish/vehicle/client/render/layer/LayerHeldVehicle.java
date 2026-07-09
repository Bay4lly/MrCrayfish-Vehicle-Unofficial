/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.handler.HeldVehicleHandler;
import com.mrcrayfish.vehicle.client.render.CachedVehicle;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import java.util.Optional;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class LayerHeldVehicle
extends RenderLayer<Player, EntityModel<Player>> {
    private VehicleEntity vehicle;
    private CachedVehicle cachedVehicle;
    private float width = -1.0f;

    public LayerHeldVehicle(RenderLayerParent<Player, EntityModel<Player>> renderer) {
        super(renderer);
    }

    public void render(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, Player player, float v, float v1, float partialTicks, float v3, float v4, float v5) {
        CompoundTag tagCompound = HeldVehicleDataHandler.getHeldVehicle(player);
        if (!tagCompound.isEmpty()) {
            EntityType entityType;
            Entity entity;
            Optional optional;
            if (this.cachedVehicle == null && (optional = EntityType.byString((String)tagCompound.getString("id"))).isPresent() && (entity = (entityType = (EntityType)optional.get()).create(player.level())) instanceof VehicleEntity) {
                entity.load(tagCompound);
                this.vehicle = (VehicleEntity)entity;
                this.width = entity.getBbWidth();
                this.cachedVehicle = new CachedVehicle(entityType);
            }
            if (this.cachedVehicle != null) {
                matrixStack.pushPose();
                HeldVehicleHandler.AnimationCounter counter = HeldVehicleHandler.idToCounter.get(player.getUUID());
                if (counter != null) {
                    float width = this.width / 2.0f;
                    matrixStack.translate(0.0, (double)(1.0f - counter.getProgress(partialTicks)), -0.5 * Math.sin(Math.PI * (double)counter.getProgress(partialTicks)) - (double)(width * (1.0f - counter.getProgress(partialTicks))));
                }
                Vec3 heldOffset = this.cachedVehicle.getProperties().getHeldOffset();
                matrixStack.translate(heldOffset.x * 0.0625, heldOffset.y * 0.0625, heldOffset.z * 0.0625);
                matrixStack.mulPose(Axis.XP.rotationDegrees(180.0f));
                matrixStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
                matrixStack.translate(0.0f, player.isCrouching() ? 0.3125f : 0.5625f, 0.0f);
                ((com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer<com.mrcrayfish.vehicle.entity.VehicleEntity>)this.cachedVehicle.getRenderer()).setupTransformsAndRender(this.vehicle, matrixStack, renderTypeBuffer, partialTicks, light);
                matrixStack.popPose();
            }
        } else {
            this.cachedVehicle = null;
            this.width = -1.0f;
        }
    }
}

