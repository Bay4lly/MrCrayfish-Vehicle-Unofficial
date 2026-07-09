/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.resources.PlayerSkin$Model
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.RenderPlayerEvent$Pre
 */
package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.client.render.layer.LayerHeldVehicle;
import com.mrcrayfish.vehicle.common.entity.HeldVehicleDataHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

public class HeldVehicleHandler {
    private static boolean setupExtraLayers = false;
    public static final Map<UUID, AnimationCounter> idToCounter = new HashMap<UUID, AnimationCounter>();

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (!setupExtraLayers) {
            Map skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
            this.patchPlayerRender((EntityRenderer<? extends Player>)((EntityRenderer)skinMap.get(PlayerSkin.Model.WIDE)));
            this.patchPlayerRender((EntityRenderer<? extends Player>)((EntityRenderer)skinMap.get(PlayerSkin.Model.SLIM)));
            setupExtraLayers = true;
        }
    }

    private void patchPlayerRender(EntityRenderer<? extends Player> player) {
        if (player instanceof LivingEntityRenderer) {
            LivingEntityRenderer renderer = (LivingEntityRenderer)player;
            if (!renderer.layers.isEmpty()) {
                renderer.layers.add(new LayerHeldVehicle((RenderLayerParent<Player, EntityModel<Player>>)renderer));
            }
        }
    }

    public static void onSetupAngles(Player player, PlayerModel<Player> model, float partialTick) {
        AnimationCounter counter;
        boolean holdingVehicle = HeldVehicleDataHandler.isHoldingVehicle(player);
        if (holdingVehicle && !idToCounter.containsKey(player.getUUID())) {
            idToCounter.put(player.getUUID(), new AnimationCounter(40));
        } else if (idToCounter.containsKey(player.getUUID())) {
            if (idToCounter.get(player.getUUID()).getProgress(partialTick) == 0.0f) {
                idToCounter.remove(player.getUUID());
                return;
            }
            if (!holdingVehicle) {
                counter = idToCounter.get(player.getUUID());
                player.yBodyRot = player.getYHeadRot() - (player.getYHeadRot() - player.yBodyRotO) * counter.getProgress(partialTick);
            }
        } else {
            return;
        }
        counter = idToCounter.get(player.getUUID());
        counter.update(holdingVehicle);
        float progress = counter.getProgress(partialTick);
        model.rightArm.xRot = (float)Math.toRadians(-180.0f * progress);
        model.rightArm.zRot = (float)Math.toRadians(-5.0f * progress);
        model.rightArm.y = (player.isCrouching() ? 3.0f : -0.5f) * progress;
        model.leftArm.xRot = (float)Math.toRadians(-180.0f * progress);
        model.leftArm.zRot = (float)Math.toRadians(5.0f * progress);
        model.leftArm.y = (player.isCrouching() ? 3.0f : -0.5f) * progress;
    }

    public static class AnimationCounter {
        private final int MAX_COUNT;
        private int prevCount;
        private int currentCount;

        public AnimationCounter(int maxCount) {
            this.MAX_COUNT = maxCount;
        }

        public int update(boolean increment) {
            this.prevCount = this.currentCount;
            if (increment) {
                if (this.currentCount < this.MAX_COUNT) {
                    ++this.currentCount;
                }
            } else if (this.currentCount > 0) {
                this.currentCount = Math.max(0, this.currentCount - 2);
            }
            return this.currentCount;
        }

        public int getMaxCount() {
            return this.MAX_COUNT;
        }

        public int getCurrentCount() {
            return this.currentCount;
        }

        public float getProgress(float partialTicks) {
            return ((float)this.prevCount + (float)(this.currentCount - this.prevCount) * partialTicks) / (float)this.MAX_COUNT;
        }
    }
}

