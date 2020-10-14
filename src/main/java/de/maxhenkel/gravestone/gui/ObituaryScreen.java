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
    protected void func_231160_c_() {
        super.func_231160_c_();

        guiLeft = (field_230708_k_ - TEXTURE_X) / 2;
        guiTop = (field_230709_l_ - TEXTURE_Y) / 2;

        field_230710_m_.clear();

        int left = (field_230708_k_ - TEXTURE_X) / 2;
        buttonPrev = func_230480_a_(new Button(left, 190, 75, 20, new TranslationTextComponent("button.gravestone.prev"), button -> {
            page--;
            if (page < 0) {
                page = 0;
            }
            checkButtons();
        }));

        buttonNext = func_230480_a_(new Button(left + TEXTURE_X - 75, 190, 75, 20, new TranslationTextComponent("button.gravestone.next"), button -> {
            page++;
            if (page > pageList.getPages()) {
                page = pageList.getPages();
            }
            checkButtons();
        }));
        buttonPrev.field_230693_o_ = false;
        if (pageList.getPages() <= 0) {
            this.buttonNext.field_230693_o_ = false;
        }
    }

    protected void checkButtons() {
        if (page <= 0) {
            buttonPrev.field_230693_o_ = false;
        } else {
            buttonPrev.field_230693_o_ = true;
        }

        if (page >= pageList.getPages()) {
            buttonNext.field_230693_o_ = false;
        } else {
            buttonNext.field_230693_o_ = true;
        }
    }

    @Override
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        super.func_230430_a_(matrixStack, mouseX, mouseY, p_230430_4_);
        int left = (field_230708_k_ - TEXTURE_X) / 2;

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        field_230706_i_.getTextureManager().bindTexture(GUI_TEXTURE);
        func_238474_b_(matrixStack, left, 20, 0, 0, TEXTURE_X, TEXTURE_Y);

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
        drawCentered(matrixStack, field_230712_o_, new TranslationTextComponent("gui.obituary.title").func_240699_a_(TextFormatting.UNDERLINE), field_230708_k_ / 2, 30, TextFormatting.BLACK.getColor());

        int height = 50;

        if (field_230706_i_.gameSettings.advancedItemTooltips) {
            drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.id").func_240702_b_(":").func_240699_a_(TextFormatting.BLACK), height);
            drawRight(matrixStack, new StringTextComponent(death.getId().toString()).func_240699_a_(TextFormatting.DARK_GRAY), height, 0.5F);
            height += 13;
        }

        drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.name").func_240702_b_(":").func_240699_a_(TextFormatting.BLACK), height);
        drawRight(matrixStack, new StringTextComponent(death.getPlayerName()).func_240699_a_(TextFormatting.DARK_GRAY), height);
        height += 13;
        drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.dimension").func_240702_b_(":").func_240699_a_(TextFormatting.BLACK), height);
        drawRight(matrixStack, new StringTextComponent(death.getDimension().split(":")[1]).func_240699_a_(TextFormatting.DARK_GRAY), height);
        height += 13;
        drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.time").func_240702_b_(":").func_240699_a_(TextFormatting.BLACK), height);
        IFormattableTextComponent date = GraveUtils.getDate(death.getTimestamp());
        if (date != null) {
            drawRight(matrixStack, date.func_240699_a_(TextFormatting.DARK_GRAY), height);
        } else {
            drawRight(matrixStack, new StringTextComponent("N/A").func_240699_a_(TextFormatting.DARK_GRAY), height);
        }
        height += 13;
        drawLeft(matrixStack, new TranslationTextComponent("gui.obituary.location").func_240702_b_(":").func_240699_a_(TextFormatting.BLACK), height);
        BlockPos pos = death.getBlockPos();
        drawRight(matrixStack, new StringTextComponent("X: " + pos.getX()).func_240699_a_(TextFormatting.DARK_GRAY), height);
        height += 13;
        drawRight(matrixStack, new StringTextComponent("Y: " + pos.getY()).func_240699_a_(TextFormatting.DARK_GRAY), height);
        height += 13;
        drawRight(matrixStack, new StringTextComponent("Z: " + pos.getZ()).func_240699_a_(TextFormatting.DARK_GRAY), height);

        RenderSystem.color4f(1F, 1F, 1F, 1F);

        if (player == null) {
            player = new DummyPlayer(field_230706_i_.world, new GameProfile(death.getPlayerUUID(), death.getPlayerName()), death.getEquipment(), death.getModel());
        }

        InventoryScreen.drawEntityOnScreen(field_230708_k_ / 2, 170, 30, (field_230708_k_ / 2) - mouseX, 100 - mouseY, player);

        if (field_230706_i_.gameSettings.advancedItemTooltips) {
            if (mouseX >= guiLeft + 7 && mouseX <= guiLeft + TEXTURE_X - 7 && mouseY >= 50 && mouseY <= 50 + field_230712_o_.FONT_HEIGHT) {
                func_238654_b_(matrixStack, Collections.singletonList(new TranslationTextComponent("gui.obituary.copy_id").func_241878_f()), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean func_231044_a_(double x, double y, int clickType) {
        if (field_230706_i_.gameSettings.advancedItemTooltips && page == 0) {
            if (x >= guiLeft + 7 && x <= guiLeft + TEXTURE_X - 7 && y >= 50 && y <= 50 + field_230712_o_.FONT_HEIGHT) {
                field_230706_i_.keyboardListener.setClipboardString(death.getId().toString());
                ITextComponent deathID = TextComponentUtils.func_240647_a_(new TranslationTextComponent("message.gravestone.death_id"))
                        .func_240700_a_((style) -> style
                                .func_240723_c_(TextFormatting.GREEN)
                                .func_240715_a_(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore " + death.getId().toString() + " @s replace"))
                                .func_240716_a_(new HoverEvent(HoverEvent.Action.field_230550_a_, new StringTextComponent(death.getId().toString())))
                        );
                field_230706_i_.player.sendMessage(new TranslationTextComponent("message.gravestone.copied", deathID), Util.field_240973_b_);
                field_230706_i_.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1F));
                field_230706_i_.displayGuiScreen(null);
            }
        }

        return super.func_231044_a_(x, y, clickType);
    }

    public void drawCentered(MatrixStack matrixStack, FontRenderer fontRenderer, IFormattableTextComponent text, int x, int y, int color) {
        fontRenderer.func_238422_b_(matrixStack, text.func_241878_f(), (float) (x - fontRenderer.func_238414_a_(text) / 2), (float) y, color);
    }

    public void drawItem(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        field_230712_o_.func_238422_b_(matrixStack, string.func_241878_f(), guiLeft + ITEM_OFFSET_LEFT, height, TextFormatting.BLACK.getColor());
    }

    public void drawItemSize(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        field_230712_o_.func_238422_b_(matrixStack, string.func_241878_f(), guiLeft + ITEM_SIZE_OFFSET_LEFT, height, TextFormatting.BLACK.getColor());
    }

    public void drawLeft(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        field_230712_o_.func_238422_b_(matrixStack, string.func_241878_f(), guiLeft + OFFSET_LEFT, height, TextFormatting.BLACK.getColor());
    }

    public void drawRight(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        drawRight(matrixStack, string, height, 1F);
    }

    public void drawRight(MatrixStack matrixStack, IFormattableTextComponent string, int height, float scale) {
        matrixStack.push();
        matrixStack.scale(scale, scale, 1F);
        float f = 1F / scale;
        int strWidth = field_230712_o_.func_238414_a_(string);
        float spacing = (field_230712_o_.FONT_HEIGHT * f - field_230712_o_.FONT_HEIGHT) / 2F;
        field_230712_o_.func_238422_b_(matrixStack, string.func_241878_f(), (guiLeft + TEXTURE_X - strWidth * scale - OFFSET_RIGHT) * f, height * f + spacing, TextFormatting.BLACK.getColor());
        matrixStack.pop();
    }

    @Override
    protected void func_230457_a_(MatrixStack matrixStack, ItemStack stack, int x, int y) {
        super.func_230457_a_(matrixStack, stack, x, y);
    }

    public FontRenderer getFontRenderer() {
        return field_230712_o_;
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

}
