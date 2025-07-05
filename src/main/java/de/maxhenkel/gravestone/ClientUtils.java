package de.maxhenkel.gravestone;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientUtils {

    public static void sendMessage(Component message) {
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }

}
