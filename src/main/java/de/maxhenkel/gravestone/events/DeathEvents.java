package de.maxhenkel.gravestone.events;

import java.util.ArrayList;
import java.util.Collection;

import de.maxhenkel.gravestone.*;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
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

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        if (!(event.getEntity() instanceof PlayerEntity) && !Config.livingGraves) {
            return;
        }

        if (event.getEntity().getEntityWorld().isRemote) {
            return;
        }

        try {
            LivingEntity entity = (LivingEntity) event.getEntity();
            GraveProcessor graveProcessor = new GraveProcessor(entity);

            Collection<ItemEntity> drops = event.getDrops();

            if (graveProcessor.placeGraveStone(drops)) {
                drops.clear();
            } else {
                if (entity instanceof ServerPlayerEntity) {
                    String modname = new TranslationTextComponent("message.name").getFormattedText();
                    String message = new TranslationTextComponent("message.create_grave_failed").getFormattedText();

                    ServerPlayerEntity player = (ServerPlayerEntity) entity;

                    player.sendMessage(new StringTextComponent("[" + modname + "] " + message));
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

        if (!Config.giveDeathNotes) {
            return;
        }

        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        if (event.getEntity().getEntityWorld().isRemote) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntity();

        if (!Tools.keepInventory(player)) {
            return;
        }

        /*
         * Give the player a note without items when he dies with keepInventory true
         */
        try {
            DeathInfo info = new DeathInfo(player.getPosition(), DimensionType.getKey(player.dimension).toString(), new ArrayList<>(), player.getName().getUnformattedComponentText(), System.currentTimeMillis(), player.getUniqueID());
            ItemStack stack = new ItemStack(Main.DEATHINFO);

            info.addToItemStack(stack);
            player.inventory.addItemStackToInventory(stack);
        } catch (Exception e) {
            Log.w("Failed to give player '" + player.getName().getUnformattedComponentText() + "' death note");
        }

    }
}
