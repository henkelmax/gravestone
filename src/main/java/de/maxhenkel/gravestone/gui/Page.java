package de.maxhenkel.gravestone.gui;

import java.util.Arrays;

import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class Page {

    private ItemStack[] items;
    private GUIDeathInfo gui;

    public Page(ItemStack[] it, GUIDeathInfo gui) {
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

    public void drawPage(int num) {
        String title = new TranslationTextComponent("gui.deathinfo.title.items").getFormattedText();

        int titleWidth = gui.getFontRenderer().getStringWidth(title);

        gui.getFontRenderer().drawString(TextFormatting.BLACK + "" + TextFormatting.UNDERLINE + title, (gui.width - titleWidth) / 2, 30, 0);


        String page = new TranslationTextComponent("gui.deathinfo.page").getFormattedText();
        page = TextFormatting.DARK_GRAY + page + " " + TextFormatting.DARK_GRAY + String.valueOf(num);
        int pageWidth = gui.getFontRenderer().getStringWidth(page);

        gui.getFontRenderer().drawString(page, (gui.width - pageWidth) / 2, 43, 0);

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

            gui.drawItem(TextFormatting.ITALIC + name, y);
            gui.drawItemSize(String.valueOf(s.getCount()), y);

            y = y + space;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(items);
    }

}
