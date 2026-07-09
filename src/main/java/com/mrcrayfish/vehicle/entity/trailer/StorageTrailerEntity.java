/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.fml.loading.FMLEnvironment
 */
package com.mrcrayfish.vehicle.entity.trailer;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.common.inventory.IStorage;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageOpenStorage;
import com.mrcrayfish.vehicle.util.InventoryUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

public class StorageTrailerEntity
extends TrailerEntity
implements IStorage {
    private static final EntityRayTracer.RayTracePart CONNECTION_BOX = new EntityRayTracer.RayTracePart(StorageTrailerEntity.createScaledBoundingBox(-0.375, 0.2625, 0.5625, 0.375, 0.51875, 1.0625, 1.1));
    private static final EntityRayTracer.RayTracePart CHEST_BOX = new EntityRayTracer.RayTracePart(new AABB(-0.4375, 0.475, -0.4375, 0.4375, 1.34, 0.4375));
    private static final Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> interactionBoxMapStatic = StorageTrailerEntity.buildInteractionBoxMap();
    private StorageInventory inventory;

    private static Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> buildInteractionBoxMap() {
        if (!FMLEnvironment.dist.isClient()) {
            return null;
        }
        HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> map = new HashMap<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList>();
        map.put(CONNECTION_BOX, EntityRayTracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        map.put(CHEST_BOX, EntityRayTracer.boxToTriangles(CHEST_BOX.getBox(), null));
        return map;
    }

    public StorageTrailerEntity(EntityType<? extends StorageTrailerEntity> type, Level worldIn) {
        super(type, worldIn);
        this.initInventory();
    }

    @Override
    public double getHitchOffset() {
        return -17.6;
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
    @OnlyIn(value=Dist.CLIENT)
    public Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> getStaticInteractionBoxMap() {
        return interactionBoxMapStatic;
    }

    @Override
    @Nullable
    @OnlyIn(value=Dist.CLIENT)
    public List<EntityRayTracer.RayTracePart> getApplicableInteractionBoxes() {
        return ImmutableList.of(CONNECTION_BOX, CHEST_BOX);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean processHit(EntityRayTracer.RayTraceResultRotated result, boolean rightClick) {
        if (rightClick) {
            if (result.getPartHit() == CONNECTION_BOX) {
                PacketHandler.sendToServer(new MessageAttachTrailer(this.getId(), Minecraft.getInstance().player.getId()));
                return true;
            }
            if (result.getPartHit() == CHEST_BOX) {
                PacketHandler.sendToServer(new MessageOpenStorage(this.getId()));
                Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
                return true;
            }
        }
        return super.processHit(result, rightClick);
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
    public boolean canTowTrailer() {
        return true;
    }

    @Override
    public void startOpen(Player player) {
        this.playSound(SoundEvents.CHEST_OPEN, 0.5f, 0.9f);
    }

    @Override
    public Component getStorageName() {
        return this.getDisplayName();
    }
}

