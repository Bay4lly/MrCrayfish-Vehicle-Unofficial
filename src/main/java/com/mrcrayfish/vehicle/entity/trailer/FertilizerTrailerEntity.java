/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
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
 *  net.minecraft.world.item.BoneMealItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.BonemealableBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.fml.loading.FMLEnvironment
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
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

public class FertilizerTrailerEntity
extends TrailerEntity
implements IStorage {
    private static final EntityRayTracer.RayTracePart CONNECTION_BOX = new EntityRayTracer.RayTracePart(FertilizerTrailerEntity.createScaledBoundingBox(-0.4375, 0.3875, 0.375, 0.4375, 0.525, 1.125, 1.1));
    private static final Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> interactionBoxMapStatic = FertilizerTrailerEntity.buildInteractionBoxMap();
    private int inventoryTimer;
    private StorageInventory inventory;
    private BlockPos[] lastPos = new BlockPos[3];

    private static Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> buildInteractionBoxMap() {
        if (!FMLEnvironment.dist.isClient()) {
            return null;
        }
        HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> map = new HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList>();
        map.put(CONNECTION_BOX, EntityRayTracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        return map;
    }

    public FertilizerTrailerEntity(EntityType<? extends FertilizerTrailerEntity> type, Level worldIn) {
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
        if (!this.level().isClientSide) {
            ItemStack fertilizer = this.getFertilizer();
            if (fertilizer.isEmpty() && this.getPullingEntity() instanceof StorageTrailerEntity) {
                fertilizer = this.getFertilizerFromStorage((StorageTrailerEntity)this.getPullingEntity());
            }
            if (!fertilizer.isEmpty()) {
                Vec3 lookVec = this.getLookAngle();
                boolean applied = this.applyFertilizer(lookVec.yRot((float)Math.toRadians(90.0)), 0);
                applied |= this.applyFertilizer(Vec3.ZERO, 1);
                if (applied |= this.applyFertilizer(lookVec.yRot((float)Math.toRadians(-90.0)), 2)) {
                    fertilizer.shrink(1);
                }
            }
        }
    }

    private boolean applyFertilizer(Vec3 vec, int index) {
        BonemealableBlock growable;
        Vec3 prevPosVec = new Vec3(this.xo, this.yo + 0.25, this.zo);
        prevPosVec = prevPosVec.add(new Vec3(0.0, 0.0, -1.0).yRot(-this.getYRot() * ((float)Math.PI / 180)));
        BlockPos pos = BlockPos.containing((double)(prevPosVec.x + vec.x), (double)prevPosVec.y, (double)(prevPosVec.z + vec.z));
        if (this.lastPos[index] != null && this.lastPos[index].equals((Object)pos)) {
            return false;
        }
        this.lastPos[index] = pos;
        BlockState state = this.level().getBlockState(pos);
        if (state.getBlock() instanceof BonemealableBlock && (growable = (BonemealableBlock)state.getBlock()).isValidBonemealTarget((LevelReader)this.level(), pos, state) && growable.isBonemealSuccess(this.level(), this.random, pos, state)) {
            growable.performBonemeal((ServerLevel)this.level(), this.random, pos, state);
            this.level().levelEvent(2005, pos, 0);
            return true;
        }
        return false;
    }

    private ItemStack getFertilizer() {
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = this.inventory.getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BoneMealItem)) continue;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getFertilizerFromStorage(StorageTrailerEntity storageTrailer) {
        if (storageTrailer == null) {
            return ItemStack.EMPTY;
        }
        if (storageTrailer.getInventory() != null) {
            StorageInventory storage = storageTrailer.getInventory();
            for (int i = 0; i < storage.getContainerSize(); ++i) {
                ItemStack stack = storage.getItem(i);
                if (stack.isEmpty() || !(stack.getItem() instanceof BoneMealItem)) continue;
                return stack;
            }
            if (storageTrailer.getPullingEntity() instanceof StorageTrailerEntity) {
                return this.getFertilizerFromStorage((StorageTrailerEntity)storageTrailer.getPullingEntity());
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
        return -18.700000000000003;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> getStaticInteractionBoxMap() {
        return interactionBoxMapStatic;
    }

    @Override
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
        return !stack.isEmpty() && stack.getItem() instanceof BoneMealItem;
    }

    @Override
    public Component getStorageName() {
        return this.getDisplayName();
    }
}

