package de.maxhenkel.gravestone.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.client.PlayerSkins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;

public class PlayerGhostRenderer extends LivingEntityRenderer<GhostPlayerEntity, PlayerModel<GhostPlayerEntity>> {

    private PlayerModel<GhostPlayerEntity> playerModel;
    private PlayerModel<GhostPlayerEntity> playerModelSmallArms;

    public PlayerGhostRenderer(EntityRendererProvider.Context renderer) {
        super(renderer, null, 0.5F);
        playerModel = new PlayerModel<>(renderer.bakeLayer(ModelLayers.PLAYER), false);
        playerModelSmallArms = new PlayerModel<>(renderer.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        model = playerModel;

        addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(renderer.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(renderer.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), Minecraft.getInstance().getModelManager()));
        this.addLayer(new CustomHeadLayer<>(this, renderer.getModelSet(), 1F, 1F, 1F, Minecraft.getInstance().gameRenderer.itemInHandRenderer));
        this.addLayer(new ElytraLayer<>(this, renderer.getModelSet()));
        this.addLayer(new ItemInHandLayer<>(this, Minecraft.getInstance().gameRenderer.itemInHandRenderer));
    }

    @Override
    protected void scale(GhostPlayerEntity ghost, PoseStack matrixStack, float partialTickTime) {
        float scale = 0.9375F;
        matrixStack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(GhostPlayerEntity entity) {
        return PlayerSkins.getSkin(entity.getPlayerUUID(), entity.getName().getString());
    }

    @Override
    public void render(GhostPlayerEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
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
