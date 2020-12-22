package de.maxhenkel.gravestone;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.DimensionManager;

public class Tools {

	public static String translate(String str) {
		return new TextComponentTranslation(str, new Object[0]).getFormattedText();
	}

	public static String dimIDToString(int id) {

		switch (id) {
		case -1:
			return "Nether";
		case 0:
			return "Overworld";
		case 1:
			return "The End";
		default:
			return id + "";
		}
	}
	
	public static String getStringFromItem(Item item){
		if(item==null){
			return null;
		}
		
		return item.getRegistryName().getResourceDomain() +":" +item.getRegistryName().getResourcePath();
	}
	
	public static String translateItem(String str){
		if(str==null){
			return null;
		}
		
		Item i=Item.REGISTRY.getObject(new ResourceLocation(str));
		
		if(i==null){
			return null;
		}
		
		return i.getItemStackDisplayName(new ItemStack(i));
	}
	
	public static boolean isArrayEmpty(Object[] obj){
		for(Object o:obj){
			if(o!=null){
				return false;
			}
		}
		return true;
	}
	
	public static boolean keepInventory(EntityPlayer player){
		try{
			return player.worldObj.getWorldInfo().getGameRulesInstance().getBoolean("keepInventory");
		}catch(Exception e){
			return false;
		}
	}
	
	public static String timeToString(long time){
		if(time==0L){
			return "";
		}
		
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(c.getTime());
	}

}
