package de.maxhenkel.gravestone;

import java.util.List;
import de.maxhenkel.gravestone.UpdateChecker.IUpdateCheckResult;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Events {

	public static final String KEY_CHECK_UPDATES = "check_updates";
	public static final String KEY_DEATH_NOTE = "enable_death_note";

	private boolean checkUpdates;
	private boolean updateShown;
	private boolean givePlayerNote;

	public Events() {
		this.checkUpdates = Main.getInstance().getConfig().getBoolean(KEY_CHECK_UPDATES, true);
		this.givePlayerNote= Main.getInstance().getConfig().getBoolean(KEY_DEATH_NOTE, true);
		this.updateShown = false;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerCloneLast(PlayerEvent.Clone event) {
		if(!givePlayerNote){
			return;
		}
		
		if (event.isCanceled()) {
			return;
		}

		if (!event.isWasDeath()) {
			return;
		}

		if (Tools.keepInventory(event.getEntityPlayer())) {
			return;
		}

		for (ItemStack stack : event.getOriginal().inventory.mainInventory) {
			if (DeathInfo.isDeathInfoItem(stack)) {
				event.getEntityPlayer().inventory.addItemStackToInventory(stack);
			}
		}

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerDeath(LivingDropsEvent event) {
		if (event.isCanceled()) {
			return;
		}

		if (!(event.getEntity() instanceof EntityPlayer)) {
			return;
		}

		if (event.getEntity().worldObj.isRemote) {
			return;
		}

		try {
			
			List<EntityItem> drops = event.getDrops();

			EntityPlayer player = (EntityPlayer) event.getEntity();
			DeathPosition deathPos = new DeathPosition(player);
			if(deathPos.placeGraveStone(drops)){
				event.setCanceled(true);
			}
			if(givePlayerNote){
				deathPos.givePlayerNote();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerDeath(LivingDeathEvent event) {
		if (event.isCanceled()) {
			return;
		}

		if (!(event.getEntity() instanceof EntityPlayer)) {
			return;
		}

		if (event.getEntity().worldObj.isRemote) {
			return;
		}
		
		EntityPlayer player=(EntityPlayer) event.getEntity();
		
		if(!Tools.keepInventory(player)){
			return;
		}
		
		try{
			DeathPosition.givePlayerNote(player);
		}catch(Exception e){
			e.printStackTrace();
		}
		
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

					String msg = "[" + new TextComponentTranslation("message.name", new Object[0]).getFormattedText()
							+ "] " + new TextComponentTranslation("message.update", new Object[0]).getFormattedText()
							+ " ";

					ClickEvent openUrl = new ClickEvent(Action.OPEN_URL,
							"http://minecraft.curseforge.com/projects/gravestone-mod");
					Style style = new Style();

					style.setClickEvent(openUrl);
					style.setUnderlined(true);
					style.setColor(TextFormatting.GREEN);
					style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(
							new TextComponentTranslation("message.update.hover", new Object[0]).getFormattedText())));
					TextComponentString comp = new TextComponentString("[Download]");
					comp.setStyle(style);
					player.addChatMessage(new TextComponentString(msg).appendSibling(comp));
				}
			}
		}, Main.VERSION_NUMBER, "http://maxhenkel.de/update/gravestone_1.10.txt");
		checker.start();
		updateShown = true;
	}

}
