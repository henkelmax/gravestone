package de.maxhenkel.gravestone.net;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.gravestone.gui.ObituaryScreen;
import net.minecraft.client.Minecraft;

public class ClientNetworking {

    public static void openObituary(Death death) {
        Minecraft.getInstance().setScreen(new ObituaryScreen(death));
    }

}
