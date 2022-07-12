package de.maxhenkel.gravestone.util;

import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.ModBlocks;
import de.maxhenkel.gravestone.ModItems;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GraveUtils {
	
	public static boolean canBreakGrave(EntityPlayer player, TileEntityGraveStone tileentity) {
		if (!Config.instance().onlyPlayersCanBreak) {
			return true;
		}

		String uuid = tileentity.getPlayerUUID();

		if (uuid == null) {
			return true;
		}

		if (player.getUniqueID().toString().equals(uuid)) {
			return true;
		}

		if (!(player instanceof EntityPlayerMP)) {
			return false;
		}

		EntityPlayerMP p = (EntityPlayerMP) player;
		boolean isOp = p.canCommandSenderUseCommand(p.mcServer.getOpPermissionLevel(), "op");

		return isOp;
	}
	
	public static void removeDeathNote(EntityPlayer player, int x, int y, int z) {
		if (!Config.instance().removeDeathNote) {
			return;
		}

		World world = player.worldObj;

		if (world.isRemote) {
			return;
		}

		if (!world.getBlock(x, y, z).equals(ModBlocks.GRAVESTONE)) {
			return;
		}

		InventoryPlayer inv = player.inventory;

		BlockPos pos = new BlockPos(x, y, z);
		int dim = player.dimension;

		for (int i=0; i<inv.mainInventory.length; i++) {
			ItemStack stack = inv.mainInventory[i];
			if (stack != null && stack.getItem().equals(ModItems.DEATH_INFO)) {
				if (stack.hasTagCompound() && stack.getTagCompound().hasKey(DeathInfo.KEY_INFO)) {
					DeathInfo info = DeathInfo.fromNBT(stack.getTagCompound().getCompoundTag(DeathInfo.KEY_INFO));
					if (info != null && dim == info.getDimension() && pos.equals(info.getDeathLocation())) {
						inv.setInventorySlotContents(i, null);
					}
				}
			}
		}

		for (int i=0; i<inv.armorInventory.length; i++) {
			ItemStack stack = inv.mainInventory[i];
			if (stack != null && stack.getItem().equals(ModItems.DEATH_INFO)) {
				inv.setInventorySlotContents(i, null);
			}
		}
	}
}
