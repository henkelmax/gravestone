package de.maxhenkel.gravestone.events;

import de.maxhenkel.gravestone.Main;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CreativeTabEvents {

    @SubscribeEvent
    public static void onCreativeModeTabBuildContents(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(new ItemStack(Main.GRAVESTONE.get()));
        }
    }

}
