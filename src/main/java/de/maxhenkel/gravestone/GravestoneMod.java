package de.maxhenkel.gravestone;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.commands.RestoreCommand;
import de.maxhenkel.gravestone.entity.GhostPlayerEntity;
import de.maxhenkel.gravestone.events.CreativeTabEvents;
import de.maxhenkel.gravestone.events.DeathEvents;
import de.maxhenkel.gravestone.items.ObituaryItem;
import de.maxhenkel.gravestone.net.MessageOpenObituary;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GravestoneMod.MODID)
@EventBusSubscriber(modid = GravestoneMod.MODID)
public class GravestoneMod {

    public static final String MODID = "gravestone";

    public static final Logger LOGGER = LogManager.getLogger(GravestoneMod.MODID);

    private static final DeferredRegister.Blocks BLOCK_REGISTER = DeferredRegister.createBlocks(GravestoneMod.MODID);
    public static final DeferredHolder<Block, GraveStoneBlock> GRAVESTONE = BLOCK_REGISTER.registerBlock("gravestone", GraveStoneBlock::new, BlockBehaviour.Properties::of);

    private static final DeferredRegister.Items ITEM_REGISTER = DeferredRegister.createItems(GravestoneMod.MODID);
    public static final DeferredHolder<Item, BlockItem> GRAVESTONE_ITEM = ITEM_REGISTER.registerSimpleBlockItem(GRAVESTONE);
    public static final DeferredHolder<Item, ObituaryItem> OBITUARY = ITEM_REGISTER.registerItem("obituary", ObituaryItem::new);

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GravestoneMod.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GraveStoneTileEntity>> GRAVESTONE_TILEENTITY = BLOCK_ENTITY_REGISTER.register("gravestone", () -> new BlockEntityType<>(GraveStoneTileEntity::new, GRAVESTONE.get()));

    private static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, GravestoneMod.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<GhostPlayerEntity>> GHOST = ENTITY_REGISTER.register("player_ghost", () ->
            CommonRegistry.registerEntity(GravestoneMod.MODID, "player_ghost", MobCategory.MONSTER, GhostPlayerEntity.class, builder -> builder.sized(0.6F, 1.95F))
    );

    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPE_REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GravestoneMod.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DeathInfo>> DEATH_DATA_COMPONENT = DATA_COMPONENT_TYPE_REGISTER.registerComponentType("death", (b) -> b.persistent(DeathInfo.CODEC).networkSynchronized(DeathInfo.STREAM_CODEC));

    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    public GravestoneMod(IEventBus eventBus) {
        eventBus.addListener(CreativeTabEvents::onCreativeModeTabBuildContents);

        SERVER_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.SERVER, ServerConfig.class, true);
        CLIENT_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.CLIENT, ClientConfig.class, true);

        BLOCK_REGISTER.register(eventBus);
        ITEM_REGISTER.register(eventBus);
        BLOCK_ENTITY_REGISTER.register(eventBus);
        ENTITY_REGISTER.register(eventBus);
        DATA_COMPONENT_TYPE_REGISTER.register(eventBus);
    }

    @SubscribeEvent
    static void commonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(new DeathEvents());
    }


    @SubscribeEvent
    static void onRegisterCommands(RegisterCommandsEvent event) {
        RestoreCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID).versioned("0");
        CommonRegistry.registerMessage(registrar, MessageOpenObituary.class);
    }

    @SubscribeEvent
    static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(GravestoneMod.GHOST.get(), GhostPlayerEntity.getGhostAttributes());
    }

}
