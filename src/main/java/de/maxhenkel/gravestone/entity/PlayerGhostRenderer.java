package de.maxhenkel.gravestone.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.gravestone.util.PlayerSkins;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class PlayerGhostRenderer extends LivingRenderer<GhostPlayerEntity, PlayerModel<GhostPlayerEntity>> {

    public PlayerGhostRenderer(EntityRendererManager renderManager) {
        this(renderManager, true);
    }

    public PlayerGhostRenderer(EntityRendererManager renderManager, boolean useSmallArms) {
        super(renderManager, new PlayerModel<>(0.0F, useSmallArms), 0.5F);
    }

    @Override
    public void func_225623_a_(GhostPlayerEntity entity, float f1, float f2, MatrixStack matrixStack, IRenderTypeBuffer buffer, int i) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.25F);
        super.func_225623_a_(entity, f1, f2, matrixStack, buffer, 0xFFFFFF);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(GhostPlayerEntity entityGhostPlayer) {
        return PlayerSkins.getSkin(entityGhostPlayer.getUniqueID(), entityGhostPlayer.getName().getUnformattedComponentText());
    }

}
