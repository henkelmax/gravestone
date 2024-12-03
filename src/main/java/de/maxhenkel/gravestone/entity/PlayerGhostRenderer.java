package de.maxhenkel.gravestone.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.client.PlayerSkins;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;

public class PlayerGhostRenderer extends LivingEntityRenderer<GhostPlayerEntity, PlayerRenderState, PlayerModel> {

    private final PlayerModel playerModel;
    private final PlayerModel playerModelSlim;

    public PlayerGhostRenderer(EntityRendererProvider.Context context) {
        super(context, null, 0.5F);
        playerModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
        playerModelSlim = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        model = playerModel;

        addLayer(new PlayerItemInHandLayer<>(this));
        addLayer(new ArrowLayer<>(this, context));
        addLayer(new Deadmau5EarsLayer(this, context.getModelSet()));
        addLayer(new CapeLayer(this, context.getModelSet(), context.getEquipmentAssets()));
        addLayer(new CustomHeadLayer<>(this, context.getModelSet(), CustomHeadLayer.Transforms.DEFAULT));
        addLayer(new WingsLayer<>(this, context.getModelSet(), context.getEquipmentRenderer()));
        addLayer(new ParrotOnShoulderLayer(this, context.getModelSet()));
        addLayer(new SpinAttackEffectLayer(this, context.getModelSet()));
        addLayer(new BeeStingerLayer<>(this, context));
    }

    @Override
    public void render(PlayerRenderState state, PoseStack stack, MultiBufferSource source, int light) {
        if (state.skin.model().equals(PlayerSkin.Model.SLIM)) {
            model = playerModelSlim;
        } else {
            model = playerModel;
        }
        super.render(state, stack, source, light);
    }

    @Override
    protected boolean shouldShowName(GhostPlayerEntity entity, double d) {
        return false;
    }

    @Override
    public PlayerRenderState createRenderState() {
        return new PlayerRenderState();
    }

    @Override
    public void extractRenderState(GhostPlayerEntity entity, PlayerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTicks, itemModelResolver);
        state.skin = PlayerSkins.getSkin(entity.getPlayerUUID(), entity.getName().getString());
        state.showHat = entity.isWearing(PlayerModelPart.HAT);
        state.showJacket = entity.isWearing(PlayerModelPart.JACKET);
        state.showLeftPants = entity.isWearing(PlayerModelPart.LEFT_PANTS_LEG);
        state.showRightPants = entity.isWearing(PlayerModelPart.RIGHT_PANTS_LEG);
        state.showLeftSleeve = entity.isWearing(PlayerModelPart.LEFT_SLEEVE);
        state.showRightSleeve = entity.isWearing(PlayerModelPart.RIGHT_SLEEVE);
        state.showCape = entity.isWearing(PlayerModelPart.CAPE);
        state.id = entity.getId();
        state.name = entity.getName().getString();
    }

    @Override
    protected void scale(PlayerRenderState state, PoseStack stack) {
        float scale = 0.9375F;
        stack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerRenderState state) {
        return state.skin.texture();
    }
}
