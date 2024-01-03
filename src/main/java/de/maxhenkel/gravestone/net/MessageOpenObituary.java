package de.maxhenkel.gravestone.net;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.gui.ObituaryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MessageOpenObituary implements Message {

    public static ResourceLocation ID = new ResourceLocation(Main.MODID, "open_obituary");

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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void executeClientSide(PlayPayloadContext context) {
        Minecraft.getInstance().setScreen(new ObituaryScreen(death));
    }

    @Override
    public MessageOpenObituary fromBytes(FriendlyByteBuf buf) {
        death = Death.fromNBT(buf.readNbt());
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(death.toNBT());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

}
