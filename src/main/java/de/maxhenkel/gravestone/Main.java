package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.proxy.ClientProxy;
import de.maxhenkel.gravestone.proxy.CommonProxy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(Main.MODID)
@Mod.EventBusSubscriber(modid = Main.MODID)
public class Main {

    public static final String MODID = "gravestone";
    //public static final String VERSION = "1.11.0";
    //public static final String MC_VERSION = "[1.13]";
    //public static final String UPDATE_JSON = "http://maxhenkel.de/update/gravestone.json";

    private static Main instance;

    //@Prox(clientSide="de.maxhenkel.gravestone.proxy.ClientProxy", serverSide="de.maxhenkel.gravestone.proxy.CommonProxy")
    public static CommonProxy proxy;

    public Main() {
        instance = this;
        this.proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new CommonProxy());
    }

    @SubscribeEvent
    public void preinit(FMLPreInitializationEvent event) {
        proxy.preinit(event);
    }

    @SubscribeEvent
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @SubscribeEvent
    public void postinit(FMLPostInitializationEvent event) {
        proxy.postinit(event);
    }

    public static Main instance() {
        return instance;
    }

}
