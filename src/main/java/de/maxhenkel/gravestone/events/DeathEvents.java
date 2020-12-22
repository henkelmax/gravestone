package de.maxhenkel.gravestone.events;

import java.util.List;
import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.Log;
import de.maxhenkel.gravestone.DeathInfo.ItemInfo;
import de.maxhenkel.gravestone.GraveProcessor;
import de.maxhenkel.gravestone.ModItems;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathEvents {

	private boolean livingGraves;
	private boolean givePlayerNote;
	
	public DeathEvents() {
		this.livingGraves=Config.instance().livingGraves;
		this.givePlayerNote=Config.instance().giveDeathNotes;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerCloneLast(PlayerEvent.Clone event) {
		if (!givePlayerNote) {
			return;
		}

		if (event.isCanceled()) {
			return;
		}

		if (!event.wasDeath) {
			return;
		}

		if (Tools.keepInventory(event.entityPlayer)) {
			return;
		}

		for (ItemStack stack : event.original.inventory.mainInventory) {
			if (DeathInfo.isDeathInfoItem(stack)) {
				event.entityPlayer.inventory.addItemStackToInventory(stack);
			}
		}

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerDeath(LivingDropsEvent event) {
		if (event.isCanceled()) {
			return;
		}

		if (!(event.entity instanceof EntityLivingBase)) {
			return;
		}

		if (!(event.entity instanceof EntityPlayer) && !livingGraves) {
			return;
		}

		if (event.entity.worldObj.isRemote) {
			return;
		}

		try {

			List<EntityItem> drops = event.drops;

			EntityLivingBase entity = (EntityLivingBase) event.entity;
			GraveProcessor graveProcessor = new GraveProcessor(entity);
			if (graveProcessor.placeGraveStone(drops)) {
				event.setCanceled(true);
			}else{
				if(entity instanceof EntityPlayerMP){
					((EntityPlayerMP)entity).addChatMessage(new ChatComponentText("[" + Tools.translate("message.name") + "] " +Tools.translate("message.create_grave_failed")));
				}
			}
			if (givePlayerNote) {
				graveProcessor.givePlayerNote();
			}
		} catch (Exception e) {
			Log.w("Failed to process death of '" +event.entity.getName() +"'");
		}

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerDeath(LivingDeathEvent event) {
		if (event.isCanceled()) {
			return;
		}

		if (!(event.entity instanceof EntityPlayer)) {
			return;
		}

		if (event.entity.worldObj.isRemote) {
			return;
		}

		EntityPlayer player = (EntityPlayer) event.entity;

		if (!Tools.keepInventory(player)) {
			return;
		}

		/*
		 * Give the player a note without items when he dies with keepInventory true
		 */
		
		try {
			givePlayerNote(player);
		} catch (Exception e) {
			Log.w("Failed to give player '" +player.getDisplayNameString() +"' death note");
		}

	}
	
	public static void givePlayerNote(EntityPlayer player){
		DeathInfo info=new DeathInfo(player.getPosition(), player.dimension, new ItemInfo[0], player.getDisplayNameString(), System.currentTimeMillis(), player.getUniqueID());
		ItemStack stack=new ItemStack(ModItems.DEATH_INFO);
		
		info.addToItemStack(stack);
		player.inventory.addItemStackToInventory(stack);
	}
}
