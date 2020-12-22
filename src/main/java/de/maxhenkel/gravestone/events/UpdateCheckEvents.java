package de.maxhenkel.gravestone.events;

import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.util.Tools;
import de.maxhenkel.gravestone.util.UpdateChecker;
import de.maxhenkel.gravestone.util.UpdateChecker.IUpdateCheckResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UpdateCheckEvents {

	private static final String UPDATE_CHECK_URL = "http://maxhenkel.de/update/gravestone_1.9.4.txt";
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
		Style style = new Style();

		style.setClickEvent(openUrl);
		style.setUnderlined(true);
		style.setColor(TextFormatting.GREEN);
		style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(
				Tools.translate("message.update.hover"))));
		TextComponentString comp = new TextComponentString("[Download]");
		comp.setStyle(style);
		player.addChatMessage(new TextComponentString(msg).appendSibling(comp));
	}

}
