package de.maxhenkel.gravestone.events;

import com.mojang.realmsclient.gui.ChatFormatting;

import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.util.Tools;
import de.maxhenkel.gravestone.util.UpdateChecker;
import de.maxhenkel.gravestone.util.UpdateChecker.IUpdateCheckResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UpdateCheckEvents {

	private static final String UPDATE_CHECK_URL = "http://maxhenkel.de/update/gravestone_1.8.8.txt";
	private static final String UPDATE_URL = "http://minecraft.curseforge.com/projects/gravestone-mod";

	private boolean checkUpdates;
	private boolean updateShown;

	public UpdateCheckEvents() {
		this.checkUpdates = Config.instance().checkUpdates;
		this.updateShown = false;
	}

	@SubscribeEvent
	public void playerJoin(EntityJoinWorldEvent event) {
		if (event.isCanceled()) {
			return;
		}

		if (!(event.entity instanceof EntityPlayer)) {
			return;
		}

		if (!event.world.isRemote) {
			return;
		}

		final EntityPlayer player = (EntityPlayer) event.entity;

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
					showUpdateMessage(player);
				}
			}
		}, Main.VERSION_NUMBER, UPDATE_CHECK_URL);
		checker.start();
		updateShown = true;
	}

	private void showUpdateMessage(EntityPlayer player) {

		String msg = "[" + Tools.translate("message.name") + "] "
				+ Tools.translate("message.update") + " ";

		ClickEvent openUrl = new ClickEvent(Action.OPEN_URL, UPDATE_URL);
		ChatStyle style = new ChatStyle();

		style.setChatClickEvent(openUrl);
		style.setUnderlined(true);
		style.setColor(EnumChatFormatting.GREEN);
		style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
				Tools.translate("message.update.hover"))));
		ChatComponentText comp = new ChatComponentText("[Download]");
		comp.setChatStyle(style);
		player.addChatMessage(new ChatComponentText(msg).appendSibling(comp));
	}

}
