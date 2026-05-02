package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.MiniBusEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.util.RenderUtil;
import com.mrcrayfish.vehicle.util.Vector3fAxis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MiniBusRenderer extends AbstractLandVehicleRenderer<MiniBusEntity>
{
    public MiniBusRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable MiniBusEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.MINI_BUS_BODY.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.pushPose();

        // Positions the steering wheel in the correct position
        matrixStack.translate(-0.2825, 0.225, 1.0625);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-67.5F));
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
    public void applyPlayerModel(MiniBusEntity entity, Player player, PlayerModel<AbstractClientPlayer> model, float partialTicks)
    {
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
            EntityRayTracer.createTransformListForPart(SpecialModels.MINI_BUS_BODY, parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.GO_KART_STEERING_WHEEL, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(-0.2825F, 0.225F, 1.0625F),
                    EntityRayTracer.MatrixTransformation.createRotation(Vector3fAxis.POSITIVE_X, -67.5F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                    EntityRayTracer.MatrixTransformation.createScale(0.75F));
            EntityRayTracer.createFuelPartTransforms(ModEntities.MINI_BUS.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.MINI_BUS.get(), parts, transforms);
        };
    }

    @Override
    public ISpecialModel getTowBarModel()
    {
        return SpecialModels.BIG_TOW_BAR;
    }
}
