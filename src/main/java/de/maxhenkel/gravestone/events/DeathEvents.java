package de.maxhenkel.gravestone.events;

import java.util.ArrayList;
import java.util.Collection;
import de.maxhenkel.gravestone.*;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DeathEvents {

    public DeathEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerCloneLast(PlayerEvent.Clone event) {
        if (!Config.giveDeathNotes) {
            return;
        }

        if (event.isCanceled()) {
            return;
        }

        if (!event.isWasDeath()) {
            return;
        }

        if (Tools.keepInventory(event.getEntityPlayer())) {
            return;
        }

        for (ItemStack stack : event.getOriginal().inventory.mainInventory) {
            if (DeathInfo.isDeathInfoItem(stack)) {
                event.getEntityPlayer().inventory.addItemStackToInventory(stack);
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeath(LivingDropsEvent event) {
        if (event.isCanceled()) {
            return;
        }

        if (!(event.getEntity() instanceof EntityLivingBase)) {
            return;
        }

        if (!(event.getEntity() instanceof EntityPlayer) && !Config.livingGraves) {
            return;
        }

        if (event.getEntity().getEntityWorld().isRemote) {
            return;
        }

        try {
            EntityLivingBase entity = (EntityLivingBase) event.getEntity();
            GraveProcessor graveProcessor = new GraveProcessor(entity);

            Collection<EntityItem> drops = event.getDrops();

            if (graveProcessor.placeGraveStone(drops)) {
                drops.clear();
            } else {
                if (entity instanceof EntityPlayerMP) {
                    String modname = new TextComponentTranslation("message.name").getFormattedText();
                    String message = new TextComponentTranslation("message.create_grave_failed").getFormattedText();

                    EntityPlayerMP player = (EntityPlayerMP) entity;

                    player.sendMessage(new TextComponentString("[" + modname + "] " + message));
                }
            }
            if (Config.giveDeathNotes) {
                graveProcessor.givePlayerNote();
            }
        } catch (Exception e) {
            Log.w("Failed to process death of '" + event.getEntity().getName().getUnformattedComponentText() + "'");
            e.printStackTrace();
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeath(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }

        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        if (event.getEntity().getEntityWorld().isRemote) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntity();

        if (!Tools.keepInventory(player)) {
            return;
        }

        /*
         * Give the player a note without items when he dies with keepInventory true
         */
        try {
            DeathInfo info = new DeathInfo(player.getPosition(), player.dimension.getRegistryName().toString(), new ArrayList<>(), player.getName().getUnformattedComponentText(), System.currentTimeMillis(), player.getUniqueID());
            ItemStack stack = new ItemStack(Main.deathInfo);

            info.addToItemStack(stack);
            player.inventory.addItemStackToInventory(stack);
        } catch (Exception e) {
            Log.w("Failed to give player '" + player.getName().getUnformattedComponentText() + "' death note");
        }

    }
}
