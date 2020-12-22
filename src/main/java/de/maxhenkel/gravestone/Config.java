package de.maxhenkel.gravestone;

import net.minecraftforge.common.config.Configuration;

public class Config {

	private Configuration config;

	public Config(Configuration config) {
		this.config = config;
	}

	public String getString(String key, String def) {
		String s = def;
		try {
			config.load();
			s = config.get(Main.MODID, key, def).getString();
			config.save();
		} catch (Exception e) {}
		return s;
	}
	
	public boolean getBoolean(String key, boolean def) {
		boolean s = def;
		try {
			config.load();
			s = config.get(Main.MODID, key, def).getBoolean();
			config.save();
		} catch (Exception e) {}
		return s;
	}
	
	public String[] getStringArray(String key, String[] def) {
		String[] s = def;
		try {
			config.load();
			s = config.get(Main.MODID, key, def).getStringList();
			config.save();
		} catch (Exception e) {}
		return s;
	}

}
