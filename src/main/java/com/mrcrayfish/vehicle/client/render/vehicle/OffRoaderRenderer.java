package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.OffRoaderEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class OffRoaderRenderer extends AbstractLandVehicleRenderer<OffRoaderEntity>
{
    public OffRoaderRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable OffRoaderEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.OFF_ROADER_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();
            // Positions the steering wheel in the correct position
        matrixStack.translate(-0.3125, 0.35, 0.2);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-45F));
        matrixStack.translate(0, -0.02, 0);
        matrixStack.scale(0.75F, 0.75F, 0.75F);

        if(vehicle != null)
        {
            // Rotates the steering wheel based on the wheel angle
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }

        RenderUtil.renderColoredModel(SpecialModels.GO_KART_STEERING_WHEEL.getModel(), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(OffRoaderEntity entity, Player player, PlayerModel model, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index < 2) //Sitting in the front
        {
            model.rightLeg.xRot = (float) Math.toRadians(-80F);
            model.rightLeg.yRot = (float) Math.toRadians(15F);
            model.leftLeg.xRot = (float) Math.toRadians(-80F);
            model.leftLeg.yRot = (float) Math.toRadians(-15F);

            if(index == 1)
            {
                model.leftArm.xRot = (float) Math.toRadians(-75F);
                model.leftArm.yRot = (float) Math.toRadians(-25F);
                model.leftArm.zRot = 0F;
            }
        }
        else
        {
            if(index == 3)
            {
                model.rightLeg.xRot = (float) Math.toRadians(-90F);
                model.rightLeg.yRot = (float) Math.toRadians(15F);
                model.leftLeg.xRot = (float) Math.toRadians(-90F);
                model.leftLeg.yRot = (float) Math.toRadians(-15F);
                model.rightArm.xRot = (float) Math.toRadians(-75F);
                model.rightArm.yRot = (float) Math.toRadians(110F);
                model.rightArm.zRot = (float) Math.toRadians(0F);
                model.leftArm.xRot = (float) Math.toRadians(-105F);
                model.leftArm.yRot = (float) Math.toRadians(-20F);
                model.leftArm.zRot = 0F;
            }
            else
            {
                model.rightLeg.xRot = (float) Math.toRadians(0F);
                model.rightLeg.yRot = (float) Math.toRadians(0F);
                model.leftLeg.xRot = (float) Math.toRadians(0F);
                model.leftLeg.yRot = (float) Math.toRadians(0F);
                model.rightArm.xRot = (float) Math.toRadians(-10F);
                model.rightArm.zRot = (float) Math.toRadians(25F);
                model.leftArm.xRot = (float) Math.toRadians(-80F);
                model.leftArm.zRot = 0F;
                model.leftLeg.xRot = (float) Math.toRadians(-20F);
                model.rightLeg.xRot = (float) Math.toRadians(20F);
            }
        }

        if(entity.getControllingPassenger() == player)
        {
            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 6F;
            model.rightArm.xRot = (float) Math.toRadians(-65F - turnRotation);
            model.rightArm.yRot = (float) Math.toRadians(-7F);
            model.leftArm.xRot = (float) Math.toRadians(-65F + turnRotation);
            model.leftArm.yRot = (float) Math.toRadians(7F);
        }
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.OFF_ROADER_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(-0.3125F, 0.35F, 0.2F),
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -45F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                    EntityRayTracer.MatrixTransformation.createScale(0.75F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.OFF_ROADER.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.OFF_ROADER.get(), parts, transforms);
        };
    }
}
