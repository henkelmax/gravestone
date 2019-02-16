package de.maxhenkel.gravestone.items;

import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.gui.GUIDeathItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemDeathInfo extends Item {

    public ItemDeathInfo() {
        super(new Item.Properties().maxStackSize(1));
        this.setRegistryName("death_info");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        DeathInfo info = DeathInfo.getDeathInfoFromPlayerHand(playerIn);

        /*if (playerIn.isSneaking() && playerIn.abilities.isCreativeMode) {
            playerIn.displayGUIChest(new InventoryDeathItems(info));
        }else */
        if (worldIn.isRemote) {
            openClientGui(info);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
    }

    @OnlyIn(Dist.CLIENT)
    private void openClientGui(DeathInfo info) {
        Minecraft.getInstance().displayGuiScreen(new GUIDeathItems(info));
    }
}
