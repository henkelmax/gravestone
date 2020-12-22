package de.maxhenkel.gravestone.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.ModBlocks;
import de.maxhenkel.gravestone.ModItems;
import de.maxhenkel.gravestone.tileentity.TileEntityGraveStone;
import de.maxhenkel.gravestone.util.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class BlockEvents {

	private boolean removeDeathNote;
	private boolean onlyOwnersCanBreak;

	public BlockEvents() {
		this.removeDeathNote = Config.instance().removeDeathNote;
		this.onlyOwnersCanBreak = Config.instance().onlyPlayersCanBreak;
	}

	@SubscribeEvent
	public void onBlockPlace(BlockEvent.PlaceEvent event) {
		if (event.isCanceled()) {
			return;
		}
		
		World world = event.world;

		if (world.isRemote) {
			return;
		}

		if (!event.placedBlock.equals(ModBlocks.GRAVESTONE)) {
			return;
		}

		TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);

		if (!(te instanceof TileEntityGraveStone)) {
			return;
		}

		TileEntityGraveStone graveTileEntity = (TileEntityGraveStone) te;

		ItemStack stack = event.itemInHand;

		if (stack == null || !stack.getItem().equals(ModItems.GRAVESTONE)) {
			return;
		}

		if (!stack.hasDisplayName()) {
			return;
		}

		String name = stack.getDisplayName();

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

		removeDeathNote(event);
		checkBreak(event);
	}

	public void removeDeathNote(BlockEvent.BreakEvent event) {
		if (!removeDeathNote) {
			return;
		}

		World world = event.world;

		if (world.isRemote) {
			return;
		}

		if (!event.block.equals(ModBlocks.GRAVESTONE)) {
			return;
		}

		EntityPlayer player = event.getPlayer();

		InventoryPlayer inv = player.inventory;

		BlockPos pos = new BlockPos(event.x, event.y, event.z);
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

	public void checkBreak(BlockEvent.BreakEvent event) {
		if (!onlyOwnersCanBreak) {
			return;
		}

		World world = event.world;

		if (world.isRemote) {
			return;
		}

		if (!event.block.equals(ModBlocks.GRAVESTONE)) {
			return;
		}

		EntityPlayer player = event.getPlayer();

		TileEntity te = world.getTileEntity(event.x, event.y, event.z);

		if (te == null || !(te instanceof TileEntityGraveStone)) {
			return;
		}

		TileEntityGraveStone tileentity = (TileEntityGraveStone) te;

		String uuid = tileentity.getPlayerUUID();

		if (uuid == null) {
			return;
		}

		if (player.getUniqueID().toString().equals(uuid)) {
			return;
		}

		if (!(player instanceof EntityPlayerMP)) {
			event.setCanceled(true);
		}

		EntityPlayerMP p = (EntityPlayerMP) player;
		boolean isOp = p.canCommandSenderUseCommand(p.mcServer.getOpPermissionLevel(), "op");

		if (!isOp) {
			event.setCanceled(true);
		}
	}

}
