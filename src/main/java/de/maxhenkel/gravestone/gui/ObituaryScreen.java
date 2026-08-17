package de.maxhenkel.gravestone.gui;

import com.mojang.authlib.GameProfile;
import de.maxhenkel.corelib.FontColorUtils;
import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.GravestoneMod;
import de.maxhenkel.gravestone.entity.DummyPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.util.Collections;

public class ObituaryScreen extends Screen {

    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(GravestoneMod.MODID, "textures/gui/info.png");
    protected static final int TEXTURE_X = 163;
    protected static final int TEXTURE_Y = 165;
    protected static final int ITEM_OFFSET_LEFT = 40;
    protected static final int OFFSET_LEFT = 7;
    protected static final int OFFSET_RIGHT = 14;
    protected static final int ITEM_SIZE_OFFSET_LEFT = 15;
    protected static final int PLAYER_X = 50;
    protected static final int PLAYER_Y = 70;
    protected static final int PLAYER_SCALE = 30;
    protected static final int TITLE_OFFSET_TOP = 10;
    protected static final int CONTENT_OFFSET_TOP = 30;
    protected static final int BUTTON_WIDTH = 75;
    protected static final int BUTTON_HEIGHT = 20;
    protected static final int BUTTON_SPACING = 5;

    private DummyPlayer player;
    private Death death;

    private Button buttonPrev;
    private Button buttonNext;

    private int page;

    private PageList pageList;

    private int guiLeft;
    private int guiTop;

    public ObituaryScreen(Death death) {
        super(Component.translatable("gui.obituary.title"));
        this.death = death;
        this.page = 0;
        this.pageList = new PageList(death.getAllItems(), this);
    }

