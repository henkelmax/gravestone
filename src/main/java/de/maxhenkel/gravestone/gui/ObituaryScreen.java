package de.maxhenkel.gravestone.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.entity.DummyPlayer;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Collections;

public class ObituaryScreen extends Screen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/info.png");
    protected static final int TEXTURE_X = 163;
    protected static final int TEXTURE_Y = 165;
    protected static final int ITEM_OFFSET_LEFT = 40;
    protected static final int OFFSET_LEFT = 7;
    protected static final int OFFSET_RIGHT = 14;
    protected static final int ITEM_SIZE_OFFSET_LEFT = 15;

    private DummyPlayer player;
    private Death death;

    private Button buttonPrev;
    private Button buttonNext;

    private int page;

    private PageList pageList;

    private int guiLeft;
    private int guiTop;

    public ObituaryScreen(Death death) {
        super(new TranslationTextComponent("gui.obituary.title"));
        this.death = death;
        this.page = 0;
        this.pageList = new PageList(death.getAllItems(), this);
    }

    @Override
    protected void init() {
        super.init();

        guiLeft = (width - TEXTURE_X) / 2;
        guiTop = (height - TEXTURE_Y) / 2;

        buttons.clear();

        int left = (width - TEXTURE_X) / 2;
        buttonPrev = addButton(new Button(left, 190, 75, 20, new TranslationTextComponent("button.gravestone.prev"), button -> {
            page--;
            if (page < 0) {
                page = 0;
            }
            checkButtons();
        }));

        buttonNext = addButton(new Button(left + TEXTURE_X - 75, 190, 75, 20, new TranslationTextComponent("button.gravestone.next"), button -> {
            page++;
            if (page > pageList.getPages()) {
                page = pageList.getPages();
            }
            checkButtons();
        }));
        buttonPrev.active = false;
        if (pageList.getPages() <= 0) {
            buttonNext.active = false;
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        int left = (width - TEXTURE_X) / 2;

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        minecraft.getTextureManager().bind(GUI_TEXTURE);
        blit(matrixStack, left, 20, 0, 0, TEXTURE_X, TEXTURE_Y);

        if (page == 0) {
            drawFirstPage(matrixStack, mouseX, mouseY);
        } else if (page > 0) {
            if (pageList.getPages() < page - 1) {

            } else {
                pageList.drawPage(matrixStack, page - 1, mouseX, mouseY);
            }
        }
    }

    public void drawFirstPage(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawCentered(matrixStack, font, new TranslationTextComponent("gui.obituary.title").withStyle(TextFormatting.UNDERLINE), width / 2, 30, TextFormatting.BLACK.getColor());

        int height = 50;

        if (minecraft.options.advancedItemTooltips) {
            drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.id").append(":").withStyle(TextFormatting.BLACK), height);
            drawRight(matrixStack, new StringTextComponent(death.getId().toString()).withStyle(TextFormatting.DARK_GRAY), height, 0.5F);
            height += 13;
        }

        drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.name").append(":").withStyle(TextFormatting.BLACK), height);
        drawRight(matrixStack, new StringTextComponent(death.getPlayerName()).withStyle(TextFormatting.DARK_GRAY), height);
        height += 13;
        drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.dimension").append(":").withStyle(TextFormatting.BLACK), height);
        drawRight(matrixStack, new StringTextComponent(death.getDimension().split(":")[1]).withStyle(TextFormatting.DARK_GRAY), height);
        height += 13;
        drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.time").append(":").withStyle(TextFormatting.BLACK), height);
        IFormattableTextComponent date = GraveUtils.getDate(death.getTimestamp());
        if (date != null) {
            drawRight(matrixStack, date.withStyle(TextFormatting.DARK_GRAY), height);
        } else {
            drawRight(matrixStack, new StringTextComponent("N/A").withStyle(TextFormatting.DARK_GRAY), height);
        }
        height += 13;
        drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.location").append(":").withStyle(TextFormatting.BLACK), height);
        BlockPos pos = death.getBlockPos();
        drawRight(matrixStack, new StringTextComponent("X: " + pos.getX()).withStyle(TextFormatting.DARK_GRAY), height);
        height += 13;
        drawRight(matrixStack, new StringTextComponent("Y: " + pos.getY()).withStyle(TextFormatting.DARK_GRAY), height);
        height += 13;
        drawRight(matrixStack, new StringTextComponent("Z: " + pos.getZ()).withStyle(TextFormatting.DARK_GRAY), height);

        RenderSystem.color4f(1F, 1F, 1F, 1F);

        if (player == null) {
            player = new DummyPlayer(minecraft.level, new GameProfile(death.getPlayerUUID(), death.getPlayerName()), death.getEquipment(), death.getModel());
        }

        InventoryScreen.renderEntityInInventory(width / 2, 170, 30, (width / 2) - mouseX, 100 - mouseY, player);

        if (minecraft.options.advancedItemTooltips) {
            if (mouseX >= guiLeft + 7 && mouseX <= guiLeft + TEXTURE_X - 7 && mouseY >= 50 && mouseY <= 50 + font.lineHeight) {
                renderTooltip(matrixStack, Collections.singletonList(new TranslationTextComponent("gui.obituary.copy_id").getVisualOrderText()), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int clickType) {
        if (minecraft.options.advancedItemTooltips && page == 0) {
            if (x >= guiLeft + 7 && x <= guiLeft + TEXTURE_X - 7 && y >= 50 && y <= 50 + font.lineHeight) {
                minecraft.keyboardHandler.setClipboard(death.getId().toString());
                ITextComponent deathID = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("message.gravestone.death_id"))
                        .withStyle((style) -> style
                                .applyFormat(TextFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " replace"))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(death.getId().toString())))
                        );
                minecraft.player.sendMessage(new TranslationTextComponent("message.gravestone.copied", deathID), Util.NIL_UUID);
                minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
                minecraft.setScreen(null);
            }
        }

        return super.mouseClicked(x, y, clickType);
    }

    public void drawCentered(MatrixStack matrixStack, FontRenderer fontRenderer, IFormattableTextComponent text, int x, int y, int color) {
        fontRenderer.draw(matrixStack, text.getVisualOrderText(), (float) (x - fontRenderer.width(text) / 2), (float) y, color);
    }

    public void drawItem(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        font.draw(matrixStack, string.getVisualOrderText(), guiLeft + ITEM_OFFSET_LEFT, height, TextFormatting.BLACK.getColor());
    }

    public void drawItemSize(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        font.draw(matrixStack, string.getVisualOrderText(), guiLeft + ITEM_SIZE_OFFSET_LEFT, height, TextFormatting.BLACK.getColor());
    }

    public void drawLeft(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        font.draw(matrixStack, string.getVisualOrderText(), guiLeft + OFFSET_LEFT, height, TextFormatting.BLACK.getColor());
    }

    public void drawRight(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        drawRight(matrixStack, string, height, 1F);
    }

    public void drawRight(MatrixStack matrixStack, IFormattableTextComponent string, int height, float scale) {
        matrixStack.pushPose();
        matrixStack.scale(scale, scale, 1F);
        float f = 1F / scale;
        int strWidth = font.width(string);
        float spacing = (font.lineHeight * f - font.lineHeight) / 2F;
        font.draw(matrixStack, string.getVisualOrderText(), (guiLeft + TEXTURE_X - strWidth * scale - OFFSET_RIGHT) * f, height * f + spacing, TextFormatting.BLACK.getColor());
        matrixStack.popPose();
    }

    public FontRenderer getFontRenderer() {
        return font;
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

    @Override
    public void renderTooltip(MatrixStack matrixStack, ItemStack itemStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, itemStack, mouseX, mouseY);
    }
}
