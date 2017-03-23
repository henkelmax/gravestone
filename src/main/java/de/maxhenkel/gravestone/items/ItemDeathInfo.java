package de.maxhenkel.gravestone.items;

import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemDeathInfo extends Item{

	private static final String UNLOCALIZED_NAME="death_info";
	
	public ItemDeathInfo() {
		super();
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setRegistryName(UNLOCALIZED_NAME);
		this.setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if(!worldIn.isRemote){
			return super.onItemRightClick(worldIn, playerIn, hand);
		}
		
		playerIn.openGui(Main.MODID, GuiHandler.ID_INFO, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}

}
