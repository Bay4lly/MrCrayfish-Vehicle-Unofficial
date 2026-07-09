/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 */
package com.mrcrayfish.vehicle.client.handler;

import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class SprayCanHandler {
    private int lastSlot = -1;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        int slot = player.getInventory().selected;
        if (this.lastSlot == slot) {
            return;
        }
        this.lastSlot = slot;
        if (player.getInventory().getSelected().isEmpty()) {
            return;
        }
        if (!(player.getInventory().getSelected().getItem() instanceof SprayCanItem)) {
            return;
        }
        SprayCanItem sprayCan = (SprayCanItem)player.getInventory().getSelected().getItem();
        float pitch = 0.85f + 0.15f * sprayCan.getRemainingSprays(player.getInventory().getSelected());
        Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)((SoundEvent)ModSounds.ITEM_SPRAY_CAN_SHAKE.get()), (float)pitch, (float)0.75f));
    }

    static void applySprayCanPose(Player player, PlayerModel<?> model) {
        ItemStack leftItem;
        if (player.getVehicle() != null) {
            return;
        }
        boolean rightInteractionHanded = player.getMainArm() == HumanoidArm.RIGHT;
        ItemStack rightItem = rightInteractionHanded ? player.getMainHandItem() : player.getOffhandItem();
        ItemStack itemStack = leftItem = rightInteractionHanded ? player.getOffhandItem() : player.getMainHandItem();
        if (!rightItem.isEmpty() && rightItem.getItem() instanceof SprayCanItem) {
            SprayCanHandler.copyModelAngles(model.head, model.rightArm);
            model.rightArm.xRot = (float)((double)model.rightArm.xRot + Math.toRadians(-80.0));
        }
        if (!leftItem.isEmpty() && leftItem.getItem() instanceof SprayCanItem) {
            model.leftArm.copyFrom(model.head);
            model.leftArm.xRot = (float)((double)model.leftArm.xRot + Math.toRadians(-80.0));
        }
    }

    private static void copyModelAngles(ModelPart source, ModelPart target) {
        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
    }
}

