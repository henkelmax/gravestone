package de.maxhenkel.gravestone.entity;

import de.maxhenkel.gravestone.util.PlayerSkins;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerGhost extends RenderLivingBase<EntityGhostPlayer> {

    public RenderPlayerGhost(RenderManager renderManager) {
        this(renderManager, true);
    }

    public RenderPlayerGhost(RenderManager renderManager, boolean useSmallArms) {
        super(renderManager, new ModelPlayer(0.0F, useSmallArms), 0.5F);
        this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerElytra(this));
    }

    public void doRender(EntityGhostPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.enableBlend();
        GlStateManager.enableCull();

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.25F);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);

        GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
    }

    @Override
    public void renderName(EntityGhostPlayer entity, double x, double y, double z) {
        if (entity.getAlwaysRenderNameTagForRender()) {
            super.renderName(entity, x, y, z);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGhostPlayer entity) {
        return PlayerSkins.getSkin(entity.getPlayerUUID(), entity.getName().getUnformattedComponentText());
    }

}
