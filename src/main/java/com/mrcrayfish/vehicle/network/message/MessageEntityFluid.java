package com.mrcrayfish.vehicle.network.message;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.mrcrayfish.vehicle.client.network.ClientPlayHandler;
import net.neoforged.neoforge.fluids.FluidStack;


/**
 * Author: MrCrayfish
 */
public class MessageEntityFluid implements IMessage<MessageEntityFluid>
{
    public static final CustomPacketPayload.Type<MessageEntityFluid> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "entity_fluid"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageEntityFluid> STREAM_CODEC = StreamCodec.ofMember((msg, buf) -> msg.encode(msg, buf), buf -> new MessageEntityFluid().decode(buf));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private int entityId;
    private FluidStack stack;

    public MessageEntityFluid() {}

    public MessageEntityFluid(int entityId, FluidStack stack)
    {
        this.entityId = entityId;
        this.stack = stack;
    }

    @Override
    public void encode(MessageEntityFluid message, RegistryFriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
        FluidStack.STREAM_CODEC.encode(buffer, message.stack);
    }

    @Override
    public MessageEntityFluid decode(RegistryFriendlyByteBuf buffer)
    {
        return new MessageEntityFluid(buffer.readInt(), FluidStack.STREAM_CODEC.decode(buffer));
    }

    @Override
    public void handle(MessageEntityFluid message, IPayloadContext context)
    {
        IMessage.enqueueTask(context, () -> ClientPlayHandler.handleEntityFluid(message));
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public FluidStack getStack()
    {
        return this.stack;
    }
}
