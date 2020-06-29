package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.entity.GhostPlayerEntity;
import de.maxhenkel.gravestone.entity.PlayerGhostRenderer;
import de.maxhenkel.gravestone.events.BlockEvents;
import de.maxhenkel.gravestone.events.DeathEvents;
import de.maxhenkel.gravestone.items.DeathInfoItem;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import de.maxhenkel.gravestone.tileentity.GravestoneRenderer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Main.MODID)
@Mod(Main.MODID)
public class Main {

    public static final String MODID = "gravestone";

    public static final Logger LOGGER = LogManager.getLogger(Main.MODID);

    public static GraveStoneBlock GRAVESTONE;

    public static Item GRAVESTONE_ITEM;

    public static TileEntityType<GraveStoneTileEntity> GRAVESTONE_TILEENTITY;

    public static DeathInfoItem DEATHINFO;

    public static EntityType<GhostPlayerEntity> GHOST;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::registerTileEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::registerEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::configEvent);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            clientStart();
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void clientStart() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup);
    }

    @SubscribeEvent
    public void configEvent(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            Config.loadServer();
        } else if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
            Config.loadClient();
        }
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new DeathEvents());
        MinecraftForge.EVENT_BUS.register(new BlockEvents());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(GRAVESTONE_TILEENTITY, GravestoneRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(GHOST, manager -> new PlayerGhostRenderer(manager));
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
                DEATHINFO = new DeathInfoItem()
        );
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        GRAVESTONE_TILEENTITY = TileEntityType.Builder.create(GraveStoneTileEntity::new, GRAVESTONE).build(null);
        GRAVESTONE_TILEENTITY.setRegistryName(new ResourceLocation(MODID, "gravestone"));
        event.getRegistry().register(GRAVESTONE_TILEENTITY);
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        GHOST = EntityType.Builder.<GhostPlayerEntity>create(GhostPlayerEntity::new, EntityClassification.MONSTER)
                .size(0.6F, 1.95F)
                .build(Main.MODID + ":player_ghost");
        GHOST.setRegistryName(new ResourceLocation(Main.MODID, "player_ghost"));
        event.getRegistry().register(GHOST);

        GlobalEntityTypeAttributes.put(GHOST, GhostPlayerEntity.getAttributes().func_233813_a_());
    }
}
