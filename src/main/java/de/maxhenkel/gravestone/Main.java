package de.maxhenkel.gravestone;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.maxhenkel.gravestone.proxy.CommonProxy;

@Mod(modid = Main.MODID, version = Main.VERSION, acceptedMinecraftVersions=Main.MC_VERSION)
public class Main{
	
    public static final String MODID = "gravestone";
    public static final String VERSION = "0.7.10.3";
    public static final String MC_VERSION = "[1.7.10]";
    public static final int VERSION_NUMBER = 27;

	@Instance
    private static Main instance;

	@SidedProxy(clientSide="de.maxhenkel.gravestone.proxy.ClientProxy", serverSide="de.maxhenkel.gravestone.proxy.CommonProxy")
    public static CommonProxy proxy;
    
	public Main() {
		instance=this;
	}
	
    @EventHandler
    public void preinit(FMLPreInitializationEvent event){
		proxy.preinit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){
    	 proxy.init(event);
    }
    
    @EventHandler
    public void postinit(FMLPostInitializationEvent event){
		proxy.postinit(event);
    }
    
	public static Main instance() {
		return instance;
	}
	
}
