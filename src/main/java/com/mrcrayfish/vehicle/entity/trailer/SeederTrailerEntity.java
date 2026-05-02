package com.mrcrayfish.vehicle.entity.trailer;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageSyncInventory;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class SeederTrailerEntity extends TrailerEntity implements IStorage
{
    private static final EntityRayTracer.RayTracePart CONNECTION_BOX = new EntityRayTracer.RayTracePart(createScaledBoundingBox(-7 * 0.0625, 6.2 * 0.0625, 6 * 0.0625, 7 * 0.0625, 8.4 * 0.0625F, 17 * 0.0625, 1.1));
    private static final Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> interactionBoxMapStatic = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> {
        Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> map = new HashMap<>();
        map.put(CONNECTION_BOX, EntityRayTracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        return map;
    });

    private int inventoryTimer;
    private StorageInventory inventory;

    public SeederTrailerEntity(EntityType<? extends SeederTrailerEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.initInventory();
    }

    public boolean canBeColored()
    {
        return true;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger)
    {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand)
    {
        ItemStack heldItem = player.getItemInHand(hand);
        if((heldItem.isEmpty() || !(heldItem.getItem() instanceof SprayCanItem)) && player instanceof ServerPlayer)
        {
            NetworkHooks.openScreen((ServerPlayer) player, this.getInventory(), buffer -> buffer.writeVarInt(this.getId()));
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    @Override
    public void tick()
    {
        super.tick();
        if(!this.level().isClientSide && Config.SERVER.trailerInventorySyncCooldown.get() > 0 && inventoryTimer++ == Config.SERVER.trailerInventorySyncCooldown.get())
        {
            this.inventoryTimer = 0;
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> SeederTrailerEntity.this), new MessageSyncInventory(this.getId(), this.inventory));
        }
    }

    @Override
    public void onUpdateVehicle()
    {
        super.onUpdateVehicle();

        Vec3 lookVec = this.getLookAngle();
        this.plantSeed(lookVec.yRot((float) Math.toRadians(90F)).scale(0.85));
        this.plantSeed(Vec3.ZERO);
        this.plantSeed(lookVec.yRot((float) Math.toRadians(-90F)).scale(0.85));
    }

    private void plantSeed(Vec3 vec)
    {
        BlockPos pos = BlockPos.containing(xo + vec.x, yo + 0.25, zo + vec.z); // FIXME
        if(level().isEmptyBlock(pos) && level().getBlockState(pos.below()).getBlock() instanceof FarmBlock)
        {
            ItemStack seed = this.getSeed();
            if(seed.isEmpty() && this.getPullingEntity() instanceof StorageTrailerEntity)
            {
                seed = this.getSeedFromStorage((StorageTrailerEntity) this.getPullingEntity());
            }
            if(this.isSeed(seed))
            {
                Block seedBlock = ((ItemNameBlockItem) seed.getItem()).getBlock();
                this.level().setBlockAndUpdate(pos, seedBlock.defaultBlockState());
                seed.shrink(1);
            }
        }
    }

    private ItemStack getSeed()
    {
        for(int i = 0; i < this.inventory.getContainerSize(); i++)
        {
            ItemStack stack = this.inventory.getItem(i);
            if(this.isSeed(stack))
            {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean isSeed(ItemStack stack)
    {
        return !stack.isEmpty() && stack.getItem() instanceof ItemNameBlockItem && ((ItemNameBlockItem) stack.getItem()).getBlock() instanceof CropBlock;
    }

    private ItemStack getSeedFromStorage(StorageTrailerEntity storageTrailer)
    {
        if(storageTrailer == null)
            return ItemStack.EMPTY;

        if(storageTrailer.getInventory() != null)
        {
            StorageInventory storage = storageTrailer.getInventory();
            for(int i = 0; i < storage.getContainerSize(); i++)
            {
                ItemStack stack = storage.getItem(i);
                if(!stack.isEmpty() && stack.getItem() instanceof net.minecraftforge.common.IPlantable)
                {
                    return stack;
                }
            }

            if(storageTrailer.getPullingEntity() instanceof StorageTrailerEntity)
            {
                return this.getSeedFromStorage((StorageTrailerEntity) storageTrailer.getPullingEntity());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Inventory", Tag.TAG_LIST))
        {
            this.initInventory();
            InventoryUtil.readInventoryToNBT(compound, "Inventory", this.inventory);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        if(this.inventory != null)
        {
            InventoryUtil.writeInventoryToNBT(compound, "Inventory", this.inventory);
        }
    }

    private void initInventory()
    {
        StorageInventory original = this.inventory;
        this.inventory = new StorageInventory(this, 27);
        // Copies the inventory if it exists already over to the new instance
        if(original != null)
        {
            for(int i = 0; i < original.getContainerSize(); i++)
            {
                ItemStack stack = original.getItem(i);
                if(!stack.isEmpty())
                {
                    inventory.setItem(i, stack.copy());
                }
            }
        }
    }

    @Override
    protected void onVehicleDestroyed(LivingEntity entity)
    {
        super.onVehicleDestroyed(entity);
        if(inventory != null)
        {
            Containers.dropContents(level(), this, inventory);
        }
    }

    @Override
    public StorageInventory getInventory()
    {
        return inventory;
    }

    @Override
    public double getHitchOffset()
    {
        return -16.0 * 1.1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<EntityRayTracer.RayTracePart> getApplicableInteractionBoxes()
    {
        return ImmutableList.of(CONNECTION_BOX);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean processHit(EntityRayTracer.RayTraceResultRotated result, boolean rightClick)
    {
        if(rightClick)
        {
            if(result.getPartHit() == CONNECTION_BOX)
            {
                PacketHandler.instance.sendToServer(new MessageAttachTrailer(this.getId(), Minecraft.getInstance().player.getId()));
                return true;
            }
        }
        return super.processHit(result, rightClick);
    }

    @Override
    public boolean isStorageItem(ItemStack stack)
    {
        return !stack.isEmpty() && stack.is(Tags.Items.SEEDS);
    }

    @Override
    public Component getStorageName()
    {
        return this.getDisplayName();
    }
}
