package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class LawnMowerEntity extends LandVehicleEntity
{
    public LawnMowerEntity(EntityType<? extends LawnMowerEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(8);
        this.setFuelCapacity(5000F);
    }

    @Override
    public void updateVehicle()
    {
        super.updateVehicle();

        if(!level().isClientSide && this.getControllingPassenger() != null)
        {
            AABB axisAligned = this.getBoundingBox().inflate(0.25);
            Vec3 lookVec = this.getLookAngle().scale(0.5);
            int minX = Mth.floor(axisAligned.minX + lookVec.x);
            int maxX = Mth.ceil(axisAligned.maxX + lookVec.x);
            int minZ = Mth.floor(axisAligned.minZ + lookVec.z);
            int maxZ = Mth.ceil(axisAligned.maxZ + lookVec.z);

            for(int x = minX; x < maxX; x++)
            {
                for(int z = minZ; z < maxZ; z++)
                {
                    BlockPos pos = new BlockPos(x, (int) (axisAligned.minY + 0.5), z); // FIXME
                    BlockState state = level().getBlockState(pos);

                    StorageTrailerEntity trailer = null;
                    if(getTrailer() instanceof StorageTrailerEntity)
                    {
                        trailer = (StorageTrailerEntity) getTrailer();
                    }

                    if(state.getBlock() instanceof BushBlock)
                    {
                        List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level(), pos, null);
                        for(ItemStack stack : drops)
                        {
                            this.addItemToStorage(trailer, stack);
                        }
                        level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        level().playSound(null, pos, state.getBlock().getSoundType(state, level(), pos, this).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        level().levelEvent(2001, pos, Block.getId(state));
                    }
                }
            }
        }
    }

    private void addItemToStorage(StorageTrailerEntity storageTrailer, ItemStack stack)
    {
        if(stack.isEmpty())
            return;

        if(storageTrailer != null && storageTrailer.getInventory() != null)
        {
            StorageInventory storage = storageTrailer.getInventory();
            stack = storage.addItem(stack);
            if(!stack.isEmpty())
            {
                if(storageTrailer.getTrailer() instanceof StorageTrailerEntity)
                {
                    this.addItemToStorage((StorageTrailerEntity) storageTrailer.getTrailer(), stack);
                }
                else
                {
                    spawnItemStack(level(), stack);
                }
            }
        }
        else
        {
            spawnItemStack(level(), stack);
        }
    }

    private void spawnItemStack(Level worldIn, ItemStack stack)
    {
        while(!stack.isEmpty())
        {
            ItemEntity itemEntity = new ItemEntity(worldIn, xo, yo, zo, stack.split(random.nextInt(21) + 10));
            itemEntity.setPickUpDelay(20);
            itemEntity.setDeltaMovement(-this.getDeltaMovement().x / 4.0, random.nextGaussian() * 0.05D + 0.2D, -this.getDeltaMovement().z / 4.0);
            worldIn.addFreshEntity(itemEntity);
        }
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_ATV_ENGINE.get();
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canTowTrailer()
    {
        return true;
    }

    //TODO remove and add key support
    @Override
    public boolean isLockable()
    {
        return false;
    }
}
