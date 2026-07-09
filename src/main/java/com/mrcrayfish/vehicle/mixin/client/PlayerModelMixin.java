/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.mrcrayfish.vehicle.mixin.client;

import com.mrcrayfish.vehicle.client.handler.HeldVehicleHandler;
import com.mrcrayfish.vehicle.client.handler.PlayerModelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerModel.class})
class PlayerModelMixin<T extends LivingEntity> {
    @Shadow
    @Final
    private boolean slim;

    PlayerModelMixin() {
    }

    @Inject(method={"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at={@At(value="HEAD")})
    void setRotationAnglesHead(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback) {
        if (!(entityIn instanceof Player)) {
            return;
        }
        this.resetRotationAngles();
        this.vehicleResetVisibilities();
    }

    @Unique
    private void resetRotationAngles() {
        PlayerModel self = (PlayerModel)(Object)this;
        this.vehicleResetAll(self.head);
        this.vehicleResetAll(self.hat);
        this.vehicleResetAll(self.body);
        this.vehicleResetAll(self.jacket);
        this.vehicleResetAll(self.rightArm);
        self.rightArm.x = -5.0f;
        self.rightArm.y = this.slim ? 2.5f : 2.0f;
        self.rightArm.z = 0.0f;
        this.vehicleResetAll(self.rightSleeve);
        self.rightSleeve.x = -5.0f;
        self.rightSleeve.y = this.slim ? 2.5f : 2.0f;
        self.rightSleeve.z = 10.0f;
        this.vehicleResetAll(self.leftArm);
        self.leftArm.x = 5.0f;
        self.leftArm.y = this.slim ? 2.5f : 2.0f;
        self.leftArm.z = 0.0f;
        this.vehicleResetAll(self.leftSleeve);
        self.leftSleeve.x = 5.0f;
        self.leftSleeve.y = this.slim ? 2.5f : 2.0f;
        self.leftSleeve.z = 0.0f;
        this.vehicleResetAll(self.leftLeg);
        self.leftLeg.x = 1.9f;
        self.leftLeg.y = 12.0f;
        self.leftLeg.z = 0.0f;
        this.vehicleResetAll(self.leftPants);
        self.leftPants.copyFrom(self.leftLeg);
        this.vehicleResetAll(self.rightLeg);
        self.rightLeg.x = -1.9f;
        self.rightLeg.y = 12.0f;
        self.rightLeg.z = 0.0f;
        this.vehicleResetAll(self.rightPants);
        self.rightPants.copyFrom(self.rightLeg);
    }

    @Unique
    private void vehicleResetAll(ModelPart part) {
        part.xRot = 0.0f;
        part.yRot = 0.0f;
        part.zRot = 0.0f;
        part.x = 0.0f;
        part.y = 0.0f;
        part.z = 0.0f;
    }

    @Unique
    private void vehicleResetVisibilities() {
        PlayerModel self = (PlayerModel)(Object)this;
        self.head.visible = true;
        self.body.visible = true;
        self.rightArm.visible = true;
        self.leftArm.visible = true;
        self.rightLeg.visible = true;
        self.leftLeg.visible = true;
    }

    @Inject(method={"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at={@At(value="TAIL")})
    void setRotationAnglesTail(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback) {
        if (!(entityIn instanceof Player)) {
            return;
        }
        PlayerModel self = (PlayerModel)(Object)this;
        PlayerModelHandler.onSetupAngles((Player)entityIn, (PlayerModel<Player>)self, Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());
        HeldVehicleHandler.onSetupAngles((Player)entityIn, (PlayerModel<Player>)self, Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());
        this.vehicleSetupRotationAngles();
    }

    @Unique
    private void vehicleSetupRotationAngles() {
        PlayerModel self = (PlayerModel)(Object)this;
        self.leftPants.copyFrom(self.leftLeg);
        self.rightPants.copyFrom(self.rightLeg);
        self.leftSleeve.copyFrom(self.leftArm);
        self.rightSleeve.copyFrom(self.rightArm);
        self.jacket.copyFrom(self.body);
        self.hat.copyFrom(self.head);
    }
}

