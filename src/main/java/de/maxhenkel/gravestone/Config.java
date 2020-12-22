package de.maxhenkel.gravestone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.maxhenkel.gravestone.util.Tools;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;

public class Config {

	private static Config instance;
	private Configuration config;
	
	public final boolean checkUpdates;
	public final Map<Integer, String> dimensionNames;
	public final String dateFormat;
	public final boolean renderSkull;
	public final boolean livingGraves;
	public final boolean giveDeathNotes;
	public final List<Block> replaceableBlocks;
	public final boolean removeDeathNote;
	public final boolean onlyPlayersCanBreak;
	

	public Config(Configuration config) {
		this.config = config;
		this.config.load();
		
		this.checkUpdates=checkUpdates();
		this.dimensionNames=Collections.unmodifiableMap(getDimensionNames());
		this.dateFormat=getDateFormat();
		this.renderSkull=renderSkull();
		this.livingGraves=livingGraves();
		this.giveDeathNotes=giveDeathNotes();
		this.replaceableBlocks=Collections.unmodifiableList(getReplaceableBlocks());
		this.removeDeathNote=removeDeathNote();
		this.onlyPlayersCanBreak=onlyOwnersCanBreak();
		
		this.config.save();
		
	}

	private String getString(String key, String category, String defaultValue, String comment) {
		String s = defaultValue;
		try {
			s = config.getString(key, category, defaultValue, comment);
		} catch (Exception e) {}
		return s;
	}

	private boolean getBoolean(String key, String category, boolean defaultValue, String comment) {
		boolean s = defaultValue;
		try {
			s = config.getBoolean(key, category, defaultValue, comment);
		} catch (Exception e) {}
		return s;
	}

	private String[] getStringArray(String key, String category, String[] defaultValues, String comment) {
		String[] s = defaultValues;
		try {
			s = config.getStringList(key, category, defaultValues, comment);
		} catch (Exception e) {}
		return s;
	}

	private boolean checkUpdates() {
		return getBoolean("check_updates", "gravestone", true,
				"Whether the mod should notify you when an update is available");
	}

	private Map<Integer, String> getDimensionNames() {
		String[] def = new String[] { "-1: Nether", "0: Overworld", "1: The End" };

		String[] dimsStr = getStringArray("dimension_names", "gravestone", def,
				"The names of the Dimensions for the Death Note");

		Map<Integer, String> dims = new HashMap<Integer, String>();

		for (String str : dimsStr) {
			try {
				int i = str.indexOf(":");

				if (i < 0) {
					continue;
				}
				
				if(str.length()-1<i+1){
					continue;
				}

				String did = str.substring(0, i);
				String name = str.substring(i+1).trim();
				int dimid = Integer.parseInt(did);

				if (name.isEmpty()) {
					Log.w("Failed to load dimension name for id " + dimid);
					continue;
				}

				dims.put(dimid, name);
			} catch (Exception e) {
				Log.w("Failed to load dimension name '" + str + "': " + e.getMessage());
			}
		}

		return dims;
	}

	private String getDateFormat() {
		return getString("grave_date_format", "gravestone", "yyyy/MM/dd HH:mm:ss",
				"The date format outputted by clicking the gravestone or displayed in the death note");
	}

	private boolean renderSkull() {
		return getBoolean("render_skull", "gravestone", true,
				"If this is set to true the players head will be rendered on the gravestone when there is a full block under it");
	}

	private boolean livingGraves() {
		return getBoolean("enable_living_entity_graves", "gravestone", false,
				"If this is set to true every living entity will generate a gravestone");
	}

	private boolean giveDeathNotes() {
		return getBoolean("enable_death_note", "gravestone", true,
				"If this is set to true you get a death note after you died");
	}

	private static final String[] DEFAULT_BLOCKS = new String[] { "minecraft:tallgrass", "minecraft:water",
			"minecraft:lava", "minecraft:yellow_flower", "minecraft:red_flower", "minecraft:double_plant",
			"minecraft:sapling", "minecraft:brown_mushroom", "minecraft:red_mushroom", "minecraft:torch",
			"minecraft:snow_layer", "minecraft:vine", "minecraft:deadbush", "minecraft:reeds", "minecraft:fire" };

	private List<Block> getReplaceableBlocks() {
		List<Block> replaceableBlocks = new ArrayList<Block>();
		try {
			String[] blocks = getStringArray("replaceable_blocks", "gravestone", DEFAULT_BLOCKS,
					"The blocks that can be replaced with a grave when someone dies on it");

			replaceableBlocks = Tools.getBlocks(blocks);

			if (blocks == null) {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			Log.w("Failed to load replaceable blocks");
			try {
				replaceableBlocks = Tools.getBlocks(DEFAULT_BLOCKS);
			} catch (Exception ex) {
				replaceableBlocks = new ArrayList<Block>();
			}
		}
		
		return replaceableBlocks;
	}
	
	private boolean removeDeathNote(){
		return getBoolean("remove_death_note", "gravestone", false, "If this is set to true the death note will be taken out of your inventory when you destroyed the gravestone");
	}
	
	private boolean onlyOwnersCanBreak(){
		return getBoolean("only_owners_can_break", "gravestone", false, "If this is set to true only the player that owns the gravestone and the admins can break the gravestone");
	}

	public void setInstance() {
		instance = this;
	}

	public static Config instance() {
		return instance;
	}

}
