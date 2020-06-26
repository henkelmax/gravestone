package de.maxhenkel.gravestone.gui;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

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
        String title = new TranslationTextComponent("gui.deathinfo.title.items").getString();

        int titleWidth = gui.getFontRenderer().getStringWidth(title);

        gui.getFontRenderer().func_238421_b_(matrixStack, TextFormatting.BLACK + "" + TextFormatting.UNDERLINE + title, (gui.field_230708_k_ - titleWidth) / 2, 30, 0);


        String page = new TranslationTextComponent("gui.deathinfo.page").getString();
        page = TextFormatting.DARK_GRAY + page + " " + TextFormatting.DARK_GRAY + num;
        int pageWidth = gui.getFontRenderer().getStringWidth(page);

        gui.getFontRenderer().func_238421_b_(matrixStack, page, (gui.field_230708_k_ - pageWidth) / 2, 43, 0);

        int y = 60;
        final int space = 12;

        for (int i = 0; i < items.length; i++) {
            ItemStack s = items[i];

            if (s == null) {
                continue;
            }

            String name = Tools.translateItem(s);

            if (name == null) {
                continue;
            }

            gui.drawItem(matrixStack, TextFormatting.ITALIC + name, y);
            gui.drawItemSize(matrixStack, String.valueOf(s.getCount()), y);

            y = y + space;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(items);
    }

}
