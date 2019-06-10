package de.maxhenkel.gravestone.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;

public class DeathInfoScreen extends Screen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/info.png");
    private static final int TEXTURE_X = 163;
    private static final int TEXTURE_Y = 165;

    private DeathInfo info;

    private Button buttonPrev;
    private Button buttonNext;

    private int page;

    private PageList pageList;

    public DeathInfoScreen(DeathInfo info) {
        super(new TranslationTextComponent("gui.deathinfo.title"));
        this.info = info;
        this.page = 0;
        this.pageList = new PageList(info.getItems(), this);
    }

    @Override
    protected void init() {
        super.init();

        buttons.clear();
        int left = (this.width - TEXTURE_X) / 2;
        buttonPrev = addButton(new Button(left, 190, 75, 20, new TranslationTextComponent("button.prev").getFormattedText(), new Button.IPressable() {
            @Override
            public void onPress(Button button) {
                page--;
                if (page < 0) {
                    page = 0;
                }
                checkButtons();
            }
        }));

        buttonNext = addButton(new Button(left + TEXTURE_X - 75, 190, 75, 20, new TranslationTextComponent("button.next").getFormattedText(), new Button.IPressable() {
            @Override
            public void onPress(Button button) {
                page++;
                if (page > pageList.getPages()) {
                    page = pageList.getPages();
                }
                checkButtons();
            }
        }));
        buttonPrev.active = false;
        if (pageList.getPages() <= 0) {
            this.buttonNext.active = false;
        }
    }

    protected void checkButtons() {
        if (page <= 0) {
            buttonPrev.active = false;
        } else {
            buttonPrev.active = true;
        }

        if (page >= pageList.getPages()) {
            buttonNext.active = false;
        } else {
            buttonNext.active = true;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        int left = (this.width - TEXTURE_X) / 2;

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
        blit(left, 20, 0, 0, TEXTURE_X, TEXTURE_Y);

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

        String title = new TranslationTextComponent("gui.deathinfo.title").getFormattedText();

        int titleWidth = this.font.getStringWidth(title);

        this.font.drawString(TextFormatting.BLACK + "" + TextFormatting.UNDERLINE + title, (this.width - titleWidth) / 2, 30, 0);

        // Name

        String textName = new TranslationTextComponent("gui.deathinfo.name").getFormattedText() + ":";
        drawLeft(TextFormatting.BLACK + textName, 50);

        String name = info.getName();
        drawRight(TextFormatting.DARK_GRAY + name, 50);

        // Dimension

        String textDimension = new TranslationTextComponent("gui.deathinfo.dimension").getFormattedText() + ":";
        drawLeft(TextFormatting.BLACK + textDimension, 63);

        String dimension = Tools.translateDimension(info.getDimension());
        drawRight(TextFormatting.DARK_GRAY + dimension, 63);

        // Time

        String textTime = new TranslationTextComponent("gui.deathinfo.time").getFormattedText() + ":";
        drawLeft(TextFormatting.BLACK + textTime, 76);

        String time = Tools.timeToString(info.getTime());
        drawRight(TextFormatting.DARK_GRAY + time, 76);

        // Location

        String textLocation = new TranslationTextComponent("gui.deathinfo.location").getFormattedText() + ":";
        drawLeft(TextFormatting.BLACK + textLocation, 89);

        String locX = "X: " + info.getDeathLocation().getX();
        String locY = "Y: " + info.getDeathLocation().getY();
        String locZ = "Z: " + info.getDeathLocation().getZ();

        drawRight(TextFormatting.DARK_GRAY + locX, 89);
        drawRight(TextFormatting.DARK_GRAY + locY, 102);
        drawRight(TextFormatting.DARK_GRAY + locZ, 115);

        // Player

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        PlayerEntity player = new RemoteClientPlayerEntity(minecraft.world, new GameProfile(info.getUuid(), info.getName()));
        InventoryScreen.drawEntityOnScreen(width / 2, 175, 30, (width / 2) - mouseX, 100 - mouseY, player);

    }

    public void drawItem(String string, int height) {
        int left = (this.width - TEXTURE_X) / 2;
        int offset = 40;
        int offsetLeft = left + offset;
        this.font.drawString(string, offsetLeft, height, 0);
    }

    public void drawItemSize(String string, int height) {
        int left = (this.width - TEXTURE_X) / 2;
        int offset = 15;
        int offsetLeft = left + offset;
        this.font.drawString(string, offsetLeft, height, 0);
    }

    public void drawLeft(String string, int height) {
        int left = (this.width - TEXTURE_X) / 2;
        int offset = 7;
        int offsetLeft = left + offset;
        this.font.drawString(string, offsetLeft, height, 0);
    }

    public void drawRight(String string, int height) {
        int left = (this.width - TEXTURE_X) / 2;
        int offset = 14;
        int strWidth = this.font.getStringWidth(string);
        this.font.drawString(string, left + TEXTURE_X - strWidth - offset, height, 0);
    }

    public FontRenderer getFontRenderer() {
        return this.font;
    }

}
