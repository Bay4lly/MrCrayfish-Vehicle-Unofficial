package com.mrcrayfish.vehicle.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.vehicle.client.handler.FuelingHandler;
import com.mrcrayfish.vehicle.client.handler.PlayerModelHandler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// FIXME cleanup
// Based on: https://github.com/MrCrayfish/Obfuscate/blob/1.17.X/src/main/java/com/mrcrayfish/obfuscate/mixin/client/LivingRendererMixin.java#L41
@Mixin(LivingEntityRenderer.class)
class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
{
    @Shadow
    protected M model;

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    void fireRenderPlayerPre(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo callback)
    {
        if(!(entity instanceof Player))
            return;

        PlayerModelHandler.onPreRender((Player) entity, poseStack, partialTick);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(shift = Shift.AFTER, value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    void fireRenderPlayerPost(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo callback)
    {
        if(!(entity instanceof Player))
            return;

        FuelingHandler.onModelRenderPost(entity, this.model, poseStack);
    }
}
