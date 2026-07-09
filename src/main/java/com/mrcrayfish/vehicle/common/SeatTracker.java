/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBiMap
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 */
package com.mrcrayfish.vehicle.common;

import com.google.common.collect.HashBiMap;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncPlayerSeat;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SeatTracker {
    private final int maxSeatSize;
    private HashBiMap<UUID, Integer> playerSeatMap = HashBiMap.create();
    private WeakReference<VehicleEntity> vehicleRef;

    public SeatTracker(VehicleEntity entity) {
        this.maxSeatSize = entity.getProperties().getSeats().size();
        this.vehicleRef = new WeakReference<VehicleEntity>(entity);
    }

    public int getSeatIndex(UUID uuid) {
        if (this.playerSeatMap.containsKey(uuid)) {
            return (Integer)this.playerSeatMap.getOrDefault(uuid, -1);
        }
        return -1;
    }

    public void setSeatIndex(int index, UUID uuid) {
        if (index < 0 || index >= this.maxSeatSize) {
            return;
        }
        this.playerSeatMap.forcePut(uuid, index);
        VehicleEntity vehicle = (VehicleEntity)this.vehicleRef.get();
        if (vehicle != null && !vehicle.level().isClientSide) {
            PacketHandler.sendToTrackingEntity(vehicle, new MessageSyncPlayerSeat(vehicle.getId(), index, uuid));
        }
    }

    public boolean isSeatAvailable(int index) {
        if (index < 0 || index >= this.maxSeatSize) {
            return false;
        }
        if (!this.playerSeatMap.inverse().containsKey(index)) {
            return true;
        }
        VehicleEntity vehicle = (VehicleEntity)this.vehicleRef.get();
        if (vehicle != null) {
            UUID uuid = (UUID)this.playerSeatMap.inverse().get(index);
            return vehicle.getPassengers().stream().noneMatch(entity -> entity.getUUID().equals(uuid));
        }
        return false;
    }

    public void remove(UUID uuid) {
        this.playerSeatMap.remove(uuid);
    }

    public int getNextAvailableSeat() {
        VehicleEntity vehicle = (VehicleEntity)this.vehicleRef.get();
        if (vehicle != null && !vehicle.level().isClientSide) {
            VehicleProperties properties = vehicle.getProperties();
            List<Seat> seats = properties.getSeats();
            for (int i = 0; i < seats.size(); ++i) {
                if (!this.playerSeatMap.values().contains(i)) {
                    return i;
                }
                UUID uuid = (UUID)this.playerSeatMap.inverse().get(i);
                if (!vehicle.getPassengers().stream().noneMatch(entity -> entity.getUUID().equals(uuid))) continue;
                this.playerSeatMap.remove(uuid);
                return i;
            }
        }
        return -1;
    }

    public int getClosestAvailableSeatToPlayer(Player player) {
        VehicleEntity vehicle = (VehicleEntity)this.vehicleRef.get();
        if (vehicle != null && !vehicle.level().isClientSide) {
            VehicleProperties properties = vehicle.getProperties();
            List<Seat> seats = properties.getSeats();
            if (vehicle.getPassengers().size() == seats.size()) {
                return -1;
            }
            int closestSeatIndex = -1;
            double closestDistance = 0.0;
            for (int i = 0; i < seats.size(); ++i) {
                if (!this.isSeatAvailable(i)) continue;
                Seat seat = seats.get(i);
                Vec3 seatVec = seat.getPosition().add(0.0, (double)(properties.getAxleOffset() + properties.getWheelOffset()), 0.0).scale(properties.getBodyPosition().getScale()).multiply(-1.0, 1.0, 1.0).scale(0.0625);
                seatVec = seatVec.yRot(-vehicle.getModifiedRotationYaw() * ((float)Math.PI / 180));
                seatVec = seatVec.add(vehicle.position());
                double distance = player.distanceToSqr(seatVec.x, seatVec.y - (double)(player.getBbHeight() / 2.0f), seatVec.z);
                if (closestSeatIndex != -1 && !(distance < closestDistance)) continue;
                closestSeatIndex = i;
                closestDistance = distance;
            }
            return closestSeatIndex;
        }
        return -1;
    }

    public CompoundTag write() {
        CompoundTag compound = new CompoundTag();
        ListTag list = new ListTag();
        this.playerSeatMap.forEach((uuid, seatIndex) -> {
            CompoundTag seatTag = new CompoundTag();
            seatTag.putUUID("UUID", uuid);
            seatTag.putInt("SeatIndex", seatIndex.intValue());
            list.add(seatTag);
        });
        compound.put("PlayerSeatMap", (Tag)list);
        return compound;
    }

    public void read(CompoundTag compound) {
        if (compound.contains("PlayerSeatMap", 9)) {
            this.playerSeatMap.clear();
            ListTag list = compound.getList("PlayerSeatMap", 10);
            list.forEach(nbt -> {
                CompoundTag seatTag = (CompoundTag)nbt;
                UUID uuid = seatTag.getUUID("UUID");
                int seatIndex = seatTag.getInt("SeatIndex");
                this.playerSeatMap.put(uuid, seatIndex);
            });
        }
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.playerSeatMap.size());
        this.playerSeatMap.forEach((uuid, seatIndex) -> {
            buffer.writeUUID(uuid);
            buffer.writeVarInt(seatIndex.intValue());
        });
    }

    public void read(FriendlyByteBuf buffer) {
        this.playerSeatMap.clear();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; ++i) {
            UUID uuid = buffer.readUUID();
            int seatIndex = buffer.readVarInt();
            this.playerSeatMap.put(uuid, seatIndex);
        }
    }
}

