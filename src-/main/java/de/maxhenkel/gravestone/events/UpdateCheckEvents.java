package de.maxhenkel.gravestone.events;

import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.util.UpdateChecker;
import de.maxhenkel.gravestone.util.UpdateChecker.IUpdateCheckResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UpdateCheckEvents {

	private boolean checkUpdates;
	private boolean updateShown;

	public UpdateCheckEvents() {
		this.checkUpdates = Config.checkUpdates;
		this.updateShown = false;
	}

	@SubscribeEvent
	public void playerJoin(EntityJoinWorldEvent event) {
		if (event.isCanceled()) {
			return;
		}

		if (!(event.getEntity() instanceof EntityPlayer)) {
			return;
		}

		if (!event.getWorld().isRemote) {
			return;
		}

		final EntityPlayer player = (EntityPlayer) event.getEntity();

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
			public void onResult(boolean isAvailable, String updateURL) {
				if (isAvailable) {
					showUpdateMessage(player, updateURL);
				}
			}
		}, Main.VERSION_NUMBER, Main.UPDATE_CHECK_URL);
		checker.start();
		updateShown = true;
	}

	private void showUpdateMessage(EntityPlayer player, String updateURL) {

		String modname=new TextComponentTranslation("message.name").getFormattedText();
		String updateMessgae=new TextComponentTranslation("message.update").getFormattedText();
		TextComponentBase hoverMessgae=new TextComponentTranslation("message.update.hover");
		TextComponentBase download=new TextComponentTranslation("message.download");
		
		String msg = "[" +  modname + "] " + updateMessgae + " ";

		ClickEvent openUrl = new ClickEvent(Action.OPEN_URL, updateURL);
		Style style = new Style();
		style.setClickEvent(openUrl);
		style.setUnderlined(true);
		style.setColor(TextFormatting.GREEN);
		style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessgae));
		download.setStyle(style);
		player.sendMessage(new TextComponentString(msg).appendSibling(download));
	}

}
