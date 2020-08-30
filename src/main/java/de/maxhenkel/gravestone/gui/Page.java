package de.maxhenkel.gravestone.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;

public class Page {

    private ItemStack[] items;
    private DeathInfoScreen gui;

    public Page(ItemStack[] it, DeathInfoScreen gui) {
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

    public void drawPage(MatrixStack matrixStack, int num) {
        gui.drawCentered(matrixStack, gui.getFontRenderer(), new TranslationTextComponent("gui.deathinfo.title.items").func_240699_a_(TextFormatting.UNDERLINE), gui.field_230708_k_ / 2, 30, TextFormatting.BLACK.getColor());
        gui.drawCentered(matrixStack, gui.getFontRenderer(), new TranslationTextComponent("gui.deathinfo.page", num), gui.field_230708_k_ / 2, 43, TextFormatting.DARK_GRAY.getColor());

        int y = 60;
        final int space = 12;

        for (ItemStack s : items) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            gui.drawItem(matrixStack, new TranslationTextComponent(s.getTranslationKey()).func_240699_a_(TextFormatting.ITALIC), y);
            gui.drawItemSize(matrixStack, new StringTextComponent(String.valueOf(s.getCount())), y);
            y = y + space;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(items);
    }

}
