package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractMotorcycleRenderer;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.MopedEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.item.IDyeable;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MopedRenderer extends AbstractMotorcycleRenderer<MopedEntity>
{
    private final ModelPart lid;
    private final ModelPart base;
    private final ModelPart lock;
    public final boolean isChristmas;

    protected final PropertyFunction<MopedEntity, Boolean> hasChestProperty = new PropertyFunction<>(MopedEntity::hasChest, false);
    protected final PropertyFunction<MopedEntity, Float> openProgressProperty = new PropertyFunction<>(MopedEntity::getOpenProgress, 0F);
    protected final PropertyFunction<MopedEntity, Float> prevOpenProgressProperty = new PropertyFunction<>(MopedEntity::getPrevOpenProgress, 0F);

    public MopedRenderer(Supplier<VehicleProperties> properties)
    {
        super(properties);
        Calendar calendar = Calendar.getInstance();
        this.isChristmas = calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DAY_OF_MONTH) >= 24 && calendar.get(Calendar.DAY_OF_MONTH) <= 26;
        ModelPart modelpart = ChestRenderer.createSingleBodyLayer().bakeRoot();
        this.base = modelpart.getChild("bottom");
        this.lid = modelpart.getChild("lid");
        this.lock = modelpart.getChild("lock");
    }

    @Override
    public void render(@Nullable MopedEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.MOPED_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.pushPose();

        matrixStack.translate(0.0, 0.0, 11.5 * 0.0625);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-22.5F));
        if(vehicle != null)
        {
            float wheelAngle = vehicle.prevWheelAngle + (vehicle.wheelAngle - vehicle.prevWheelAngle) * partialTicks;
            float wheelAngleNormal = wheelAngle / 45F;
            float turnRotation = wheelAngleNormal * 25F;
            matrixStack.mulPose(Axis.YP.rotationDegrees(turnRotation));
        }
        matrixStack.mulPose(Axis.XP.rotationDegrees(22.5F));
        matrixStack.translate(0.0, 0.0, -11.5 * 0.0625);

        //Render handles bars
        matrixStack.pushPose();
        matrixStack.translate(0, (12.2739 - 8) * 0.0625, (16.4071 - 8) * 0.0625);
        this.renderDamagedPart(vehicle, SpecialModels.MOPED_HANDLES.getModel(), matrixStack, renderTypeBuffer, light);
        matrixStack.popPose();

        //Render front bar and mud guard
        matrixStack.pushPose();
        {
            matrixStack.translate(0, (4.1044 - 8) * 0.0625, (19.8181 - 8) * 0.0625);
            this.renderDamagedPart(vehicle, SpecialModels.MOPED_MUD_GUARD.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();

        //Render front wheel
        ItemStack wheelStack = this.wheelStackProperty.get(vehicle);
        if(!wheelStack.isEmpty())
        {
            matrixStack.pushPose();
            VehicleProperties properties = this.vehiclePropertiesProperty.get(vehicle);
            Wheel wheel = properties.getFirstFrontWheel();
            if(wheel != null)
            {
                matrixStack.translate(0.0, -8 * 0.0625, 0.0);
                matrixStack.translate(0.0, -properties.getAxleOffset() * 0.0625F, 0.0);
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
                BakedModel wheelModel = RenderUtil.getModel(wheelStack);
                int wheelColor = IDyeable.getColorFromStack(wheelStack);
                RenderUtil.renderColoredModel(wheelModel, ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, wheelColor, light, OverlayTexture.NO_OVERLAY);
            }
            matrixStack.popPose();
        }

        matrixStack.popPose();

        if(this.hasChestProperty.get(vehicle))
        {
            matrixStack.pushPose();
            matrixStack.mulPose(Axis.YP.rotationDegrees(180F));
            matrixStack.translate(0, 0, 6.5 * 0.0625F);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.translate(-0.5, 0, 0);
            float progress = Mth.lerp(partialTicks, this.prevOpenProgressProperty.get(vehicle), this.openProgressProperty.get(vehicle));
            progress = 1.0F - progress;
            progress = 1.0F - progress * progress * progress;
            Material renderMaterial = this.isChristmas ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;
            VertexConsumer builder = renderMaterial.buffer(renderTypeBuffer, RenderType::entityCutout);
            this.renderChest(matrixStack, builder, this.lid, this.lock, this.base, progress, light, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
        }
    }

    @Override
    public void applyPlayerModel(MopedEntity entity, Player player, PlayerModel model, float partialTicks)
    {
        float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
        float wheelAngleNormal = wheelAngle / 45F;
        float turnRotation = wheelAngleNormal * 6F;
        model.rightArm.xRot = (float) Math.toRadians(-65F - turnRotation);
        model.rightArm.yRot = (float) Math.toRadians(5F);
        model.rightArm.z -= 1;
        model.rightArm.z -= wheelAngleNormal * 2;
        model.leftArm.xRot = (float) Math.toRadians(-65F + turnRotation);
        model.leftArm.yRot = (float) Math.toRadians(-5F);
        model.leftArm.z -= 1;
        model.leftArm.z += wheelAngleNormal * 2;
        model.rightLeg.xRot = (float) Math.toRadians(-62F);
        model.leftLeg.xRot = (float) Math.toRadians(-62F);
    }

    @Override
    public void applyPlayerRender(MopedEntity entity, Player player, float partialTicks, PoseStack matrixStack)
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
            float turnAngleNormal = (entity.prevTurnAngle + (entity.turnAngle - entity.prevTurnAngle) * partialTicks) / 45F;
            matrixStack.mulPose(Axis.ZP.rotationDegrees(turnAngleNormal * currentSpeedNormal * 20F));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
        }
    }

    private void renderChest(PoseStack matrixStack, VertexConsumer builder, ModelPart lid, ModelPart lock, ModelPart base, float openProgress, int lightTexture, int overlayTexture)
    {
        lid.xRot = -(openProgress * ((float) Math.PI / 2F));
        lock.xRot = lid.xRot;
        lid.render(matrixStack, builder, lightTexture, overlayTexture);
        lock.render(matrixStack, builder, lightTexture, overlayTexture);
        base.render(matrixStack, builder, lightTexture, overlayTexture);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.MOPED_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.MOPED_HANDLES, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.0625F, 0.0F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, 0.835F, 0.525F),
                    EntityRayTracer.MatrixTransformation.createScale(0.8F));
            EntityRayTracer.createTransformListForPart(SpecialModels.MOPED_MUD_GUARD, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.0625F, 0.0F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.12F, 0.785F),
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -22.5F),
                    EntityRayTracer.MatrixTransformation.createScale(0.9F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.MOPED.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
        };
    }
}
