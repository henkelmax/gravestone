package de.maxhenkel.gravestone.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.client.PlayerSkins;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.ResourceLocation;

public class PlayerGhostRenderer extends LivingRenderer<GhostPlayerEntity, PlayerModel<GhostPlayerEntity>> {

    private PlayerModel<GhostPlayerEntity> playerModel;
    private PlayerModel<GhostPlayerEntity> playerModelSmallArms;

    public PlayerGhostRenderer(EntityRendererManager renderManager) {
        super(renderManager, null, 0.5F);
        playerModel = new PlayerModel<>(0F, false);
        playerModelSmallArms = new PlayerModel<>(0F, true);
        entityModel = playerModel;

        addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1F)));
        addLayer(new HeldItemLayer<>(this));
        addLayer(new HeadLayer<>(this));
        addLayer(new ElytraLayer<>(this));
    }

    @Override
    protected void preRenderCallback(GhostPlayerEntity ghost, MatrixStack matrixStack, float partialTickTime) {
        float scale = 0.9375F;
        matrixStack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getEntityTexture(GhostPlayerEntity entity) {
        return PlayerSkins.getSkin(entity.getPlayerUUID(), entity.getCustomName().getString());
    }

    @Override
    public void render(GhostPlayerEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        packedLight = 0xFFFFFF;
        matrixStack.push();

        if (PlayerSkins.isSlim(entity.getPlayerUUID())) {
            entityModel = playerModelSmallArms;
        } else {
            entityModel = playerModel;
        }
        setModelVisibilities(entity);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);

        matrixStack.pop();
    }

    private void setModelVisibilities(GhostPlayerEntity playerEntity) {
        entityModel.bipedHeadwear.showModel = playerEntity.isWearing(PlayerModelPart.HAT);
        entityModel.bipedBodyWear.showModel = playerEntity.isWearing(PlayerModelPart.JACKET);
        entityModel.bipedLeftLegwear.showModel = playerEntity.isWearing(PlayerModelPart.LEFT_PANTS_LEG);
        entityModel.bipedRightLegwear.showModel = playerEntity.isWearing(PlayerModelPart.RIGHT_PANTS_LEG);
        entityModel.bipedLeftArmwear.showModel = playerEntity.isWearing(PlayerModelPart.LEFT_SLEEVE);
        entityModel.bipedRightArmwear.showModel = playerEntity.isWearing(PlayerModelPart.RIGHT_SLEEVE);
    }

}
