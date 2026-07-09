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
import com.mrcrayfish.vehicle.entity.vehicle.CouchEntity;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

public class SofaCarRenderer
extends AbstractLandVehicleRenderer<CouchEntity> {
    public SofaCarRenderer(Supplier<VehicleProperties> defaultProperties) {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable CouchEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0625, 0.0);
        this.renderDamagedPart(vehicle, SpecialModels.RAINBOW_SOFA.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(CouchEntity entity, Player player, PlayerModel model, float partialTicks) {
        model.rightArm.xRot = (float)Math.toRadians(-55.0);
        model.rightArm.yRot = (float)Math.toRadians(25.0);
        model.leftArm.xRot = (float)Math.toRadians(-55.0);
        model.leftArm.yRot = (float)Math.toRadians(-25.0);
        model.rightLeg.xRot = (float)Math.toRadians(-90.0);
        model.rightLeg.yRot = (float)Math.toRadians(15.0);
        model.leftLeg.xRot = (float)Math.toRadians(-90.0);
        model.leftLeg.yRot = (float)Math.toRadians(-15.0);
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (tracer, transforms, parts) -> EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.RAINBOW_SOFA, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, 90.0f), EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.0625f, 0.0f));
    }
}

