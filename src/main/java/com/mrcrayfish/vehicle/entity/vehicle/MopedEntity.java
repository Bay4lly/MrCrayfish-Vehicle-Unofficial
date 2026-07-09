/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.network.syncher.SynchedEntityData$Builder
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.component.ItemContainerContents
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.fml.loading.FMLEnvironment
 */
package com.mrcrayfish.vehicle.entity.vehicle;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.inventory.IAttachableChest;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.MotorcycleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachChest;
import com.mrcrayfish.vehicle.network.message.MessageOpenStorage;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

public class MopedEntity
extends MotorcycleEntity
implements IAttachableChest {
    private static final EntityDataAccessor<Boolean> CHEST = SynchedEntityData.defineId(MopedEntity.class, (EntityDataSerializer)EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHEST_OPEN = SynchedEntityData.defineId(MopedEntity.class, (EntityDataSerializer)EntityDataSerializers.BOOLEAN);
    private static final EntityRayTracer.RayTracePart CHEST_BOX = new EntityRayTracer.RayTracePart(MopedEntity.createBoxScaled(-3.5, 10.5, -7.0, 3.5, 17.5, -14.0, 1.2));
    private static final EntityRayTracer.RayTracePart TRAY_BOX = new EntityRayTracer.RayTracePart(MopedEntity.createBoxScaled(-4.0, 9.5, -6.5, 4.0, 10.5, -14.5, 1.2));
    private static final Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> interactionBoxMapStatic = MopedEntity.buildInteractionBoxMap();
    private StorageInventory inventory;
    @OnlyIn(value=Dist.CLIENT)
    private float openProgress;
    @OnlyIn(value=Dist.CLIENT)
    private float prevOpenProgress;

    private static Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> buildInteractionBoxMap() {
        if (!FMLEnvironment.dist.isClient()) {
            return null;
        }
        HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> map = new HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList>();
        map.put(CHEST_BOX, EntityRayTracer.boxToTriangles(CHEST_BOX.getBox(), null));
        map.put(TRAY_BOX, EntityRayTracer.boxToTriangles(TRAY_BOX.getBox(), null));
        return map;
    }

    public MopedEntity(EntityType<? extends MopedEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setMaxSpeed(12.0f);
        this.setTurnSensitivity(5);
        this.setMaxTurnAngle(45);
        this.setFuelCapacity(12000.0f);
        this.setFuelConsumption(0.225f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHEST, false);
        builder.define(CHEST_OPEN, false);
    }

    @Override
    public SoundEvent getEngineSound() {
        return (SoundEvent)ModSounds.ENTITY_MOPED_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch() {
        return 0.5f;
    }

    @Override
    public float getMaxEnginePitch() {
        return 1.2f;
    }

    @Override
    public boolean canBeColored() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Chest", 1)) {
            this.setChest(compound.getBoolean("Chest"));
            if (compound.contains("Inventory", 9)) {
                this.initInventory();
                InventoryUtil.readInventoryToNBT(compound, "Inventory", this.inventory, (HolderLookup.Provider)this.level().registryAccess());
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Chest", this.hasChest());
        if (this.hasChest() && this.inventory != null) {
            InventoryUtil.writeInventoryToNBT(compound, "Inventory", (Container)this.inventory, (HolderLookup.Provider)this.level().registryAccess());
        }
    }

    @Override
    public boolean hasChest() {
        return (Boolean)this.entityData.get(CHEST);
    }

    public void setChest(boolean chest) {
        this.entityData.set(CHEST, chest);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean processHit(EntityRayTracer.RayTraceResultRotated result, boolean rightClick) {
        if (rightClick) {
            EntityRayTracer.RayTracePart partHit = result.getPartHit();
            if (partHit == CHEST_BOX && this.hasChest()) {
                PacketHandler.sendToServer(new MessageOpenStorage(this.getId()));
                Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
                return true;
            }
            if (partHit == TRAY_BOX && !this.hasChest()) {
                PacketHandler.sendToServer(new MessageAttachChest(this.getId()));
                Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
                return true;
            }
        }
        return super.processHit(result, rightClick);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> getStaticInteractionBoxMap() {
        return interactionBoxMapStatic;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public List<EntityRayTracer.RayTracePart> getApplicableInteractionBoxes() {
        ArrayList boxes = Lists.newArrayList();
        if (this.hasChest()) {
            boxes.add(CHEST_BOX);
        } else {
            boxes.add(TRAY_BOX);
        }
        return boxes;
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
        if (this.hasChest() && this.inventory != null) {
            Containers.dropContents((Level)this.level(), (Entity)this, (Container)this.inventory);
        }
    }

    @Override
    @Nullable
    public StorageInventory getInventory() {
        if (this.hasChest() && this.inventory == null) {
            this.initInventory();
        }
        return this.inventory;
    }

    @Override
    public void attachChest(ItemStack stack) {
        block2: {
            CompoundTag blockEntityTag;
            block3: {
                if (stack.isEmpty() || stack.getItem() != Item.byBlock((Block)Blocks.CHEST)) break block2;
                this.setChest(true);
                this.initInventory();
                ItemContainerContents contents = (ItemContainerContents)stack.get(DataComponents.CONTAINER);
                if (contents == null) break block3;
                NonNullList chestInventory = NonNullList.withSize((int)27, ItemStack.EMPTY);
                contents.copyInto(chestInventory);
                for (int i = 0; i < chestInventory.size(); ++i) {
                    this.inventory.setItem(i, (ItemStack)chestInventory.get(i));
                }
                break block2;
            }
            CustomData customData = (CustomData)stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (customData == null || (blockEntityTag = customData.copyTag()).isEmpty() || !blockEntityTag.contains("Items", 9)) break block2;
            NonNullList chestInventory = NonNullList.withSize((int)27, ItemStack.EMPTY);
            ContainerHelper.loadAllItems((CompoundTag)blockEntityTag, (NonNullList)chestInventory, (HolderLookup.Provider)this.level().registryAccess());
            for (int i = 0; i < chestInventory.size(); ++i) {
                this.inventory.setItem(i, (ItemStack)chestInventory.get(i));
            }
        }
    }

    @Override
    public void removeChest() {
        if (this.inventory != null) {
            Vec3 target = this.getChestPosition();
            InventoryUtil.dropInventoryItems(this.level(), target.x, target.y, target.z, (Container)this.inventory);
            this.inventory = null;
            this.setChest(false);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
            this.level().addFreshEntity((Entity)new ItemEntity(this.level(), target.x, target.y, target.z, new ItemStack((ItemLike)Blocks.CHEST)));
        }
    }

    @Override
    public boolean isLockable() {
        return false;
    }

    @Override
    public Component getStorageName() {
        return this.getDisplayName();
    }

    @Override
    public void startOpen(Player player) {
        Vec3 target = this.getChestPosition();
        this.level().playSound(null, target.x, target.y, target.z, SoundEvents.CHEST_OPEN, this.getSoundSource(), 0.5f, 0.9f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasChest()) {
            if (!this.level().isClientSide()) {
                this.entityData.set(CHEST_OPEN, this.getPlayerCountInChest() > 0);
            } else {
                this.prevOpenProgress = this.openProgress;
                if (((Boolean)this.entityData.get(CHEST_OPEN)).booleanValue()) {
                    this.openProgress = Math.min(1.0f, this.openProgress + 0.1f);
                } else {
                    float lastOpenProgress = this.openProgress;
                    this.openProgress = Math.max(0.0f, this.openProgress - 0.1f);
                    if (this.openProgress < 0.5f && lastOpenProgress >= 0.5f) {
                        Vec3 target = this.getChestPosition();
                        this.level().playLocalSound(target.x, target.y, target.z, SoundEvents.CHEST_CLOSE, this.getSoundSource(), 0.5f, this.level().random.nextFloat() * 0.1f + 0.9f, false);
                    }
                }
            }
        }
    }

    protected Vec3 getChestPosition() {
        return new Vec3(0.0, 1.0, -0.75).yRot(-(this.getYRot() - this.additionalYaw) * ((float)Math.PI / 180)).add(this.position());
    }

    protected int getPlayerCountInChest() {
        if (!this.hasChest()) {
            return 0;
        }
        int count = 0;
        for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(5.0))) {
            Container container;
            if (!(player.containerMenu instanceof StorageContainer) || (container = ((StorageContainer)player.containerMenu).getStorageInventory()) != this) continue;
            ++count;
        }
        return count;
    }

    @OnlyIn(value=Dist.CLIENT)
    public float getOpenProgress() {
        return this.openProgress;
    }

    @OnlyIn(value=Dist.CLIENT)
    public float getPrevOpenProgress() {
        return this.prevOpenProgress;
    }
}

