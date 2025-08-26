package de.maxhenkel.gravestone.events;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.death.PlayerDeathEvent;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.items.ObituaryItem;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class DeathEvents {

    public DeathEvents() {
        de.maxhenkel.corelib.death.DeathEvents.register();
    }

    @SubscribeEvent
    public void playerDeath(PlayerDeathEvent event) {
        event.storeDeath();

        Death death = event.getDeath();
        Player player = event.getPlayer();
        Level world = player.level();

        if (keepInventory(player)) {
            return;
        }

        GravestoneMod.LOGGER.info("The death ID of player {} is {}", death.getPlayerName(), death.getId());

        BlockPos graveStoneLocation = GraveUtils.getGraveStoneLocation(world, death.getBlockPos());

        if (Main.SERVER_CONFIG.giveObituaries.get()) {
            player.getInventory().add(Main.OBITUARY.get().toStack(death));
        }

        if (graveStoneLocation == null) {
            Main.LOGGER.info("Grave of '{}' can't be placed (No space)", death.getPlayerName());
            Main.LOGGER.info("The death ID of '{}' is {}", death.getPlayerName(), death.getId().toString());
            return;
        }

        world.setBlockAndUpdate(graveStoneLocation, Main.GRAVESTONE.get().defaultBlockState().setValue(GraveStoneBlock.FACING, player.getDirection().getOpposite()));

        if (GraveUtils.isReplaceable(world, graveStoneLocation.below())) {
            world.setBlockAndUpdate(graveStoneLocation.below(), Blocks.DIRT.defaultBlockState());
        }

        BlockEntity tileentity = world.getBlockEntity(graveStoneLocation);

        if (!(tileentity instanceof GraveStoneTileEntity gravestone)) {
            Main.LOGGER.info("Grave of '{}' can't be filled with loot (No tileentity found)", death.getPlayerName());
            Main.LOGGER.info("The death ID of '{}' is {}", death.getPlayerName(), death.getId().toString());
            return;
        }

        gravestone.setDeath(death);
        event.removeDrops();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerCloneLast(PlayerEvent.Clone event) {
        if (!Main.SERVER_CONFIG.giveObituaries.get()) {
            return;
        }

        if (!event.isWasDeath()) {
            return;
        }

        if (keepInventory(event.getEntity())) {
            return;
        }

        for (int i = 0; i < event.getOriginal().getInventory().getContainerSize(); i++) {
            ItemStack stack = event.getOriginal().getInventory().getItem(i);
            if (stack.getItem() instanceof ObituaryItem) {
                event.getEntity().getInventory().add(stack);
            }
        }
    }

    public static boolean keepInventory(Player player) {
        try {
            MinecraftServer server = player.getCommandSenderWorld().getServer();
            if (server == null) {
                return false;
            }
            return server.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        } catch (Exception e) {
            return false;
        }
    }

}
