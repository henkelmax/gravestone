package de.maxhenkel.gravestone.net;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.gravestone.gui.ObituaryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

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
    public void executeClientSide(NetworkEvent.Context context) {
        Minecraft.getInstance().displayGuiScreen(new ObituaryScreen(death));
    }

    @Override
    public MessageOpenObituary fromBytes(PacketBuffer buf) {
        death = Death.fromNBT(buf.readCompoundTag());
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeCompoundTag(death.toNBT());
    }

}
