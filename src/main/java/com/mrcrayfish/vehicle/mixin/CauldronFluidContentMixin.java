/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.fluids.CauldronFluidContent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.mrcrayfish.vehicle.mixin;

import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.CauldronFluidContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={CauldronFluidContent.class}, remap=false)
public class CauldronFluidContentMixin {
    @Shadow
    private static Map<Block, CauldronFluidContent> BLOCK_TO_CAULDRON;

    @Inject(method={"register"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void onRegister(Block block, Fluid fluid, int totalAmount, IntegerProperty levelProperty, CallbackInfo ci) {
        if (BLOCK_TO_CAULDRON.containsKey(block)) {
            ci.cancel();
        }
    }
}

