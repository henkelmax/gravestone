package de.maxhenkel.gravestone.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.item.ItemStack;

public class PageList {

    private List<Page> list;

    public PageList(List<ItemStack> items, GUIDeathInfo gui) {
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

        if (!Tools.isArrayEmpty(temp)) {
            list.add(new Page(temp, gui));
        }

    }

    public int getPages() {
        return list.size();
    }

    public void drawPage(int p) {
        if (p >= list.size()) {
            p = list.size() - 1;
        }

        Page page = list.get(p);
        page.drawPage(p + 1);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(list.toArray(new Page[0]));
    }

}
