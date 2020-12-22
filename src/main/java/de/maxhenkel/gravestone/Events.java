package de.maxhenkel.gravestone;

import java.io.File;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import de.maxhenkel.gravestone.UpdateChecker.IUpdateCheckResult;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Events {

	public static final String POS_SUFFIX = "deathpos";

	private boolean checkUpdates;
	private DeathLocationManager deathLocationManager;
	private boolean updateShown;

	public Events() {
		this.deathLocationManager = new DeathLocationManager();
		this.checkUpdates = false;
		this.updateShown = false;

		try {
			Main.getInstance().getConfig().load();
			this.checkUpdates = Main.getInstance().getConfig().get(Main.MODID, "check_updates", true).getBoolean();
			Main.getInstance().getConfig().save();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SubscribeEvent
	public void playerLoad(PlayerEvent.LoadFromFile event) {
		if (event.isCanceled()) {
			return;
		}

		if (event.entityPlayer.worldObj.isRemote) {
			return;
		}

		File userFile = event.getPlayerFile(POS_SUFFIX);

		deathLocationManager.loadPlayer(event.entityPlayer, userFile);
	}

	@SubscribeEvent
	public void playerSave(PlayerEvent.SaveToFile event) {
		if (event.isCanceled()) {
			return;
		}

		if (event.entityPlayer.worldObj.isRemote) {
			return;
		}

		File userFile = event.getPlayerFile(POS_SUFFIX);

		deathLocationManager.savePlayer(event.entityPlayer, userFile);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerDeath(LivingDropsEvent event) {
		if (event.isCanceled()) {
			return;
		}

		if (!(event.entity instanceof EntityPlayer)) {
			return;
		}

		if (event.entity.worldObj.isRemote) {
			return;
		}

		try {
			event.setCanceled(true);
			List<EntityItem> drops = event.drops;

			EntityPlayer player = (EntityPlayer) event.entity;
			DeathPosition deathPos = new DeathPosition(player);
			deathPos.placeGraveStone(drops);

			deathLocationManager.putPos(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void playerJoin(EntityJoinWorldEvent event) {
		if (event.isCanceled()) {
			return;
		}

		if (!(event.entity instanceof EntityPlayer)) {
			return;
		}

		final EntityPlayer player = (EntityPlayer) event.entity;

		if (!player.worldObj.isRemote) {
			return;
		}

		if (player.isDead) {
			return;
		}

		if (!checkUpdates) {
			return;
		}
		
		if (updateShown) {
			return;
		}

		UpdateChecker checker = new UpdateChecker(new IUpdateCheckResult() {
			@Override
			public void onResult(boolean isAvailable) {
				if (isAvailable) {

					String msg = "[" + new ChatComponentTranslation("message.name", new Object[0]).getFormattedText()
							+ "] " + new ChatComponentTranslation("message.update", new Object[0]).getFormattedText()
							+ " ";

					ClickEvent openUrl = new ClickEvent(Action.OPEN_URL,
							"http://minecraft.curseforge.com/projects/gravestone-mod");
					ChatStyle style = new ChatStyle();

					style.setChatClickEvent(openUrl);
					style.setUnderlined(true);
					style.setColor(EnumChatFormatting.GREEN);
					style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
							new ChatComponentTranslation("message.update.hover", new Object[0]).getFormattedText())));
					ChatComponentText comp = new ChatComponentText("[Download]");
					comp.setChatStyle(style);
					player.addChatMessage(new ChatComponentText(msg).appendSibling(comp));
				}
			}
		}, Main.VERSION_NUMBER, "http://maxhenkel.de/update/gravestone_1.8.txt");
		checker.start();
		updateShown=true;
	}

	public DeathLocationManager getDeathLocationManager() {
		return deathLocationManager;
	}

}
