package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractMotorcycleRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.DirtBikeEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class DirtBikeRenderer extends AbstractMotorcycleRenderer<DirtBikeEntity>
{
    public DirtBikeRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable DirtBikeEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.DIRT_BIKE_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        //Render the handles bars
        matrixStack.pushPose();

        matrixStack.translate(0.0, 0.0, 10.5 * 0.0625);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-22.5F));

        if(vehicle != null)
        {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }

        matrixStack.mulPose(Axis.XP.rotationDegrees(22.5F));
        matrixStack.translate(0.0, 0.0, -10.5 * 0.0625);

        this.renderDamagedPart(vehicle, SpecialModels.DIRT_BIKE_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);

        ItemStack wheelStack = this.wheelStackProperty.get(vehicle);
        if(!wheelStack.isEmpty())
        {
            VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
            Wheel wheel = properties.getWheels().stream().filter(wheel1 -> wheel1.getPosition() == Wheel.Position.FRONT).findFirst().orElse(null);
            if(wheel != null)
            {
                matrixStack.pushPose();
                matrixStack.translate(0, -0.5, 0);
                matrixStack.translate(wheel.getOffsetX() * 0.0625, wheel.getOffsetY() * 0.0625, wheel.getOffsetZ() * 0.0625);
                if(vehicle != null)
                {
                    float frontWheelSpin = Mth.lerp(partialTicks, vehicle.prevFrontWheelRotation, vehicle.frontWheelRotation);
                    if(vehicle.isMoving())
                    {
                        matrixStack.mulPose(Axis.XP.rotationDegrees(-frontWheelSpin));
                    }
                }
                matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
                matrixStack.mulPose(Axis.YP.rotationDegrees(180F));
                RenderUtil.renderColoredModel(RenderUtil.getModel(wheelStack), ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
                matrixStack.popPose();
            }
        }

        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(DirtBikeEntity entity, Player player, PlayerModel<AbstractClientPlayer> model, float partialTicks)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index == 0)
        {
            float wheelAngle = entity.prevRenderWheelAngle + (entity.renderWheelAngle - entity.prevRenderWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 8F;
            model.rightArm.xRot = (float) Math.toRadians(-55F - turnRotation);
            model.leftArm.xRot = (float) Math.toRadians(-55F + turnRotation);
        }
        else if(index == 1)
        {
            model.rightArm.xRot = (float) Math.toRadians(-45F);
            model.rightArm.zRot = (float) Math.toRadians(-10F);
            model.leftArm.xRot = (float) Math.toRadians(-45F);
            model.leftArm.zRot = (float) Math.toRadians(10F);
        }

        model.rightLeg.xRot = (float) Math.toRadians(-45F);
        model.rightLeg.yRot = (float) Math.toRadians(30F);
        model.leftLeg.xRot = (float) Math.toRadians(-45F);
        model.leftLeg.yRot = (float) Math.toRadians(-30F);
    }

    @Override
    public void applyPlayerRender(DirtBikeEntity entity, Player player, float partialTicks, PoseStack matrixStack)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y + player.getMyRidingOffset()) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = -seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            matrixStack.mulPose(Axis.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * 20F));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (entityRayTracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.DIRT_BIKE_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.DIRT_BIKE_HANDLES, parts, transforms);
            EntityRayTracer.createFuelPartTransforms(ModEntities.DIRT_BIKE.get(), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
