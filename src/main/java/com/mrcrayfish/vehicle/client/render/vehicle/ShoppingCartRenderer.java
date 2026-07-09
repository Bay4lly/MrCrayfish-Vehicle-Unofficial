/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.Nullable
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.world.entity.player.Player
 */
package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.ShoppingCartEntity;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

public class ShoppingCartRenderer
extends AbstractLandVehicleRenderer<ShoppingCartEntity> {
    public ShoppingCartRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    public void render(@Nullable ShoppingCartEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.SHOPPING_CART_BODY.getModel(), matrixStack, renderTypeBuffer, light);
    }

    @Override
    public void applyPlayerModel(ShoppingCartEntity entity, Player player, PlayerModel model, float partialTicks) {
        model.rightArm.xRot = (float)Math.toRadians(-70.0);
        model.rightArm.yRot = (float)Math.toRadians(5.0);
        model.leftArm.xRot = (float)Math.toRadians(-70.0);
        model.leftArm.yRot = (float)Math.toRadians(-5.0);
        model.rightLeg.xRot = (float)Math.toRadians(-90.0);
        model.rightLeg.yRot = (float)Math.toRadians(15.0);
        model.leftLeg.xRot = (float)Math.toRadians(-90.0);
        model.leftLeg.yRot = (float)Math.toRadians(-15.0);
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (tracer, transforms, parts) -> EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.SHOPPING_CART_BODY, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
    }
}

