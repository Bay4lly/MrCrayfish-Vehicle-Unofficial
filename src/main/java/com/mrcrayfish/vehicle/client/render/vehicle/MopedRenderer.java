/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.renderer.blockentity.ChestRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractMotorcycleRenderer;
import com.mrcrayfish.vehicle.client.render.AbstractVehicleRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.MopedEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class MopedRenderer
extends AbstractMotorcycleRenderer<MopedEntity> {
    private final ModelPart lid;
    private final ModelPart base;
    private final ModelPart lock;
    public final boolean isChristmas;
    protected final AbstractVehicleRenderer.PropertyFunction<MopedEntity, Boolean> hasChestProperty = new AbstractVehicleRenderer.PropertyFunction<MopedEntity, Boolean>(MopedEntity::hasChest, false);
    protected final AbstractVehicleRenderer.PropertyFunction<MopedEntity, Float> openProgressProperty = new AbstractVehicleRenderer.PropertyFunction<MopedEntity, Float>(MopedEntity::getOpenProgress, Float.valueOf(0.0f));
    protected final AbstractVehicleRenderer.PropertyFunction<MopedEntity, Float> prevOpenProgressProperty = new AbstractVehicleRenderer.PropertyFunction<MopedEntity, Float>(MopedEntity::getPrevOpenProgress, Float.valueOf(0.0f));

    public MopedRenderer(Supplier<VehicleProperties> properties) {
        super(properties);
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;
        ModelPart modelpart = ChestRenderer.createSingleBodyLayer().bakeRoot();
        this.base = modelpart.getChild("bottom");
        this.lid = modelpart.getChild("lid");
        this.lock = modelpart.getChild("lock");
    }

    @Override
    public void render(@Nullable MopedEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light) {
        this.renderDamagedPart(vehicle, SpecialModels.MOPED_BODY.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.0, 0.71875);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
        if (vehicle != null) {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45.0f;
            float turnRotation = wheelAngleNormal * 25.0f;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }
        matrixStack.mulPose(Axis.XP.rotationDegrees(22.5f));
        matrixStack.translate(0.0, 0.0, -0.71875);
        matrixStack.pushPose();
        matrixStack.translate(0.0, 0.26711874999999996, 0.52544375);
        this.renderDamagedPart(vehicle, SpecialModels.MOPED_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0, -0.243475, 0.7386312500000001);
        this.renderDamagedPart(vehicle, SpecialModels.MOPED_MUD_GUARD.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();
        ItemStack wheelStack = (ItemStack)this.wheelStackProperty.get(vehicle);
        if (!wheelStack.isEmpty()) {
            matrixStack.pushPose();
            VehicleProperties properties = (VehicleProperties)this.vehiclePropertiesProperty.get(vehicle);
            Wheel wheel = properties.getFirstFrontWheel();
            if (wheel != null) {
                matrixStack.translate(0.0, -0.5, 0.0);
                matrixStack.translate(0.0, (double)(-properties.getAxleOffset() * 0.0625f), 0.0);
                matrixStack.translate((double)wheel.getOffsetX() * 0.0625, (double)wheel.getOffsetY() * 0.0625, (double)wheel.getOffsetZ() * 0.0625);
                if (vehicle != null) {
                    float frontWheelSpin = Mth.lerp((float)partialTicks, (float)vehicle.prevFrontWheelRotation, (float)vehicle.frontWheelRotation);
                    if (vehicle.isMoving()) {
                        matrixStack.mulPose(Axis.XP.rotationDegrees(-frontWheelSpin));
                    }
                }
                matrixStack.scale(wheel.getScaleX(), wheel.getScaleY(), wheel.getScaleZ());
                BakedModel wheelModel = RenderUtil.getModel(wheelStack);
                int wheelColor = IDyeable.getColorFromStack(wheelStack);
                RenderUtil.renderColoredModel(wheelModel, ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, wheelColor, light, OverlayTexture.NO_OVERLAY);
            }
            matrixStack.popPose();
        }
        matrixStack.popPose();
        if (this.hasChestProperty.get(vehicle).booleanValue()) {
            matrixStack.pushPose();
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            matrixStack.translate(0.0, 0.0, 0.40625);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            matrixStack.translate(-0.5, 0.0, 0.0);
            float progress = Mth.lerp((float)partialTicks, (float)this.prevOpenProgressProperty.get(vehicle).floatValue(), (float)this.openProgressProperty.get(vehicle).floatValue());
            progress = 1.0f - progress;
            progress = 1.0f - progress * progress * progress;
            Material renderMaterial = this.isChristmas ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;
            VertexConsumer builder = renderMaterial.buffer(renderTypeBuffer, RenderType::entityCutout);
            this.renderChest(matrixStack, builder, this.lid, this.lock, this.base, progress, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
    }

    @Override
    public void applyPlayerModel(MopedEntity entity, Player player, PlayerModel model, float partialTicks) {
        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45.0f;
        float turnRotation = wheelAngleNormal * 6.0f;
        model.rightArm.xRot = (float)Math.toRadians(-65.0f - turnRotation);
        model.rightArm.yRot = (float)Math.toRadians(5.0);
        model.rightArm.z -= 1.0f;
        model.rightArm.z -= wheelAngleNormal * 2.0f;
        model.leftArm.xRot = (float)Math.toRadians(-65.0f + turnRotation);
        model.leftArm.yRot = (float)Math.toRadians(-5.0);
        model.leftArm.z -= 1.0f;
        model.leftArm.z += wheelAngleNormal * 2.0f;
        model.rightLeg.xRot = (float)Math.toRadians(-62.0);
        model.leftLeg.xRot = (float)Math.toRadians(-62.0);
    }

    @Override
    public void applyPlayerRender(MopedEntity entity, Player player, float partialTicks, PoseStack matrixStack) {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if (index != -1) {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vec3 seatVec = seat.getPosition().add(0.0, (double)(properties.getAxleOffset() + properties.getWheelOffset()), 0.0).scale(properties.getBodyPosition().getScale()).multiply(-1.0, 1.0, 1.0).scale(0.0625);
            double scale = 1.0666666666666667;
            double offsetX = -seatVec.x * scale;
            double offsetY = (seatVec.y - player.getVehicleAttachmentPoint(entity).y + 0.25) * scale + 1.5;
            double offsetZ = seatVec.z * scale;
            matrixStack.translate(offsetX, offsetY, offsetZ);
            float currentSpeedNormal = (entity.prevCurrentSpeed + (entity.currentSpeed - entity.prevCurrentSpeed) * partialTicks) / entity.getMaxSpeed();
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45.0f;
            matrixStack.mulPose(Axis.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * 20.0f));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    private void renderChest(PoseStack matrixStack, VertexConsumer builder, ModelPart lid, ModelPart lock, ModelPart base, float openProgress, int lightTexture, int overlayTexture) {
        lock.xRot = lid.xRot = -(openProgress * 1.5707964f);
        lid.render(matrixStack, builder, lightTexture, overlayTexture);
        lock.render(matrixStack, builder, lightTexture, overlayTexture);
        base.render(matrixStack, builder, lightTexture, overlayTexture);
    }

    @Override
    @Nullable
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms() {
        return (tracer, transforms, parts) -> {
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.MOPED_BODY, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, new EntityRayTracer.MatrixTransformation[0]);
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.MOPED_HANDLES, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -0.0625f, 0.0f), EntityRayTracer.MatrixTransformation.createTranslation(0.0f, 0.835f, 0.525f), EntityRayTracer.MatrixTransformation.createScale(0.8f));
            EntityRayTracer.createTransformListForPart((ISpecialModel)SpecialModels.MOPED_MUD_GUARD, (HashMap<EntityRayTracer.RayTracePart, List<EntityRayTracer.MatrixTransformation>>)parts, (List<EntityRayTracer.MatrixTransformation>)transforms, EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -0.0625f, 0.0f), EntityRayTracer.MatrixTransformation.createTranslation(0.0f, -0.12f, 0.785f), EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -22.5f), EntityRayTracer.MatrixTransformation.createScale(0.9f));
            EntityRayTracer.createFuelPartTransforms((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.MOPED.get()), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}


