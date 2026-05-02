package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractPlaneRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.BathEntity;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class BathModel extends AbstractPlaneRenderer<BathEntity>
{
    public BathModel(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable BathEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        matrixStack.mulPose(Axis.YP.rotationDegrees(90F));
        this.renderDamagedPart(vehicle, SpecialModels.ATV_BODY.getModel(), matrixStack, renderTypeBuffer, light);
    }

    @Override
    public void applyPlayerRender(BathEntity entity, Player player, float partialTicks, PoseStack matrixStack)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = seatVec.x * scale;
            double offsetY = (seatVec.y + player.getMyRidingOffset() - 0.5) * scale + 24 * 0.0625; //Player is 2 blocks high tall but renders at 1.8 blocks tall
            double offsetZ = seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float bodyPitch = entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks;
            float bodyRoll = entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks;
            matrixStack.mulPose(Axis.ZP.rotationDegrees(bodyRoll));
            matrixStack.mulPose(Axis.XP.rotationDegrees(-bodyPitch));
            matrixStack.translate(-offsetX, -offsetY, -offsetX);
        }
    }

    @Override
    public void applyPlayerModel(BathEntity entity, Player player, PlayerModel model, float partialTicks)
    {
        model.rightLeg.xRot = (float) Math.toRadians(-85F);
        model.rightLeg.yRot = (float) Math.toRadians(10F);
        model.leftLeg.xRot = (float) Math.toRadians(-85F);
        model.leftLeg.yRot = (float) Math.toRadians(-10F);
        model.rightArm.xRot = (float) Math.toRadians(-80F);
        model.rightArm.yRot = (float) Math.toRadians(5F);
        model.rightArm.zRot = (float) Math.toRadians(0F);
        model.leftArm.xRot = (float) Math.toRadians(-80F);
        model.leftArm.yRot = (float) Math.toRadians(-5F);
        model.leftArm.zRot = (float) Math.toRadians(0F);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cfm:bath")), parts, transforms,
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_Y, 90F));
        };
    }
}
