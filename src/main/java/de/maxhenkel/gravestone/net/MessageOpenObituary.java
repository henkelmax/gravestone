package de.maxhenkel.gravestone.net;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.gravestone.gui.ObituaryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class MessageOpenObituary implements Message {

    private Death death;

    public MessageOpenObituary() {

    }

    public MessageOpenObituary(Death death) {
        this.death = death;
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.CLIENT;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void executeClientSide(CustomPayloadEvent.Context context) {
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

}
