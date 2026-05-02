package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.QuadBikeEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class QuadBikeRenderer extends AbstractLandVehicleRenderer<QuadBikeEntity>
{
    public QuadBikeRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable QuadBikeEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.QUAD_BIKE_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.pushPose();
        matrixStack.translate(0.0, 6.0 * 0.0625, 3.0 * 0.0625);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-35F));

        if(vehicle != null)
        {
            float wheelAngle = Mth.lerp(partialTicks, vehicle.prevWheelAngle, vehicle.wheelAngle);
            float maxSteeringAngle = vehicle.getProperties().getRearAxelVec() != null ? vehicle.getMaxTurnAngle() : 45F;
            float turnRotation = (wheelAngle / maxSteeringAngle) * 15F;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }

        RenderUtil.renderColoredModel(SpecialModels.QUAD_BIKE_HANDLES.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, this.colorProperty.get(vehicle), light, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(QuadBikeEntity entity, Player player, PlayerModel<AbstractClientPlayer> model, float partialTicks)
    {
        float wheelAngle = Mth.lerp(partialTicks, this.prevWheelAngleProperty.get(entity), this.wheelAngleProperty.get(entity));
        float maxSteeringAngle = entity.getProperties().getRearAxelVec() != null
                ? entity.getMaxTurnAngle()
                : 45F;
        float steeringWheelRotation = (wheelAngle / maxSteeringAngle) * 15F / 2F;
        model.rightArm.xRot = (float) Math.toRadians(-65F - steeringWheelRotation);
        model.rightArm.yRot = (float) Math.toRadians(15F);
        model.leftArm.xRot = (float) Math.toRadians(-65F + steeringWheelRotation);
        model.leftArm.yRot = (float) Math.toRadians(-15F);

        if(entity.getControllingPassenger() != player)
        {
            model.rightArm.xRot = (float) Math.toRadians(-20F);
            model.rightArm.yRot = (float) Math.toRadians(0F);
            model.rightArm.zRot = (float) Math.toRadians(15F);
            model.leftArm.xRot = (float) Math.toRadians(-20F);
            model.leftArm.yRot = (float) Math.toRadians(0F);
            model.leftArm.zRot = (float) Math.toRadians(-15F);
            model.rightLeg.xRot = (float) Math.toRadians(-85F);
            model.rightLeg.yRot = (float) Math.toRadians(30F);
            model.leftLeg.xRot = (float) Math.toRadians(-85F);
            model.leftLeg.yRot = (float) Math.toRadians(-30F);
            return;
        }

        model.rightLeg.xRot = (float) Math.toRadians(-45F);
        model.rightLeg.yRot = (float) Math.toRadians(40F);
        model.leftLeg.xRot = (float) Math.toRadians(-45F);
        model.leftLeg.yRot = (float) Math.toRadians(-40F);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.QUAD_BIKE_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.QUAD_BIKE_HANDLES, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 6.0F * 0.0625F, 3.0F * 0.0625F),
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -35F));
            EntityRayTracer.createTransformListForPart(SpecialModels.TOW_BAR, parts,
                EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, 180F),
                EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.5F, 1.05F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.QUAD_BIKE.get(), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.QUAD_BIKE.get(), parts, transforms);
        };
    }

    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }
}