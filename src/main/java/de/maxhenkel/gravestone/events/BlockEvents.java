package de.maxhenkel.gravestone.events;

import java.util.UUID;

import de.maxhenkel.gravestone.*;
import de.maxhenkel.gravestone.entity.EntityGhostPlayer;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
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

    private boolean removeDeathNote;
    private boolean onlyOwnersCanBreak;
    private boolean spawnGhost;

    public BlockEvents() {
        this.removeDeathNote = Config.removeDeathNote;
        this.onlyOwnersCanBreak = Config.onlyPlayersCanBreak;
        this.spawnGhost = Config.spawnGhost;
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.isCanceled()) {
            return;
        }

        IWorld world = event.getWorld();

        if (world.isRemote()) {
            return;
        }

        if (!event.getState().getBlock().equals(ModBlocks.GRAVESTONE)) {
            return;
        }

        TileEntity te = event.getWorld().getTileEntity(event.getPos());

        if (!(te instanceof TileEntityGraveStone)) {
            return;
        }

        TileEntityGraveStone graveTileEntity = (TileEntityGraveStone) te;

        ItemStack stack = event.getPlayer().getHeldItem(event.getHand());

        if (stack == null || !stack.getItem().equals(Item.getItemFromBlock(ModBlocks.GRAVESTONE))) {
            return;
        }

        if (!stack.hasDisplayName()) {
            return;
        }

        String name = stack.getDisplayName().getUnformattedComponentText();

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

        if (!event.getState().getBlock().equals(ModBlocks.GRAVESTONE)) {
            return;
        }

        if (!checkBreak(event)) {
            return;
        }

        removeDeathNote(event);
        spawnGhost(event);
    }

    private void spawnGhost(BreakEvent event) {
        if (!spawnGhost) {
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

        if (te == null || !(te instanceof TileEntityGraveStone)) {
            return;
        }

        TileEntityGraveStone tileentity = (TileEntityGraveStone) te;

        UUID uuid = new UUID(0, 0);

        try {
            uuid = UUID.fromString(tileentity.getPlayerUUID());
        } catch (Exception e) {
        }

        EntityGhostPlayer z = new EntityGhostPlayer(world, uuid, tileentity.getPlayerName());
        z.setPosition(event.getPos().getX() + 0.5, event.getPos().getY() + 0.1, event.getPos().getZ() + 0.5);
        world.spawnEntity(z);
    }

    private void removeDeathNote(BlockEvent.BreakEvent event) {
        if (!removeDeathNote) {
            return;
        }

        EntityPlayer player = event.getPlayer();

        InventoryPlayer inv = player.inventory;

        BlockPos pos = event.getPos();
        int dim = player.dimension;

        for (ItemStack stack : inv.mainInventory) {
            if (stack != null && stack.getItem().equals(ModItems.DEATH_INFO)) {
                if (stack.hasTag() && stack.getTag().hasKey(DeathInfo.KEY_INFO)) {
                    DeathInfo info = DeathInfo.fromNBT(stack.getTag().getCompound(DeathInfo.KEY_INFO));
                    if (info != null && dim == info.getDimension() && pos.equals(info.getDeathLocation())) {
                        inv.deleteStack(stack);
                    }
                }
            }
        }

        for (ItemStack stack : inv.armorInventory) {
            if (stack != null && stack.getItem().equals(ModItems.DEATH_INFO)) {
                inv.deleteStack(stack);
            }
        }

        for (ItemStack stack : inv.offHandInventory) {
            if (stack != null && stack.getItem().equals(ModItems.DEATH_INFO)) {
                inv.deleteStack(stack);
            }
        }
    }

    public boolean checkBreak(BlockEvent.BreakEvent event) {
        if (!onlyOwnersCanBreak) {
            return true;
        }

        IWorld world = event.getWorld();

        EntityPlayer player = event.getPlayer();

        TileEntity te = world.getTileEntity(event.getPos());

        if (te == null || !(te instanceof TileEntityGraveStone)) {
            return true;
        }

        TileEntityGraveStone tileentity = (TileEntityGraveStone) te;

        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP p = (EntityPlayerMP) player;

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
