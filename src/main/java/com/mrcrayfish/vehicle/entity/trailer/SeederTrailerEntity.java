/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemNameBlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.FarmBlock
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.fml.loading.FMLEnvironment
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.mrcrayfish.vehicle.entity.trailer;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageSyncInventory;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.Tags;

public class SeederTrailerEntity
extends TrailerEntity
implements IStorage {
    private static final EntityRayTracer.RayTracePart CONNECTION_BOX = new EntityRayTracer.RayTracePart(SeederTrailerEntity.createScaledBoundingBox(-0.4375, 0.3875, 0.375, 0.4375, 0.525, 1.0625, 1.1));
    private static final Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> interactionBoxMapStatic = SeederTrailerEntity.buildInteractionBoxMap();
    private int inventoryTimer;
    private StorageInventory inventory;

    private static Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> buildInteractionBoxMap() {
        if (!FMLEnvironment.dist.isClient()) {
            return null;
        }
        HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> map = new HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList>();
        map.put(CONNECTION_BOX, EntityRayTracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        return map;
    }

    public SeederTrailerEntity(EntityType<? extends SeederTrailerEntity> type, Level worldIn) {
        super(type, worldIn);
        this.initInventory();
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if ((heldItem.isEmpty() || !(heldItem.getItem() instanceof SprayCanItem)) && player instanceof ServerPlayer) {
            ((ServerPlayer)player).openMenu((MenuProvider)this.getInventory(), buffer -> buffer.writeVarInt(this.getId()));
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && (Integer)Config.SERVER.trailerInventorySyncCooldown.get() > 0 && this.inventoryTimer++ == (Integer)Config.SERVER.trailerInventorySyncCooldown.get()) {
            this.inventoryTimer = 0;
            PacketHandler.sendToTrackingEntity(this, new MessageSyncInventory(this.getId(), this.inventory));
        }
    }

    @Override
    public void onUpdateVehicle() {
        super.onUpdateVehicle();
        Vec3 lookVec = this.getLookAngle();
        this.plantSeed(lookVec.yRot((float)Math.toRadians(90.0)).scale(0.85));
        this.plantSeed(Vec3.ZERO);
        this.plantSeed(lookVec.yRot((float)Math.toRadians(-90.0)).scale(0.85));
    }

    private void plantSeed(Vec3 vec) {
        BlockPos pos = BlockPos.containing((double)(this.xo + vec.x), (double)(this.yo + 0.25), (double)(this.zo + vec.z));
        if (this.level().isEmptyBlock(pos) && this.level().getBlockState(pos.below()).getBlock() instanceof FarmBlock) {
            ItemStack seed = this.getSeed();
            if (seed.isEmpty() && this.getPullingEntity() instanceof StorageTrailerEntity) {
                seed = this.getSeedFromStorage((StorageTrailerEntity)this.getPullingEntity());
            }
            if (this.isSeed(seed)) {
                Block seedBlock = ((ItemNameBlockItem)seed.getItem()).getBlock();
                this.level().setBlockAndUpdate(pos, seedBlock.defaultBlockState());
                seed.shrink(1);
            }
        }
    }

    private ItemStack getSeed() {
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = this.inventory.getItem(i);
            if (!this.isSeed(stack)) continue;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    private boolean isSeed(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemNameBlockItem && ((ItemNameBlockItem)stack.getItem()).getBlock() instanceof CropBlock;
    }

    private ItemStack getSeedFromStorage(StorageTrailerEntity storageTrailer) {
        if (storageTrailer == null) {
            return ItemStack.EMPTY;
        }
        if (storageTrailer.getInventory() != null) {
            StorageInventory storage = storageTrailer.getInventory();
            for (int i = 0; i < storage.getContainerSize(); ++i) {
                ItemStack stack = storage.getItem(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof ItemNameBlockItem)) continue;
                return stack;
            }
            if (storageTrailer.getPullingEntity() instanceof StorageTrailerEntity) {
                return this.getSeedFromStorage((StorageTrailerEntity)storageTrailer.getPullingEntity());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Inventory", 9)) {
            this.initInventory();
            InventoryUtil.readInventoryToNBT(compound, "Inventory", this.inventory, (HolderLookup.Provider)this.level().registryAccess());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.inventory != null) {
            InventoryUtil.writeInventoryToNBT(compound, "Inventory", (Container)this.inventory, (HolderLookup.Provider)this.level().registryAccess());
        }
    }

    private void initInventory() {
        StorageInventory original = this.inventory;
        this.inventory = new StorageInventory(this, 27);
        if (original != null) {
            for (int i = 0; i < original.getContainerSize(); ++i) {
                ItemStack stack = original.getItem(i);
                if (stack.isEmpty()) continue;
                this.inventory.setItem(i, stack.copy());
            }
        }
    }

    @Override
    protected void onVehicleDestroyed(LivingEntity entity) {
        super.onVehicleDestroyed(entity);
        if (this.inventory != null) {
            Containers.dropContents((Level)this.level(), (Entity)this, (Container)this.inventory);
        }
    }

    @Override
    public StorageInventory getInventory() {
        return this.inventory;
    }

    @Override
    public double getHitchOffset() {
        return -17.6;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> getStaticInteractionBoxMap() {
        return interactionBoxMapStatic;
    }

    @Override
    @Nullable
    @OnlyIn(value=Dist.CLIENT)
    public List<EntityRayTracer.RayTracePart> getApplicableInteractionBoxes() {
        return ImmutableList.of(CONNECTION_BOX);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean processHit(EntityRayTracer.RayTraceResultRotated result, boolean rightClick) {
        if (rightClick && result.getPartHit() == CONNECTION_BOX) {
            PacketHandler.sendToServer(new MessageAttachTrailer(this.getId(), Minecraft.getInstance().player.getId()));
            return true;
        }
        return super.processHit(result, rightClick);
    }

    @Override
    public boolean isStorageItem(ItemStack stack) {
        return !stack.isEmpty() && stack.is(Tags.Items.SEEDS);
    }

    @Override
    public Component getStorageName() {
        return this.getDisplayName();
    }
}

