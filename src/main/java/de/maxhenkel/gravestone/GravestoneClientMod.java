package de.maxhenkel.gravestone;

import de.maxhenkel.gravestone.entity.PlayerGhostRenderer;
import de.maxhenkel.gravestone.tileentity.render.GravestoneRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = GravestoneMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GravestoneMod.MODID, value = Dist.CLIENT)
public class GravestoneClientMod {

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(GravestoneMod.GRAVESTONE_TILEENTITY.get(), GravestoneRenderer::new);
        EntityRenderers.register(GravestoneMod.GHOST.get(), PlayerGhostRenderer::new);
    }

}
