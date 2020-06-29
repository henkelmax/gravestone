package de.maxhenkel.gravestone.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

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
    protected void func_231160_c_() {
        super.func_231160_c_();

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
                pageList.drawPage(matrixStack, page - 1);
            }
        }
    }

    public void drawFirstPage(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawCentered(matrixStack, field_230712_o_, new TranslationTextComponent("gui.deathinfo.title").func_240699_a_(TextFormatting.UNDERLINE), field_230708_k_ / 2, 30, TextFormatting.BLACK.getColor());

        drawLeft(matrixStack, new TranslationTextComponent("gui.deathinfo.name").func_240699_a_(TextFormatting.BLACK), 50);
        drawRight(matrixStack, new StringTextComponent(info.getName()).func_240699_a_(TextFormatting.DARK_GRAY), 50);

        drawLeft(matrixStack, new TranslationTextComponent("gui.deathinfo.dimension").func_240699_a_(TextFormatting.BLACK), 63);
        drawRight(matrixStack, new StringTextComponent(Tools.translateDimension(info.getDimension())).func_240699_a_(TextFormatting.DARK_GRAY), 63);

        drawLeft(matrixStack, new TranslationTextComponent("gui.deathinfo.time").func_240702_b_(":").func_240699_a_(TextFormatting.BLACK), 76);
        drawRight(matrixStack, new StringTextComponent(Tools.timeToString(info.getTime())).func_240699_a_(TextFormatting.DARK_GRAY), 76);

        drawLeft(matrixStack, new TranslationTextComponent("gui.deathinfo.location").func_240702_b_(":").func_240699_a_(TextFormatting.BLACK), 89);
        drawRight(matrixStack, new StringTextComponent("X: " + info.getDeathLocation().getX()).func_240699_a_(TextFormatting.DARK_GRAY), 89);
        drawRight(matrixStack, new StringTextComponent("Y: " + info.getDeathLocation().getY()).func_240699_a_(TextFormatting.DARK_GRAY), 102);
        drawRight(matrixStack, new StringTextComponent("Z: " + info.getDeathLocation().getZ()).func_240699_a_(TextFormatting.DARK_GRAY), 115);

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        PlayerEntity player = new RemoteClientPlayerEntity(field_230706_i_.world, new GameProfile(info.getUuid(), info.getName()));
        InventoryScreen.drawEntityOnScreen(field_230708_k_ / 2, 175, 30, (field_230708_k_ / 2) - mouseX, 100 - mouseY, player);
    }

    public void drawCentered(MatrixStack matrixStack, FontRenderer fontRenderer, ITextProperties text, int x, int y, int color) {
        fontRenderer.func_238422_b_(matrixStack, text, (float) (x - fontRenderer.func_238414_a_(text) / 2), (float) y, color);
    }

    public void drawItem(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        int left = (field_230708_k_ - TEXTURE_X) / 2;
        int offset = 40;
        int offsetLeft = left + offset;
        field_230712_o_.func_238422_b_(matrixStack, string, offsetLeft, height, TextFormatting.BLACK.getColor());
    }

    public void drawItemSize(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        int left = (field_230708_k_ - TEXTURE_X) / 2;
        int offset = 15;
        int offsetLeft = left + offset;
        field_230712_o_.func_238422_b_(matrixStack, string, offsetLeft, height, TextFormatting.BLACK.getColor());
    }

    public void drawLeft(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        int left = (field_230708_k_ - TEXTURE_X) / 2;
        int offset = 7;
        int offsetLeft = left + offset;
        field_230712_o_.func_238422_b_(matrixStack, string, offsetLeft, height, TextFormatting.BLACK.getColor());
    }

    public void drawRight(MatrixStack matrixStack, IFormattableTextComponent string, int height) {
        int left = (field_230708_k_ - TEXTURE_X) / 2;
        int offset = 14;
        int strWidth = field_230712_o_.func_238414_a_(string);
        field_230712_o_.func_238422_b_(matrixStack, string, left + TEXTURE_X - strWidth - offset, height, TextFormatting.BLACK.getColor());
    }

    public FontRenderer getFontRenderer() {
        return field_230712_o_;
    }

}
