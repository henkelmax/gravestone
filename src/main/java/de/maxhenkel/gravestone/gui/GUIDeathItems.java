package de.maxhenkel.gravestone.gui;

import com.mojang.authlib.GameProfile;
import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class GUIDeathItems extends GuiScreen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/info.png");
    private static final int TEXTURE_X = 163;
    private static final int TEXTURE_Y = 165;

    private DeathInfo info;

    private GuiButton buttonPrev;
    private GuiButton buttonNext;

    private int page;

    private PageList pageList;

    public GUIDeathItems(DeathInfo info) {
        this.info = info;
        this.page = 0;
        this.pageList = new PageList(info.getItems(), this);
    }

    public void initGui() {
        buttons.clear();
        int left = (this.width - TEXTURE_X) / 2;
        buttonPrev = addButton(new GuiButton(0, left, 190, 75, 20, new TextComponentTranslation("button.prev").getFormattedText()) {
            @Override
            public void onClick(double x, double y) {
                super.onClick(x, y);
                page--;
                if (page < 0) {
                    page = 0;
                }
                checkButtons();
            }
        });
        buttonNext = addButton(new GuiButton(1, left + TEXTURE_X - 75, 190, 75, 20, new TextComponentTranslation("button.next").getFormattedText()) {
            @Override
            public void onClick(double x, double y) {
                super.onClick(x, y);
                page++;
                if (page > pageList.getPages()) {
                    page = pageList.getPages();
                }
                checkButtons();
            }
        });
        buttonPrev.enabled = false;
        if (pageList.getPages() <= 0) {
            this.buttonNext.enabled = false;
        }

    }

    protected void checkButtons() {
        if (page <= 0) {
            buttonPrev.enabled = false;
        } else {
            buttonPrev.enabled = true;
        }

        if (page >= pageList.getPages()) {
            buttonNext.enabled = false;
        } else {
            buttonNext.enabled = true;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        int left = (this.width - TEXTURE_X) / 2;

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI_TEXTURE);
        drawTexturedModalRect(left, 20, 0, 0, TEXTURE_X, TEXTURE_Y);

        if (page == 0) {
            drawFirstPage(mouseX, mouseY);
        } else if (page > 0) {
            if (pageList.getPages() < page - 1) {

            } else {
                pageList.drawPage(page - 1);
            }

        }
    }

    public void drawFirstPage(int mouseX, int mouseY) {
        // Title

        String title = new TextComponentTranslation("gui.deathinfo.title").getFormattedText();

        int titleWidth = this.fontRenderer.getStringWidth(title);

        this.fontRenderer.drawString(TextFormatting.BLACK + "" + TextFormatting.UNDERLINE + title, (this.width - titleWidth) / 2, 30, 0);

        // Name

        String textName = new TextComponentTranslation("gui.deathinfo.name").getFormattedText() + ":";
        drawLeft(TextFormatting.BLACK + textName, 50);

        String name = info.getName();
        drawRight(TextFormatting.DARK_GRAY + name, 50);

        // Dimension

        String textDimension = new TextComponentTranslation("gui.deathinfo.dimension").getFormattedText() + ":";
        drawLeft(TextFormatting.BLACK + textDimension, 63);

        String dimension = Tools.translateDimension(info.getDimension());
        drawRight(TextFormatting.DARK_GRAY + dimension, 63);

        // Time

        String textTime = new TextComponentTranslation("gui.deathinfo.time").getFormattedText() + ":";
        drawLeft(TextFormatting.BLACK + textTime, 76);

        String time = Tools.timeToString(info.getTime());
        drawRight(TextFormatting.DARK_GRAY + time, 76);

        // Location

        String textLocation = new TextComponentTranslation("gui.deathinfo.location").getFormattedText() + ":";
        drawLeft(TextFormatting.BLACK + textLocation, 89);

        String locX = "X: " + info.getDeathLocation().getX();
        String locY = "Y: " + info.getDeathLocation().getY();
        String locZ = "Z: " + info.getDeathLocation().getZ();

        drawRight(TextFormatting.DARK_GRAY + locX, 89);
        drawRight(TextFormatting.DARK_GRAY + locY, 102);
        drawRight(TextFormatting.DARK_GRAY + locZ, 115);

        // Player

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        EntityPlayer player = new EntityOtherPlayerMP(this.mc.world, new GameProfile(info.getUuid(), info.getName()));
        GuiInventory.drawEntityOnScreen(width / 2, 175, 30, (width / 2) - mouseX, 100 - mouseY, player);

    }

    public void drawItem(String string, int height) {
        int left = (this.width - TEXTURE_X) / 2;
        int offset = 40;
        int offsetLeft = left + offset;
        this.fontRenderer.drawString(string, offsetLeft, height, 0);
    }

    public void drawItemSize(String string, int height) {
        int left = (this.width - TEXTURE_X) / 2;
        int offset = 15;
        int offsetLeft = left + offset;
        this.fontRenderer.drawString(string, offsetLeft, height, 0);
    }

    public void drawLeft(String string, int height) {
        int left = (this.width - TEXTURE_X) / 2;
        int offset = 7;
        int offsetLeft = left + offset;
        this.fontRenderer.drawString(string, offsetLeft, height, 0);
    }

    public void drawRight(String string, int height) {
        int left = (this.width - TEXTURE_X) / 2;
        int offset = 14;
        int strWidth = this.fontRenderer.getStringWidth(string);
        this.fontRenderer.drawString(string, left + TEXTURE_X - strWidth - offset, height, 0);
    }

    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

}
