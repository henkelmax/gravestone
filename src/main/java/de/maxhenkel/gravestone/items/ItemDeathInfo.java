package de.maxhenkel.gravestone.items;

import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

public class ItemDeathInfo extends Item{

	private static final String NAME="death_info";
	
	public ItemDeathInfo() {
		super();
		this.setUnlocalizedName(NAME);
		setTextureName(Main.MODID +":" +NAME);
		//this.setRegistryName(UNLOCALIZED_NAME);
		this.setMaxStackSize(1);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		if(!worldIn.isRemote){
			return super.onItemRightClick(itemStackIn, worldIn, playerIn);
		}
		
		playerIn.openGui(Main.MODID, GuiHandler.ID_INFO, worldIn, (int)playerIn.posX, (int)playerIn.posY, (int)playerIn.posZ);
		
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}
}
