package de.maxhenkel.gravestone.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.client.PlayerSkins;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.player.PlayerModelType;

public class PlayerGhostRenderer extends LivingEntityRenderer<GhostPlayerEntity, AvatarRenderState, PlayerModel> {

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
        addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache()));
        addLayer(new WingsLayer<>(this, context.getModelSet(), context.getEquipmentRenderer()));
        addLayer(new ParrotOnShoulderLayer(this, context.getModelSet()));
        addLayer(new SpinAttackEffectLayer(this, context.getModelSet()));
        addLayer(new BeeStingerLayer<>(this, context));
    }

    @Override
    public void submit(AvatarRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        super.submit(state, stack, collector, cameraRenderState);

        if (state.skin.model().equals(PlayerModelType.SLIM)) {
            model = playerModelSlim;
        } else {
            model = playerModel;
        }
    }

    @Override
    public Identifier getTextureLocation(AvatarRenderState renderState) {
        return renderState.skin.body().texturePath();
    }

    @Override
    protected boolean shouldShowName(GhostPlayerEntity entity, double d) {
        return false;
    }

    @Override
    public AvatarRenderState createRenderState() {
        return new AvatarRenderState();
    }

    @Override
    public void extractRenderState(GhostPlayerEntity entity, AvatarRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTicks, itemModelResolver);
        state.skin = PlayerSkins.getSkin(entity.getPlayerUUID());
        state.showHat = entity.isWearing(PlayerModelPart.HAT);
        state.showJacket = entity.isWearing(PlayerModelPart.JACKET);
        state.showLeftPants = entity.isWearing(PlayerModelPart.LEFT_PANTS_LEG);
        state.showRightPants = entity.isWearing(PlayerModelPart.RIGHT_PANTS_LEG);
        state.showLeftSleeve = entity.isWearing(PlayerModelPart.LEFT_SLEEVE);
        state.showRightSleeve = entity.isWearing(PlayerModelPart.RIGHT_SLEEVE);
        state.showCape = entity.isWearing(PlayerModelPart.CAPE);
        state.id = entity.getId();
    }

    @Override
    protected void scale(AvatarRenderState state, PoseStack stack) {
        float scale = 0.9375F;
        stack.scale(scale, scale, scale);
    }
}
