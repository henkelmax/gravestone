package de.maxhenkel.gravestone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class ItemDeathLocationFinder extends Item{

	private static final String UNLOCALIZED_NAME="death_location_finder";
	
	public ItemDeathLocationFinder() {
		super();
		this.setUnlocalizedName(UNLOCALIZED_NAME);
		this.setRegistryName(UNLOCALIZED_NAME);
		this.setMaxStackSize(1);
		this.setMaxDamage(16);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		if(worldIn.isRemote){
			return super.onItemRightClick(itemStackIn, worldIn, playerIn);
		}
		
		DeathLocation loc=Main.getInstance().getEvents().getDeathLocationManager().getPos(playerIn);
		
		if(loc==null){
			ChatComponentTranslation msg=new ChatComponentTranslation("message.no_deathlocation", new Object[0]);
    		playerIn.addChatMessage(msg);
		}else{
			String msg=new ChatComponentTranslation("message.deathlocation", new Object[0]).getFormattedText();
			msg=msg +": " +loc.toString();
    		playerIn.addChatMessage(new ChatComponentText(msg));
			itemStackIn.damageItem(1, playerIn);
		}
		
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}
	
}
