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

// Based on: https://github.com/MrCrayfish/Obfuscate/blob/3814e8ee29570820dbadab43fae65009dc4ef557/src/main/java/com/mrcrayfish/obfuscate/mixin/client/PlayerModelMixin.java#L62
// FIXME cleanup
@Mixin(PlayerModel.class)
class PlayerModelMixin<T extends LivingEntity>
{
    @Shadow
    @Final
    private boolean slim;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "HEAD"))
    void setRotationAnglesHead(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
    {
        if(!(entityIn instanceof Player))
            return;

        this.resetRotationAngles();
        this.vehicleResetVisibilities();
    }

    /**
     * Resets all the rotations and rotation points back to their initial values. This makes it
     * so ever developer doesn't have to do it themselves.
     */
    // FIXME still actual?
    @Unique
    private void resetRotationAngles()
    {
        PlayerModel<Player> self = (PlayerModel<Player>) (Object) this;

        this.vehicleResetAll(self.head);
        this.vehicleResetAll(self.hat);
        this.vehicleResetAll(self.body);
        this.vehicleResetAll(self.jacket);

        this.vehicleResetAll(self.rightArm);
        self.rightArm.x = -5.0F;
        self.rightArm.y = this.slim ? 2.5F : 2.0F;
        self.rightArm.z = 0.0F;

        this.vehicleResetAll(self.rightSleeve);
        self.rightSleeve.x = -5.0F;
        self.rightSleeve.y = this.slim ? 2.5F : 2.0F;
        self.rightSleeve.z = 10.0F;

        this.vehicleResetAll(self.leftArm);
        self.leftArm.x = 5.0F;
        self.leftArm.y = this.slim ? 2.5F : 2.0F;
        self.leftArm.z = 0.0F;

        this.vehicleResetAll(self.leftSleeve);
        self.leftSleeve.x = 5.0F;
        self.leftSleeve.y = this.slim ? 2.5F : 2.0F;
        self.leftSleeve.z = 0.0F;

        this.vehicleResetAll(self.leftLeg);
        self.leftLeg.x = 1.9F;
        self.leftLeg.y = 12.0F;
        self.leftLeg.z = 0.0F;

        this.vehicleResetAll(self.leftPants);
        self.leftPants.copyFrom(self.leftLeg);

        this.vehicleResetAll(self.rightLeg);
        self.rightLeg.x = -1.9F;
        self.rightLeg.y = 12.0F;
        self.rightLeg.z = 0.0F;

        this.vehicleResetAll(self.rightPants);
        self.rightPants.copyFrom(self.rightLeg);
    }

    /*
     * Resets the rotation angles and points to zero for the given model renderer
     */
    @Unique
    private void vehicleResetAll(ModelPart part)
    {
        part.xRot = 0.0F;
        part.yRot = 0.0F;
        part.zRot = 0.0F;
        part.x = 0.0F;
        part.y = 0.0F;
        part.z = 0.0F;
    }

    @Unique
    private void vehicleResetVisibilities()
    {
        PlayerModel<Player> self = (PlayerModel<Player>) (Object) this;
        self.head.visible = true;
        self.body.visible = true;
        self.rightArm.visible = true;
        self.leftArm.visible = true;
        self.rightLeg.visible = true;
        self.leftLeg.visible = true;
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "TAIL"))
    void setRotationAnglesTail(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback)
    {
        if(!(entityIn instanceof Player))
            return;

        PlayerModel<Player> self = (PlayerModel<Player>) (Object) this;
        PlayerModelHandler.onSetupAngles((Player) entityIn, self, Minecraft.getInstance().getDeltaFrameTime());
        HeldVehicleHandler.onSetupAngles((Player) entityIn, self, Minecraft.getInstance().getDeltaFrameTime());
        this.vehicleSetupRotationAngles();
    }

    @Unique
    private void vehicleSetupRotationAngles()
    {
        PlayerModel<Player> self = (PlayerModel<Player>) (Object) this;
        self.leftPants.copyFrom(self.leftLeg);
        self.rightPants.copyFrom(self.rightLeg);
        self.leftSleeve.copyFrom(self.leftArm);
        self.rightSleeve.copyFrom(self.rightArm);
        self.jacket.copyFrom(self.body);
        self.hat.copyFrom(self.head);
    }
}
