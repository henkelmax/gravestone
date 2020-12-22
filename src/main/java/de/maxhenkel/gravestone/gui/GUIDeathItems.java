package de.maxhenkel.gravestone.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.GraveProcessor;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.util.Tools;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GUIDeathItems extends GuiScreen {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/info.png");
	private static final int TEXTURE_X = 163;
	private static final int TEXTURE_Y = 165;

	private EntityPlayer player;
	private DeathInfo info;

	private GuiButton buttonPrev;
	private GuiButton buttonNext;

	private int page;

	private PageList pageList;

	public GUIDeathItems(EntityPlayer player, DeathInfo info) {
		this.player = player;
		this.info = info;
		this.page = 0;
		this.pageList = new PageList(info.getItems(), this);
	}

	public void initGui() {
		this.buttonList.clear();
		int left = (this.width - TEXTURE_X) / 2;
		this.buttonPrev = new GuiButton(0, left, 190, 75, 20, Tools.translate("button.prev"));
		this.buttonNext = new GuiButton(1, left + TEXTURE_X - 75, 190, 75, 20, Tools.translate("button.next"));
		buttonList.add(buttonPrev);
		buttonList.add(buttonNext);
		this.buttonPrev.enabled=false;
		if(pageList.getPages()<=0){
			this.buttonNext.enabled=false;
		}
		
	}
	
	protected void actionPerformed(GuiButton button){
		if (button.enabled) {
			if (button.id == 0) {
				page--;
				if (page < 0) {
					page = 0;
				}

			} else if (button.id == 1) {
				page++;
				if (page > pageList.getPages()) {
					page = pageList.getPages();
				}
				
			}
		}
		
		if(page<=0){
			buttonPrev.enabled=false;
		}else{
			buttonPrev.enabled=true;
		}
		
		if(page>=pageList.getPages()){
			buttonNext.enabled=false;
		}else{
			buttonNext.enabled=true;
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		int left = (this.width - TEXTURE_X) / 2;

		GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		this.drawTexturedModalRect(left, 20, 0, 0, TEXTURE_X, TEXTURE_Y);

		if (page == 0) {
			drawFirstPage(mouseX, mouseY);
		} else if (page > 0) {
			if (pageList.getPages() < page - 1) {

			} else {
				pageList.drawPage(page - 1);
			}

		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void drawFirstPage(int mouseX, int mouseY) {

		int left = (this.width - TEXTURE_X) / 2;
		int offset = 5;
		int offsetLeft = left + offset;

		// Title

		String title = Tools.translate("gui.deathinfo.title");

		int titleWidth = this.fontRendererObj.getStringWidth(title);

		this.fontRendererObj.drawString(EnumChatFormatting.BLACK +"" +EnumChatFormatting.UNDERLINE +title, (this.width - titleWidth) / 2, 30, 0);

		// Name

		String textName = Tools.translate("gui.deathinfo.name") + ":";
		drawLeft(EnumChatFormatting.BLACK + textName, 50);

		String name = info.getName();
		drawRight(EnumChatFormatting.DARK_GRAY + name, 50);

		// Dimension

		String textDimension = Tools.translate("gui.deathinfo.dimension") + ":";
		drawLeft(EnumChatFormatting.BLACK + textDimension, 63);

		String dimension = Tools.dimIDToString(info.getDimension());
		drawRight(EnumChatFormatting.DARK_GRAY + dimension, 63);

		// Time

		String textTime = Tools.translate("gui.deathinfo.time") + ":";
		drawLeft(EnumChatFormatting.BLACK + textTime, 76);

		String time = Tools.timeToString(info.getTime());
		drawRight(EnumChatFormatting.DARK_GRAY + time, 76);

		// Location

		String textLocation = Tools.translate("gui.deathinfo.location") + ":";
		drawLeft(EnumChatFormatting.BLACK + textLocation, 89);

		String locX = "X: " + info.getDeathLocation().getX();
		String locY = "Y: " + info.getDeathLocation().getY();
		String locZ = "Z: " + info.getDeathLocation().getZ();

		drawRight(EnumChatFormatting.DARK_GRAY + locX, 89);
		drawRight(EnumChatFormatting.DARK_GRAY + locY, 102);
		drawRight(EnumChatFormatting.DARK_GRAY + locZ, 115);

		// Player
		
		GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
		
		EntityPlayer player=new EntityOtherPlayerMP(this.mc.theWorld, new GameProfile(info.getUuid(), info.getName()));
		GuiInventory.func_147046_a(width / 2, 175, 30, (width / 2) - mouseX, 100 - mouseY, player);

	}

	public void drawItem(String string, int height) {
		int left = (this.width - TEXTURE_X) / 2;
		int offset = 40;
		int offsetLeft = left + offset;
		this.fontRendererObj.drawString(string, offsetLeft, height, 0);
	}

	public void drawItemSize(String string, int height) {
		int left = (this.width - TEXTURE_X) / 2;
		int offset = 15;
		int offsetLeft = left + offset;
		this.fontRendererObj.drawString(string, offsetLeft, height, 0);
	}

	public void drawLeft(String string, int height) {
		int left = (this.width - TEXTURE_X) / 2;
		int offset = 7;
		int offsetLeft = left + offset;
		this.fontRendererObj.drawString(string, offsetLeft, height, 0);
	}

	public void drawRight(String string, int height) {
		int left = (this.width - TEXTURE_X) / 2;
		int offset = 14;
		int offsetLeft = left + offset;
		int strWidth = this.fontRendererObj.getStringWidth(string);
		this.fontRendererObj.drawString(string, left + TEXTURE_X - strWidth - offset, height, 0);
	}

	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}

}
