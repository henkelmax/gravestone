package de.maxhenkel.gravestone.gui;

import de.maxhenkel.gravestone.Main;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;

public class DeathItemsContainer extends ContainerBase {
    private IInventory albumInventory;

    public DeathItemsContainer(int id, IInventory playerInventory, IInventory inventory) {
        super(Main.DEATH_INFO_INVENTORY_CONTAINER, id, playerInventory, inventory);
        this.albumInventory = inventory;

        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 9; y++) {

                addSlot(new Slot(inventory, y + x * 9, 8 + y * 18, 18 + x * 18));
            }
        }

        addInvSlots();
    }

    public DeathItemsContainer(int id, IInventory playerInventory) {
        this(id, playerInventory, new Inventory(54));
    }

    @Override
    public int getInventorySize() {
        return 54;
    }

    @Override
    public int getInvOffset() {
        return 56;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return albumInventory.isUsableByPlayer(playerIn);
    }
}
