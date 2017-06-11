package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.MODID, version = Main.VERSION, acceptedMinecraftVersions=Main.MC_VERSION)
public class Main{
	
    public static final String MODID = "gravestone";
    public static final String VERSION = "1.6.5";
    public static final String MC_VERSION = "[1.11]";
    public static final int VERSION_NUMBER = 27;
    public static final String UPDATE_CHECK_URL = "http://maxhenkel.de/update/gravestone_1.11.txt";

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
