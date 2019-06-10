package de.maxhenkel.gravestone.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import de.maxhenkel.gravestone.util.PlayerSkins;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class PlayerGhostRenderer extends LivingRenderer<GhostPlayerEntity, PlayerModel<GhostPlayerEntity>> {

    public PlayerGhostRenderer(EntityRendererManager renderManager) {
        this(renderManager, true);
    }

    public PlayerGhostRenderer(EntityRendererManager renderManager, boolean useSmallArms) {
        super(renderManager, new PlayerModel<>(0.0F, useSmallArms), 0.5F);
        this.addLayer(new BipedArmorLayer(this, new BipedModel(0.5F), new BipedModel(1.0F)));
        this.addLayer(new HeldItemLayer(this));
        this.addLayer(new ArrowLayer(this));
        this.addLayer(new ElytraLayer(this));
    }

    public void doRender(GhostPlayerEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.enableBlend();
        GlStateManager.enableCull();

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.25F);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);

        GlStateManager.unsetProfile(GlStateManager.Profile.PLAYER_SKIN);
    }

    @Override
    public void renderName(GhostPlayerEntity entity, double x, double y, double z) {
        if (entity.getAlwaysRenderNameTagForRender()) {
            super.renderName(entity, x, y, z);
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(GhostPlayerEntity entityGhostPlayer) {
        return PlayerSkins.getSkin(entityGhostPlayer.getUniqueID(), entityGhostPlayer.getName().getUnformattedComponentText());
    }

}
