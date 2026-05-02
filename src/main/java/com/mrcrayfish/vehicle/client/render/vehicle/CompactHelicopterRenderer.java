package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractHelicopterRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.CompactHelicopterEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CompactHelicopterRenderer extends AbstractHelicopterRenderer<CompactHelicopterEntity>
{
    public CompactHelicopterRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
        this.setRenderEngine(false);
    }

    @Override
    protected void render(@Nullable CompactHelicopterEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.HELICOPTER_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        // joystick is rendered once above

        // Render helicopter joystick (control stick)
        matrixStack.pushPose();
        // translate matches helicopter_base.complex child transform: x=-7.5, y=4.0, z=13.5 (model pixels)
        matrixStack.translate(-7.5 * 0.0625, 4.0 * 0.0625, 13.5 * 0.0625);
        // keep orientation consistent with model
        matrixStack.mulPose(Axis.YP.rotationDegrees(180F));
        this.renderDamagedPart(vehicle, SpecialModels.HELICOPTER_JOYSTICK.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0.0, 37.0 * 0.0625, -9.0 * 0.0625);
        if(vehicle != null)
        {
            float bladeRotation = vehicle.prevBladeRotation + (vehicle.bladeRotation - vehicle.prevBladeRotation) * partialTicks;
            matrixStack.mulPose(Axis.YP.rotationDegrees(bladeRotation));
        }
        this.renderDamagedPart(vehicle, SpecialModels.HELICOPTER_BLADES.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(3.0 * 0.0625, 24.5 * 0.0625, -81.5 * 0.0625);
        if(vehicle != null)
        {
            float bladeRotation = vehicle.prevBladeRotation + (vehicle.bladeRotation - vehicle.prevBladeRotation) * partialTicks;
            matrixStack.mulPose(Axis.XP.rotationDegrees(bladeRotation));
        }
        this.renderDamagedPart(vehicle, SpecialModels.HELICOPTER_TAIL_ROTOR.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(CompactHelicopterEntity entity, Player player, PlayerModel<AbstractClientPlayer> model, float partialTicks)
    {
        // Reset arms to a normal seated pose (default seating)
        model.rightArm.xRot = (float) Math.toRadians(-20F);
        model.rightArm.yRot = (float) Math.toRadians(0F);
        model.rightArm.zRot = (float) Math.toRadians(0F);
        model.leftArm.xRot = (float) Math.toRadians(-20F);
        model.leftArm.yRot = (float) Math.toRadians(0F);
        model.leftArm.zRot = (float) Math.toRadians(0F);

        // Reset arm pivot positions to typical defaults so they don't float
        model.rightArm.y = 2.0F;
        model.leftArm.y = 2.0F;
        model.rightArm.z = 0.0F;
        model.leftArm.z = 0.0F;
        model.rightLeg.xRot = (float) Math.toRadians(-90F);
        model.rightLeg.yRot = (float) Math.toRadians(15F);
        model.leftLeg.xRot = (float) Math.toRadians(-90F);
        model.leftLeg.yRot = (float) Math.toRadians(-15F);
    }

    @Override
    public void applyPlayerRender(CompactHelicopterEntity entity, Player player, float partialTicks, PoseStack matrixStack)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyPosition().getScale()).multiply(-1, 1, 1).scale(0.0625);
            double scale = 32.0 / 30.0;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y + player.getMyRidingOffset() + 0.3) * scale + 24 * 0.0625;
            double offsetZ = seatVec.z * scale;
            float entityYaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTicks;

            matrixStack.translate(offsetX, offsetY, offsetZ);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(-(entity.prevBodyRotationX + (entity.bodyRotationX - entity.prevBodyRotationX) * partialTicks)));
            matrixStack.mulPose(Axis.XP.rotationDegrees(entity.prevBodyRotationZ + (entity.bodyRotationZ - entity.prevBodyRotationZ) * partialTicks));
            matrixStack.mulPose(Axis.YP.rotationDegrees(entityYaw));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.HELICOPTER_BODY, parts, transforms);
            EntityRayTracer.createFuelPartTransforms(ModEntities.COMPACT_HELICOPTER.get(), SpecialModels.SMALL_FUEL_DOOR_CLOSED, parts, transforms);
            // expose joystick as an interactable part (translate matches complex model)
            transforms.add(EntityRayTracer.MatrixTransformation.createTranslation(-7.5F * 0.0625F, 4.0F * 0.0625F, 13.5F * 0.0625F));
            EntityRayTracer.createTransformListForPart(SpecialModels.HELICOPTER_JOYSTICK, parts, transforms);
        };
    }
}