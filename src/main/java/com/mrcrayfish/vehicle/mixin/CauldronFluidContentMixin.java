package com.mrcrayfish.vehicle.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = CauldronFluidContent.class, remap = false)
public class CauldronFluidContentMixin
{
    @Shadow
    private static Map<Block, CauldronFluidContent> BLOCK_TO_CAULDRON;

    @Inject(method = "register", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onRegister(Block block, Fluid fluid, int totalAmount, IntegerProperty levelProperty, CallbackInfo ci)
    {
        if(BLOCK_TO_CAULDRON.containsKey(block))
        {
            ci.cancel();
        }
    }
}
