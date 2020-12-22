package de.maxhenkel.gravestone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.DimensionManager;

public class DeathLocation {

	private BlockPos blockPos;
	private int dimID;

	public DeathLocation(BlockPos blockPos, int dimID) {
		this.blockPos = blockPos;
		this.dimID = dimID;
	}

	public BlockPos getBlockPos() {
		return blockPos;
	}

	public int getDimID() {
		return dimID;
	}

	public void writeToFile(File file) {
		try {
			FileWriter writer = new FileWriter(file);

			writer.write(String.valueOf(blockPos.getX()) + "\n");
			writer.write(String.valueOf(blockPos.getY()) + "\n");
			writer.write(String.valueOf(blockPos.getZ()) + "\n");
			writer.write(String.valueOf(dimID) + "\n");

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DeathLocation readFromFile(File file) {
		if (!file.exists()) {
			return null;
		}

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			int x = Integer.parseInt(reader.readLine());
			int y = Integer.parseInt(reader.readLine());
			int z = Integer.parseInt(reader.readLine());
			int dimID = Integer.parseInt(reader.readLine());

			reader.close();

			return new DeathLocation(new BlockPos(x, y, z), dimID);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String dimIDToString(int id){

		switch(id){
		case -1:
			return "Nether";
		case 0:
			return "Overworld";
		case 1:
			return "The End";
		default:
			String name="";
			try{
				name=DimensionManager.getWorld(id).getWorldInfo().getWorldName();
			}catch(Exception e){}
			
			if(name==null){
				name="";
			}
			
			return name;
		}
	}

	@Override
	public String toString() {
		return "X: " + blockPos.getX() + " Y: " + blockPos.getY() + " Z: " + blockPos.getZ() + " ("
				+ dimIDToString(dimID) +")";
	}

}
