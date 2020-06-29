package de.maxhenkel.gravestone.events;

import java.util.UUID;

import de.maxhenkel.gravestone.*;
import de.maxhenkel.gravestone.entity.GhostPlayerEntity;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class BlockEvents {

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled()) {
            return;
        }

        IWorld world = event.getWorld();

        if (world.isRemote()) {
            return;
        }

        if (!event.getState().getBlock().equals(Main.GRAVESTONE)) {
            return;
        }

        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntity();

        TileEntity te = event.getWorld().getTileEntity(event.getPos());

        if (!(te instanceof GraveStoneTileEntity)) {
            return;
        }

        GraveStoneTileEntity graveTileEntity = (GraveStoneTileEntity) te;

        ItemStack stack = player.getHeldItemMainhand();

        if (stack == null || !stack.getItem().equals(Main.GRAVESTONE_ITEM)) {
            stack = player.getHeldItemOffhand();
            if (stack == null || !stack.getItem().equals(Main.GRAVESTONE_ITEM)) {
                return;
            }
        }

        if (!stack.hasDisplayName()) {
            return;
        }

        String name = stack.getDisplayName().getString();

        if (name == null) {
            return;
        }

        graveTileEntity.setPlayerName(name);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) {
            return;
        }

        IWorld world = event.getWorld();

        if (world.isRemote()) {
            return;
        }

        if (!event.getState().getBlock().equals(Main.GRAVESTONE)) {
            return;
        }

        if (!checkBreak(event)) {
            return;
        }

        removeDeathNote(event);
        spawnGhost(event);
    }

    private void spawnGhost(BreakEvent event) {
        if (!Config.spawnGhost) {
            return;
        }
        IWorld iWorld = event.getWorld();
        if (!(iWorld instanceof World)) {
            return;
        }
        World world = (World) iWorld;

        if (!world.isAirBlock(event.getPos().up())) {
            return;
        }

        TileEntity te = world.getTileEntity(event.getPos());

        if (!(te instanceof GraveStoneTileEntity)) {
            return;
        }

        GraveStoneTileEntity tileentity = (GraveStoneTileEntity) te;

        UUID uuid = null;

        try {
            uuid = UUID.fromString(tileentity.getPlayerUUID());
        } catch (Exception e) {
        }

        if (uuid == null) {
            return;
        }

        GhostPlayerEntity ghost = new GhostPlayerEntity(world, uuid, tileentity.getPlayerName());
        ghost.setPosition(event.getPos().getX() + 0.5, event.getPos().getY() + 0.1, event.getPos().getZ() + 0.5);
        world.addEntity(ghost);
    }

    private void removeDeathNote(BlockEvent.BreakEvent event) {
        if (!Config.removeDeathNote) {
            return;
        }

        PlayerEntity player = event.getPlayer();

        PlayerInventory inv = player.inventory;

        BlockPos pos = event.getPos();
        String dim = player.world.func_230315_m_().toString();

        for (ItemStack stack : inv.mainInventory) {
            if (stack != null && stack.getItem().equals(Main.DEATHINFO)) {
                if (stack.hasTag() && stack.getTag().contains(DeathInfo.KEY_INFO)) {
                    DeathInfo info = DeathInfo.fromNBT(stack.getTag().getCompound(DeathInfo.KEY_INFO));
                    if (info != null && dim.equals(info.getDimension()) && pos.equals(info.getDeathLocation())) {
                        inv.deleteStack(stack);
                    }
                }
            }
        }

        for (ItemStack stack : inv.armorInventory) {
            if (stack != null && stack.getItem().equals(Main.DEATHINFO)) {
                inv.deleteStack(stack);
            }
        }

        for (ItemStack stack : inv.offHandInventory) {
            if (stack != null && stack.getItem().equals(Main.DEATHINFO)) {
                inv.deleteStack(stack);
            }
        }
    }

    public boolean checkBreak(BlockEvent.BreakEvent event) {
        if (!Config.onlyOwnersCanBreak) {
            return true;
        }

        IWorld world = event.getWorld();

        PlayerEntity player = event.getPlayer();

        TileEntity te = world.getTileEntity(event.getPos());

        if (te == null || !(te instanceof GraveStoneTileEntity)) {
            return true;
        }

        GraveStoneTileEntity tileentity = (GraveStoneTileEntity) te;

        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity p = (ServerPlayerEntity) player;

            boolean isOp = p.hasPermissionLevel(p.server.getOpPermissionLevel());

            if (isOp) {
                return true;
            }
        }

        String uuid = tileentity.getPlayerUUID();

        if (uuid == null) {
            return true;
        }

        if (player.getUniqueID().toString().equals(uuid)) {
            return true;
        }

        event.setCanceled(true);
        return false;
    }

}
