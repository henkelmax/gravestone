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
        model = playerModel;

        addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1F)));
        addLayer(new HeldItemLayer<>(this));
        addLayer(new HeadLayer<>(this));
        addLayer(new ElytraLayer<>(this));
    }

    @Override
    protected void scale(GhostPlayerEntity ghost, MatrixStack matrixStack, float partialTickTime) {
        float scale = 0.9375F;
        matrixStack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(GhostPlayerEntity entity) {
        return PlayerSkins.getSkin(entity.getPlayerUUID(), entity.getName().getString());
    }

    @Override
    public void render(GhostPlayerEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.pushPose();

        if (PlayerSkins.isSlim(entity.getPlayerUUID())) {
            model = playerModelSmallArms;
        } else {
            model = playerModel;
        }
        setModelVisibilities(entity);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);

        matrixStack.popPose();
    }

    private void setModelVisibilities(GhostPlayerEntity playerEntity) {
        model.hat.visible = playerEntity.isWearing(PlayerModelPart.HAT);
        model.jacket.visible = playerEntity.isWearing(PlayerModelPart.JACKET);
        model.leftPants.visible = playerEntity.isWearing(PlayerModelPart.LEFT_PANTS_LEG);
        model.rightPants.visible = playerEntity.isWearing(PlayerModelPart.RIGHT_PANTS_LEG);
        model.leftSleeve.visible = playerEntity.isWearing(PlayerModelPart.LEFT_SLEEVE);
        model.rightSleeve.visible = playerEntity.isWearing(PlayerModelPart.RIGHT_SLEEVE);
    }

}
