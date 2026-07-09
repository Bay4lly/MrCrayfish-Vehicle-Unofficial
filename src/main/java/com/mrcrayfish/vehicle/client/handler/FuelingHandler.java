/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 *  net.neoforged.neoforge.client.event.RenderHandEvent
 */
package com.mrcrayfish.vehicle.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.RayTraceFunction;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.init.ModDataKeys;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.util.RenderUtil;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

public class FuelingHandler {
    private int fuelTickCounter;
    private boolean fueling;
    private boolean renderNozzle;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        EntityRayTracer.RayTraceResultRotated result;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (this.fueling) {
            ++this.fuelTickCounter;
        }
        if ((result = EntityRayTracer.instance().getContinuousInteraction()) != null && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING)) {
            if (this.fuelTickCounter % 20 == 0) {
                Vec3 vec = result.getLocation();
                player.level().playSound((Player)player, vec.x(), vec.y(), vec.z(), (SoundEvent)ModSounds.ITEM_JERRY_CAN_LIQUID_GLUG.get(), SoundSource.PLAYERS, 0.6f, 1.0f + 0.1f * player.level().random.nextFloat());
            }
            if (!this.fueling) {
                this.fuelTickCounter = 0;
                this.fueling = true;
            }
        } else {
            this.fueling = false;
        }
    }

    static void applyFuelingPose(Player player, PlayerModel<?> model) {
        boolean rightInteractionHanded;
        boolean bl = rightInteractionHanded = player.getMainArm() == HumanoidArm.RIGHT;
        if (rightInteractionHanded) {
            model.rightArm.xRot = (float)Math.toRadians(-20.0);
            model.rightArm.yRot = (float)Math.toRadians(0.0);
            model.rightArm.zRot = (float)Math.toRadians(0.0);
        } else {
            model.leftArm.xRot = (float)Math.toRadians(-20.0);
            model.leftArm.yRot = (float)Math.toRadians(0.0);
            model.leftArm.zRot = (float)Math.toRadians(0.0);
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        PoseStack matrixStack = event.getPoseStack();
        EntityRayTracer.RayTraceResultRotated result = EntityRayTracer.instance().getContinuousInteraction();
        if (result != null && result.equalsContinuousInteraction(RayTraceFunction.FUNCTION_FUELING) && event.getHand() == EntityRayTracer.instance().getContinuousInteractionInteractionHand()) {
            double offset = Math.sin((double)((float)this.fuelTickCounter + minecraft.getTimer().getGameTimeDeltaPartialTick(true)) / 3.0) * 0.1;
            matrixStack.translate(0.0, 0.35 + offset, -0.2);
            matrixStack.mulPose(Axis.XP.rotationDegrees(-25.0f));
        }
        if (((Optional)ModDataKeys.GAS_PUMP.getValue(player)).isPresent()) {
            if (event.getSwingProgress() > 0.0f) {
                this.renderNozzle = true;
            }
            if (event.getHand() == InteractionHand.MAIN_HAND && this.renderNozzle) {
                if (event.getSwingProgress() > 0.0f && (double)event.getSwingProgress() <= 0.25) {
                    return;
                }
                event.setCanceled(true);
                boolean mainInteractionHand = event.getHand() == InteractionHand.MAIN_HAND;
                HumanoidArm handSide = mainInteractionHand ? player.getMainArm() : player.getMainArm().getOpposite();
                int handOffset = handSide == HumanoidArm.RIGHT ? 1 : -1;
                MultiBufferSource.BufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
                int light = minecraft.getEntityRenderDispatcher().getPackedLightCoords(player, event.getPartialTick());
                matrixStack.pushPose();
                matrixStack.translate((double)handOffset * 0.65, -0.27, -0.72);
                matrixStack.mulPose(Axis.XP.rotationDegrees(45.0f));
                RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemDisplayContext.NONE, false, matrixStack, (MultiBufferSource)renderTypeBuffer, -1, light, OverlayTexture.NO_OVERLAY);
                matrixStack.popPose();
            }
        } else {
            this.renderNozzle = false;
        }
    }

    public static <T extends Entity> void onModelRenderPost(T entity, EntityModel<T> model, PoseStack matrixStack) {
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (!((Optional)ModDataKeys.GAS_PUMP.getValue(player)).isPresent()) {
            return;
        }
        matrixStack.pushPose();
        if (model.young) {
            matrixStack.translate(0.0, 0.75, 0.0);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        }
        if (player.isCrouching()) {
            matrixStack.translate(0.0, 0.2, 0.0);
        }
        ((PlayerModel)model).translateToHand(HumanoidArm.RIGHT, matrixStack);
        matrixStack.mulPose(Axis.XP.rotationDegrees(180.0f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        boolean leftInteractionHanded = player.getMainArm() == HumanoidArm.LEFT;
        matrixStack.translate((double)(leftInteractionHanded ? -1 : 1) / 16.0, 0.125, -0.625);
        matrixStack.translate(0.0, -0.5625, 0.359375);
        MultiBufferSource.BufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderUtil.renderColoredModel(SpecialModels.NOZZLE.getModel(), ItemDisplayContext.NONE, false, matrixStack, (MultiBufferSource)renderTypeBuffer, -1, 0xF000F0, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}

