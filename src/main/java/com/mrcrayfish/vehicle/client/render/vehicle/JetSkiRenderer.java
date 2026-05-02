package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractBoatRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.JetSkiEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class JetSkiRenderer extends AbstractBoatRenderer<JetSkiEntity>
{
    public JetSkiRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable JetSkiEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        //Render the body
        this.renderDamagedPart(vehicle, SpecialModels.JET_SKI_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();

        matrixStack.translate(0, 0.355, 0.225);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-45F));

        if(vehicle != null)
        {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 15F;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }

        this.renderDamagedPart(vehicle, SpecialModels.ATV_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(JetSkiEntity entity, Player player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / (float) entity.getMaxTurnAngle();
        float turnRotation = wheelAngleNormal * 12F;
        model.rightArm.xRot = (float) Math.toRadians(-65F - turnRotation);
        model.rightArm.yRot = (float) Math.toRadians(15F);
        //model.bipedRightArm.offsetZ = -0.1F * wheelAngleNormal; //TODO test this out
        model.leftArm.xRot = (float) Math.toRadians(-65F + turnRotation);
        model.leftArm.yRot = (float) Math.toRadians(-15F);
        //model.bipedLeftArm.offsetZ = 0.1F * wheelAngleNormal;

        if(entity.getControllingPassenger() != player)
        {
            model.rightArm.xRot = (float) Math.toRadians(-55F);
            model.rightArm.yRot = (float) Math.toRadians(0F);
            model.leftArm.xRot = (float) Math.toRadians(-55F);
            model.leftArm.yRot = (float) Math.toRadians(0F);
        }

        model.rightLeg.xRot = (float) Math.toRadians(-65F);
        model.rightLeg.yRot = (float) Math.toRadians(30F);
        model.leftLeg.xRot = (float) Math.toRadians(-65F);
        model.leftLeg.yRot = (float) Math.toRadians(-30F);
    }

    @Override
    public void applyPlayerRender(JetSkiEntity entity, Player player, float partialTicks, PoseStack matrixStack)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getMyRidingOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;

            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / entity.getMaxTurnAngle();
            matrixStack.mulPose(Axis.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * 15F));
            matrixStack.mulPose(Axis.XP.rotationDegrees(-8F * Math.min(1.0F, currentSpeedNormal)));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    @Override
    protected boolean shouldRenderFuelLid()
    {
        return false;
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.JET_SKI_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.ATV_HANDLES, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.375F, 0.25F),
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -45F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.02F, 0.0F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.JET_SKI.get(), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
