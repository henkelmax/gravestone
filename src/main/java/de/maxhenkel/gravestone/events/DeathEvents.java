package de.maxhenkel.gravestone.events;

import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.GraveProcessor;
import de.maxhenkel.gravestone.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;

public class DeathEvents {

    public DeathEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerCloneLast(PlayerEvent.Clone event) {
        if (!Main.SERVER_CONFIG.giveDeathNotes.get()) {
            return;
        }

        if (event.isCanceled()) {
            return;
        }

        if (!event.isWasDeath()) {
            return;
        }

        if (keepInventory(event.getPlayer())) {
            return;
        }

        for (ItemStack stack : event.getOriginal().inventory.mainInventory) {
            if (DeathInfo.isDeathInfoItem(stack)) {
                event.getPlayer().inventory.addItemStackToInventory(stack);
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeath(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        if (!(event.getEntity() instanceof PlayerEntity) && !Main.SERVER_CONFIG.livingGraves.get()) {
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
                    String modname = new TranslationTextComponent("message.gravestone.name").getString();
                    String message = new TranslationTextComponent("message.gravestone.create_grave_failed").getString();

                    ServerPlayerEntity player = (ServerPlayerEntity) entity;

                    player.sendMessage(new StringTextComponent("[" + modname + "] " + message), player.getUniqueID());
                }
            }
            if (Main.SERVER_CONFIG.giveDeathNotes.get()) {
                graveProcessor.givePlayerNote();
            }
        } catch (Exception e) {
            Main.LOGGER.warn("Failed to process death of '{}'", event.getEntity().getName().getString());
            e.printStackTrace();
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeath(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }

        if (!Main.SERVER_CONFIG.giveDeathNotes.get()) {
            return;
        }

        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        if (event.getEntity().getEntityWorld().isRemote) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntity();

        if (!keepInventory(player)) {
            return;
        }

        /*
         * Give the player a note without items when he dies with keepInventory true
         */
        try {
            DeathInfo info = new DeathInfo(player.func_233580_cy_(), player.world.func_234923_W_().func_240901_a_().toString(), new ArrayList<>(), player.getName().getString(), System.currentTimeMillis(), player.getUniqueID());
            ItemStack stack = new ItemStack(Main.DEATHINFO);

            info.addToItemStack(stack);
            player.inventory.addItemStackToInventory(stack);
        } catch (Exception e) {
            Main.LOGGER.warn("Failed to give player '{}' death note", player.getName().getString());
        }
    }

    public static boolean keepInventory(PlayerEntity player) {
        try {
            return player.getEntityWorld().getWorldInfo().getGameRulesInstance().getBoolean(GameRules.KEEP_INVENTORY);
        } catch (Exception e) {
            return false;
        }
    }

}
