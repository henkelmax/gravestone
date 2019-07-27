package de.maxhenkel.gravestone.gui;

import de.maxhenkel.gravestone.Main;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DeathItemsScreen extends ScreenBase<DeathItemsContainer> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/death_items.png");

    private PlayerInventory playerInventory;

    public DeathItemsScreen(PlayerInventory playerInventory, DeathItemsContainer container, ITextComponent name) {
        super(TEXTURE, container, playerInventory, name);

        this.playerInventory = playerInventory;
        xSize = 176;
        ySize = 222;
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);

        font.drawString(getTitle().getFormattedText(), 8.0F, 6.0F, FONT_COLOR);
        font.drawString(playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (ySize - 96 + 2), FONT_COLOR);
    }
}