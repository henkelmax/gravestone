package de.maxhenkel.gravestone.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;

public class Page {

    private static final int ITEM_START_Y = 60;

    private ItemStack[] items;
    private ObituaryScreen gui;

    public Page(ItemStack[] it, ObituaryScreen gui) {
        this.gui = gui;
        int arraySize = 10;
        items = new ItemStack[10];
        if (it.length < 10) {
            arraySize = it.length;
        }
        for (int i = 0; i < items.length && i < arraySize; i++) {
            items[i] = it[i];
        }
    }

    public void drawPage(MatrixStack matrixStack, int page, int pageCount, int mouseX, int mouseY) {
        gui.drawCentered(matrixStack, gui.getFontRenderer(), new TranslationTextComponent("gui.obituary.title.items").func_240699_a_(TextFormatting.UNDERLINE), gui.field_230708_k_ / 2, 30, TextFormatting.BLACK.getColor());
        gui.drawCentered(matrixStack, gui.getFontRenderer(), new TranslationTextComponent("gui.obituary.page", page, pageCount), gui.field_230708_k_ / 2, 43, TextFormatting.DARK_GRAY.getColor());

        int y = ITEM_START_Y;
        final int space = 12;

        for (ItemStack s : items) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            gui.drawItem(matrixStack, new TranslationTextComponent(s.getTranslationKey()).func_240699_a_(TextFormatting.ITALIC), y);
            gui.drawItemSize(matrixStack, new StringTextComponent(String.valueOf(s.getCount())), y);
            y = y + space;
        }

        if (mouseX >= gui.getGuiLeft() + ObituaryScreen.ITEM_SIZE_OFFSET_LEFT && mouseX <= gui.getGuiLeft() + ObituaryScreen.TEXTURE_X - ObituaryScreen.OFFSET_RIGHT) {
            if (mouseY >= ITEM_START_Y && mouseY <= ITEM_START_Y + 10 * space) {
                int index = (mouseY + 3 - ITEM_START_Y) / 12;
                ItemStack stack = items[Math.max(0, Math.min(items.length - 1, index))];
                if (stack != null && !stack.isEmpty()) {
                    gui.func_230457_a_(matrixStack, stack, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(items);
    }

}
