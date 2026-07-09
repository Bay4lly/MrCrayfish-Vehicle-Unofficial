/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mrcrayfish.framework.api.sync.DataSerializer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 */
package com.mrcrayfish.vehicle.common.data;

import com.mrcrayfish.framework.api.sync.DataSerializer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class Serializers {
    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<BlockPos>> OPTIONAL_BLOCK_POS_STREAM_CODEC = StreamCodec.of((buf, optional) -> {
        buf.writeBoolean(optional.isPresent());
        optional.ifPresent(arg_0 -> ((RegistryFriendlyByteBuf)buf).writeBlockPos(arg_0));
    }, buf -> {
        if (buf.readBoolean()) {
            return Optional.of(buf.readBlockPos());
        }
        return Optional.empty();
    });
    public static final DataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new DataSerializer<>(OPTIONAL_BLOCK_POS_STREAM_CODEC, (optional, provider) -> {
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("Present", optional.isPresent());
        optional.ifPresent(blockPos -> compound.putLong("BlockPos", blockPos.asLong()));
        return compound;
    }, (nbt, provider) -> {
        CompoundTag compound = (CompoundTag)nbt;
        if (compound.getBoolean("Present")) {
            return Optional.of(BlockPos.of((long)compound.getLong("BlockPos")));
        }
        return Optional.empty();
    });
}

