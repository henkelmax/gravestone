package de.maxhenkel.gravestone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

public class DeathLocationManager {

	private Map<String, DeathLocation> deathLocations;
	
	public DeathLocationManager() {
		this.deathLocations=new HashMap<String, DeathLocation>();
	}
	
	public boolean hasPos(EntityPlayer player){
		String uuid=player.getUniqueID().toString();
		return deathLocations.containsKey(uuid);
	}
	
	public DeathLocation getPos(EntityPlayer player){
		String uuid=player.getUniqueID().toString();
		return deathLocations.get(uuid);
	}
	
	public DeathLocation getPos(String uuid){
		return deathLocations.get(uuid);
	}
	
	public void putPos(EntityPlayer player, DeathLocation location){
		deathLocations.put(player.getUniqueID().toString(), location);
	}
	
	public void putPos(EntityPlayer player){
		deathLocations.put(player.getUniqueID().toString(), new DeathLocation(player.getPosition(), player.dimension));
	}
	
	public void loadPlayer(EntityPlayer player, File file){
		if(!file.exists()){
			return;
		}

		DeathLocation loc=DeathLocation.readFromFile(file);
		
		if(loc!=null){
			deathLocations.put(player.getUniqueID().toString(), loc);
		}
	}
	
	public void savePlayer(EntityPlayer player, File file){
		DeathLocation loc=getPos(player);
		
		if(loc!=null){
			loc.writeToFile(file);
		}
	}
}
