package de.maxhenkel.gravestone.net;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.gravestone.GravestoneMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageOpenObituary implements Message<MessageOpenObituary> {

    public static final CustomPacketPayload.Type<MessageOpenObituary> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GravestoneMod.MODID, "open_obituary"));

    private Death death;

    public MessageOpenObituary() {

    }

    public MessageOpenObituary(Death death) {
        this.death = death;
    }

    @Override
    public PacketFlow getExecutingSide() {
        return PacketFlow.CLIENTBOUND;
    }

    @Override
    public void executeClientSide(IPayloadContext context) {
        ClientNetworking.openObituary(death);
    }

    @Override
    public MessageOpenObituary fromBytes(RegistryFriendlyByteBuf buf) {
        death = Death.read(buf.registryAccess(), buf.readNbt());
        return this;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(death.write(buf.registryAccess()));
    }

    @Override
    public Type<MessageOpenObituary> type() {
        return TYPE;
    }

}
