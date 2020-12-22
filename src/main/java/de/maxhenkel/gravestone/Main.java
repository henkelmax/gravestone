package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main{
    public static final String MODID = "gravestone";
    public static final String VERSION = "1.1.5";
    public static final int VERSION_NUMBER = 15;
    
    private Configuration config;
    private Events events;

	@Instance
    private static Main instance;

	@SidedProxy(clientSide="de.maxhenkel.gravestone.proxy.ClientProxy", serverSide="de.maxhenkel.gravestone.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void preinit(FMLPreInitializationEvent event){
		try{
			config=new Configuration(event.getSuggestedConfigurationFile());
		}catch(Exception e){
			e.printStackTrace();
		}
		
    	instance=this;
    	this.events=new Events();
    	MinecraftForge.EVENT_BUS.register(events);
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
    
    public Configuration getConfig() {
		return config;
	}
	
    public Events getEvents() {
		return events;
	}

	public static Main getInstance() {
		return instance;
	}

}
