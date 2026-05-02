package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractLandVehicleRenderer;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.ShoppingCartEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ShoppingCartRenderer extends AbstractLandVehicleRenderer<ShoppingCartEntity>
{
    public ShoppingCartRenderer(Supplier<VehicleProperties> defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    public void render(@Nullable ShoppingCartEntity vehicle, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.SHOPPING_CART_BODY.getModel(), matrixStack, renderTypeBuffer, light);
    }

    @Override
    public void applyPlayerModel(ShoppingCartEntity entity, Player player, PlayerModel model, float partialTicks)
    {
        model.rightArm.xRot = (float) Math.toRadians(-70F);
        model.rightArm.yRot = (float) Math.toRadians(5F);
        model.leftArm.xRot = (float) Math.toRadians(-70F);
        model.leftArm.yRot = (float) Math.toRadians(-5F);
        model.rightLeg.xRot = (float) Math.toRadians(-90F);
        model.rightLeg.yRot = (float) Math.toRadians(15F);
        model.leftLeg.xRot = (float) Math.toRadians(-90F);
        model.leftLeg.yRot = (float) Math.toRadians(-15F);
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.SHOPPING_CART_BODY, parts, transforms);
        };
    }
}
