package de.maxhenkel.gravestone.items;

import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.gui.GUIDeathItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemDeathInfo extends Item {

    public ItemDeathInfo() {
        super(new Item.Properties().maxStackSize(1));
        this.setRegistryName("death_info");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        DeathInfo info = DeathInfo.getDeathInfoFromPlayerHand(playerIn);

        if (playerIn.isSneaking() && playerIn.playerAbilities.isCreativeMode) {
            // TODO
            //playerIn.openContainer(new InventoryDeathItems(info));
            /*playerIn.openContainer(new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return null;
                }

                @Nullable
                @Override
                public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    return new ChestContainer(ContainerType.field_221512_f, );
                }
            });*/
        } else if (worldIn.isRemote) {
            openClientGui(info);
        }
        return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
    }

    @OnlyIn(Dist.CLIENT)
    private void openClientGui(DeathInfo info) {
        Minecraft.getInstance().displayGuiScreen(new GUIDeathItems(info));
    }
}
