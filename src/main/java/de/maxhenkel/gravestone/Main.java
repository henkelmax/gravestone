package de.maxhenkel.gravestone;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.commands.RestoreCommand;
import de.maxhenkel.gravestone.entity.GhostPlayerEntity;
import de.maxhenkel.gravestone.entity.PlayerGhostRenderer;
import de.maxhenkel.gravestone.events.DeathEvents;
import de.maxhenkel.gravestone.items.ObituaryItem;
import de.maxhenkel.gravestone.net.MessageOpenObituary;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import de.maxhenkel.gravestone.tileentity.render.GravestoneRenderer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Main.MODID)
@Mod(Main.MODID)
public class Main {

    public static final String MODID = "gravestone";

    public static final Logger LOGGER = LogManager.getLogger(Main.MODID);

    public static SimpleChannel SIMPLE_CHANNEL;

    public static GraveStoneBlock GRAVESTONE;
    public static Item GRAVESTONE_ITEM;
    public static TileEntityType<GraveStoneTileEntity> GRAVESTONE_TILEENTITY;
    public static ObituaryItem OBITUARY;
    public static EntityType<GhostPlayerEntity> GHOST;
    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::registerTileEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::registerEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerAttributes);

        SERVER_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.COMMON, ServerConfig.class, true);
        CLIENT_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.CLIENT, ClientConfig.class, true);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup));
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new DeathEvents());
        MinecraftForge.EVENT_BUS.register(this);

        SIMPLE_CHANNEL = CommonRegistry.registerChannel(Main.MODID, "default");
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 0, MessageOpenObituary.class);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(GRAVESTONE_TILEENTITY, GravestoneRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(GHOST, PlayerGhostRenderer::new);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RestoreCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                GRAVESTONE = new GraveStoneBlock()
        );
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                GRAVESTONE_ITEM = GRAVESTONE.toItem(),
                OBITUARY = new ObituaryItem()
        );
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        GRAVESTONE_TILEENTITY = TileEntityType.Builder.of(GraveStoneTileEntity::new, GRAVESTONE).build(null);
        GRAVESTONE_TILEENTITY.setRegistryName(new ResourceLocation(MODID, "gravestone"));
        event.getRegistry().register(GRAVESTONE_TILEENTITY);
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        GHOST = CommonRegistry.registerEntity(Main.MODID, "player_ghost", EntityClassification.MONSTER, GhostPlayerEntity.class, builder -> builder.sized(0.6F, 1.95F));
        event.getRegistry().register(GHOST);
    }

    @SubscribeEvent
    public void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(Main.GHOST, GhostPlayerEntity.getGhostAttributes());
    }

}
