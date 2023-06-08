package de.maxhenkel.gravestone.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PageList {

    private final List<Page> list;

    public PageList(NonNullList<ItemStack> items, ObituaryScreen gui) {
        this.list = new ArrayList<>();

        ItemStack[] temp = new ItemStack[10];
        int i = 0;
        for (ItemStack s : items) {
            temp[i] = s;

            i++;
            if (i > 9) {
                list.add(new Page(temp, gui));
                temp = new ItemStack[10];
                i = 0;
            }
        }

        if (Stream.of(temp).anyMatch(Objects::nonNull)) {
            list.add(new Page(temp, gui));
        }

    }

    public int getPages() {
        return list.size();
    }

    public void drawPage(GuiGraphics guiGraphics, int p, int mouseX, int mouseY) {
        if (p >= list.size()) {
            p = list.size() - 1;
        }

        Page page = list.get(p);
        page.drawPage(guiGraphics, p + 1, list.size(), mouseX, mouseY);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(list.toArray(new Page[0]));
    }

}
