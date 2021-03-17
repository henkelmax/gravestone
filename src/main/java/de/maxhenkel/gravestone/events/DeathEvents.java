package de.maxhenkel.gravestone.events;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.death.PlayerDeathEvent;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.items.ObituaryItem;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathEvents {

    public DeathEvents() {
        de.maxhenkel.corelib.death.DeathEvents.register();
    }

    @SubscribeEvent()
    public void playerDeath(PlayerDeathEvent event) {
        event.storeDeath();

        Death death = event.getDeath();
        PlayerEntity player = event.getPlayer();
        World world = player.level;

        if (keepInventory(player)) {
            return;
        }

        BlockPos graveStoneLocation = GraveUtils.getGraveStoneLocation(world, death.getBlockPos());

        if (Main.SERVER_CONFIG.giveObituaries.get()) {
            player.inventory.add(Main.OBITUARY.toStack(death));
        }

        if (graveStoneLocation == null) {
            Main.LOGGER.info("Grave of '{}' can't be placed (No space)", death.getPlayerName());
            Main.LOGGER.info("The death ID of '{}' is {}", death.getPlayerName(), death.getId().toString());
            return;
        }

        world.setBlockAndUpdate(graveStoneLocation, Main.GRAVESTONE.defaultBlockState().setValue(GraveStoneBlock.FACING, player.getDirection().getOpposite()));

        if (GraveUtils.isReplaceable(world, graveStoneLocation.below())) {
            world.setBlockAndUpdate(graveStoneLocation.below(), Blocks.DIRT.defaultBlockState());
        }

        TileEntity tileentity = world.getBlockEntity(graveStoneLocation);

        if (!(tileentity instanceof GraveStoneTileEntity)) {
            Main.LOGGER.info("Grave of '{}' can't be filled with loot (No tileentity found)", death.getPlayerName());
            Main.LOGGER.info("The death ID of '{}' is {}", death.getPlayerName(), death.getId().toString());
            return;
        }

        GraveStoneTileEntity gravestone = (GraveStoneTileEntity) tileentity;

        gravestone.setDeath(death);

        event.removeDrops();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerCloneLast(PlayerEvent.Clone event) {
        if (!Main.SERVER_CONFIG.giveObituaries.get()) {
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

        for (ItemStack stack : event.getOriginal().inventory.items) {
            if (stack.getItem() instanceof ObituaryItem) {
                event.getPlayer().inventory.add(stack);
            }
        }
    }

    public static boolean keepInventory(PlayerEntity player) {
        try {
            return player.getCommandSenderWorld().getLevelData().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        } catch (Exception e) {
            return false;
        }
    }

}
