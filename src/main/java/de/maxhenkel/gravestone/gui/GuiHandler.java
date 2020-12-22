package de.maxhenkel.gravestone.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import de.maxhenkel.gravestone.DeathInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

	public static final int ID_INFO=0;
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if(id==ID_INFO){
			DeathInfo info=DeathInfo.getDeathInfoFromPlayerHand(player);
			if(info==null){
				return null;
			}
			return new GUIDeathItems(player, info);
		}
		
		return null;
	}

}
