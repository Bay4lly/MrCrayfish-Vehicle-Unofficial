package com.mrcrayfish.vehicle.common.data;

import com.mrcrayfish.framework.api.sync.IDataSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class Serializers
{
    public static final IDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new IDataSerializer<Optional<BlockPos>>()
    {
        @Override
        public void write(FriendlyByteBuf buffer, Optional<BlockPos> optional)
        {
            buffer.writeBoolean(optional.isPresent());
            optional.ifPresent(buffer::writeBlockPos);
        }

        @Override
        public Optional<BlockPos> read(FriendlyByteBuf buffer)
        {
            if(buffer.readBoolean())
            {
                return Optional.of(buffer.readBlockPos());
            }
            return Optional.empty();
        }

        @Override
        public Tag write(Optional<BlockPos> value)
        {
            CompoundTag compound = new CompoundTag();
            compound.putBoolean("Present", value.isPresent());
            value.ifPresent(blockPos -> compound.putLong("BlockPos", value.get().asLong()));
            return compound;
        }

        @Override
        public Optional<BlockPos> read(Tag nbt)
        {
            CompoundTag compound = (CompoundTag) nbt;
            if(compound.getBoolean("Present"))
            {
                BlockPos pos = BlockPos.of(compound.getLong("BlockPos"));
                return Optional.of(pos);
            }
            return Optional.empty();
        }
    };
}