    @Override
    protected void init() {
        super.init();

        guiLeft = (width - TEXTURE_X) / 2;
        guiTop = (height - (TEXTURE_Y + BUTTON_SPACING + BUTTON_HEIGHT)) / 2;

        int buttonTop = guiTop + TEXTURE_Y + BUTTON_SPACING;

        buttonPrev = addRenderableWidget(Button.builder(Component.translatable("button.gravestone.prev"), button -> {
            page--;
            if (page < 0) {
                page = 0;
            }
            checkButtons();
        }).bounds(guiLeft, buttonTop, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        buttonNext = addRenderableWidget(Button.builder(Component.translatable("button.gravestone.next"), button -> {
            page++;
            if (page > pageList.getPages()) {
                page = pageList.getPages();
            }
            checkButtons();
        }).bounds(guiLeft + TEXTURE_X - BUTTON_WIDTH, buttonTop, BUTTON_WIDTH, BUTTON_HEIGHT).build());
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
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, guiLeft, guiTop, 0, 0, TEXTURE_X, TEXTURE_Y, 256, 256);

        if (page == 0) {
            drawFirstPage(guiGraphics, mouseX, mouseY);
        } else if (page > 0) {
            if (pageList.getPages() < page - 1) {

            } else {
                pageList.drawPage(guiGraphics, page - 1, mouseX, mouseY);
            }
        }
    }

    public void drawFirstPage(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        drawCentered(guiGraphics, font, Component.translatable("gui.obituary.title").withStyle(ChatFormatting.UNDERLINE), width / 2, guiTop + TITLE_OFFSET_TOP, FontColorUtils.getFontColor(TextColor.BLACK));

        int height = guiTop + CONTENT_OFFSET_TOP;

        if (minecraft.options.advancedItemTooltips) {
            drawLeft(guiGraphics, Component.translatable("gui.obituary.id").append(":").withStyle(ChatFormatting.BLACK), height);
            drawRight(guiGraphics, Component.literal(death.getId().toString()).withStyle(ChatFormatting.DARK_GRAY), height, 0.5F);
            height += 13;
        }

        drawLeft(guiGraphics, Component.translatable("gui.obituary.name").append(":").withStyle(ChatFormatting.BLACK), height);
        drawRight(guiGraphics, Component.literal(death.getPlayerName()).withStyle(ChatFormatting.DARK_GRAY), height);
        height += 13;
        drawLeft(guiGraphics, Component.translatable("gui.obituary.dimension").append(":").withStyle(ChatFormatting.BLACK), height);
        drawRight(guiGraphics, Component.literal(death.getDimension().split(":")[1]).withStyle(ChatFormatting.DARK_GRAY), height);
        height += 13;
        drawLeft(guiGraphics, Component.translatable("gui.obituary.time").append(":").withStyle(ChatFormatting.BLACK), height);
        MutableComponent date = GraveUtils.getDate(death.getTimestamp());
        if (date != null) {
            drawRight(guiGraphics, date.withStyle(ChatFormatting.DARK_GRAY), height);
        } else {
            drawRight(guiGraphics, Component.literal("N/A").withStyle(ChatFormatting.DARK_GRAY), height);
        }
        height += 13;
        drawLeft(guiGraphics, Component.translatable("gui.obituary.location").append(":").withStyle(ChatFormatting.BLACK), height);
        BlockPos pos = death.getBlockPos();
        drawRight(guiGraphics, Component.literal("X: " + pos.getX()).withStyle(ChatFormatting.DARK_GRAY), height);
        height += 13;
        drawRight(guiGraphics, Component.literal("Y: " + pos.getY()).withStyle(ChatFormatting.DARK_GRAY), height);
        height += 13;
        drawRight(guiGraphics, Component.literal("Z: " + pos.getZ()).withStyle(ChatFormatting.DARK_GRAY), height);

        if (player == null) {
            player = new DummyPlayer(minecraft.level, new GameProfile(death.getPlayerUUID(), death.getPlayerName()), death.getEquipment(), death.getModel());
        }

        int playerLeft = guiLeft + (TEXTURE_X - PLAYER_X) / 2;
        int playerTop = guiTop + TEXTURE_Y - PLAYER_Y;
        InventoryScreen.extractEntityInInventoryFollowsMouse(guiGraphics, playerLeft, playerTop, playerLeft + PLAYER_X, playerTop + PLAYER_Y, PLAYER_SCALE, 0.0625F, mouseX, mouseY, player);

        if (minecraft.options.advancedItemTooltips) {
            if (mouseX >= guiLeft + 7 && mouseX <= guiLeft + TEXTURE_X - 7 && mouseY >= guiTop + CONTENT_OFFSET_TOP && mouseY <= guiTop + CONTENT_OFFSET_TOP + font.lineHeight) {
                guiGraphics.setTooltipForNextFrame(font, Collections.singletonList(Component.translatable("gui.obituary.copy_id").getVisualOrderText()), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean b) {
        if (minecraft.options.advancedItemTooltips && page == 0) {
            if (event.x() >= guiLeft + 7 && event.x() <= guiLeft + TEXTURE_X - 7 && event.y() >= guiTop + CONTENT_OFFSET_TOP && event.y() <= guiTop + CONTENT_OFFSET_TOP + font.lineHeight) {
                minecraft.keyboardHandler.setClipboard(death.getId().toString());
                Component deathID = ComponentUtils.wrapInSquareBrackets(Component.translatable("message.gravestone.death_id"))
                        .withStyle((style) -> style
                                .applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent.SuggestCommand("/restore @s " + death.getId().toString() + " replace"))
                                .withHoverEvent(new HoverEvent.ShowText(Component.literal(death.getId().toString())))
                        );
                minecraft.gui.hud.getChat().addClientSystemMessage(Component.translatable("message.gravestone.copied", deathID));
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
                minecraft.gui.setScreen(null);
            }
        }
        return super.mouseClicked(event, b);
    }

    public void drawCentered(GuiGraphicsExtractor guiGraphics, Font fontRenderer, MutableComponent text, int x, int y, int color) {
        guiGraphics.text(fontRenderer, text.getVisualOrderText(), x - fontRenderer.width(text) / 2, y, color, false);
    }

    public void drawItem(GuiGraphicsExtractor guiGraphics, MutableComponent string, int height) {
        guiGraphics.text(font, string.getVisualOrderText(), guiLeft + ITEM_OFFSET_LEFT, height, FontColorUtils.getFontColor(TextColor.BLACK), false);
    }

    public void drawItemSize(GuiGraphicsExtractor guiGraphics, MutableComponent string, int height) {
        guiGraphics.text(font, string.getVisualOrderText(), guiLeft + ITEM_SIZE_OFFSET_LEFT, height, FontColorUtils.getFontColor(TextColor.BLACK), false);
    }

    public void drawLeft(GuiGraphicsExtractor guiGraphics, MutableComponent string, int height) {
        guiGraphics.text(font, string.getVisualOrderText(), guiLeft + OFFSET_LEFT, height, FontColorUtils.getFontColor(TextColor.BLACK), false);
    }

    public void drawRight(GuiGraphicsExtractor guiGraphics, MutableComponent string, int height) {
        drawRight(guiGraphics, string, height, 1F);
    }

    public void drawRight(GuiGraphicsExtractor guiGraphics, MutableComponent string, int height, float scale) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scale, scale);
        float f = 1F / scale;
        int strWidth = font.width(string);
        float spacing = (font.lineHeight * f - font.lineHeight) / 2F;
        guiGraphics.text(font, string.getVisualOrderText(), (int) ((guiLeft + TEXTURE_X - strWidth * scale - OFFSET_RIGHT) * f), (int) (height * f + spacing), FontColorUtils.getFontColor(TextColor.BLACK), false);
        guiGraphics.pose().popMatrix();
    }

    public Font getFontRenderer() {
        return font;
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }
}
