package de.maxhenkel.gravestone.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.corelib.client.PlayerSkins;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.GravestoneMod;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GravestoneRenderer implements BlockEntityRenderer<GraveStoneTileEntity, GravestoneRenderState> {

    private final BlockEntityRendererProvider.Context renderer;

    public GravestoneRenderer(BlockEntityRendererProvider.Context renderer) {
        this.renderer = renderer;
    }

    @Override
    public GravestoneRenderState createRenderState() {
        return new GravestoneRenderState();
    }

    @Override
    public void extractRenderState(GraveStoneTileEntity grave, GravestoneRenderState state, float partialTicks, Vec3 pos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(grave, state, partialTicks, pos, crumblingOverlay);
        Component graveName = grave.getGraveName();
        state.name = graveName == null ? null : graveName.getVisualOrderText();
        state.direction = grave.getBlockState().getValue(GraveStoneBlock.FACING);

        BlockState blockState = grave.getLevel().getBlockState(grave.getBlockPos().below());
        //TODO fix with slime block
        state.renderHead = blockState.isRedstoneConductor(grave.getLevel(), grave.getBlockPos());

        state.playerId = grave.getDeath().getPlayerUUID();
    }

    @Override
    public void submit(GravestoneRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (state.name == null) {
            return;
        }

        stack.pushPose();
        stack.translate(0.5D, 1D, 0.5D);
        stack.mulPose(Axis.XP.rotationDegrees(180F));
        stack.mulPose(Axis.YP.rotationDegrees(180F + state.direction.toYRot()));

        Font font = renderer.font();

        int textWidth = font.width(state.name);
        double textScale = Math.min(0.8D / textWidth, 0.02D);

        stack.translate(0D, 0.3D, 0.37D);
        stack.scale((float) textScale, (float) textScale, (float) textScale);
        float left = (float) (-textWidth / 2);
        collector.submitText(stack, left, 0F, state.name, false, Font.DisplayMode.NORMAL, state.lightCoords, 0xFFFFFFFF, 0, 0);
        stack.popPose();

        if (state.renderHead && state.playerId != null && !state.playerId.equals(GraveUtils.EMPTY_UUID) && GravestoneMod.CLIENT_CONFIG.renderSkull.get()) {
            renderSkull(collector, state, stack);
        }
    }

    public void renderSkull(SubmitNodeCollector collector, GravestoneRenderState state, PoseStack stack) {
        SkullModel model = new SkullModel(renderer.bakeLayer(ModelLayers.PLAYER_HEAD));
        Identifier resourcelocation = PlayerSkins.getSkin(state.playerId).body().texturePath();

        stack.pushPose();

        stack.translate(0.5D, 0D, 0.5D);

        stack.mulPose(Axis.YP.rotationDegrees(180F - state.direction.toYRot()));
        stack.mulPose(Axis.YP.rotationDegrees(-26F));
        stack.translate(0D, -0.14D, 0.18D);
        stack.mulPose(Axis.XP.rotationDegrees(180F));
        stack.mulPose(Axis.XP.rotationDegrees(-61F));

        int light = state.lightCoords;
        collector.submitCustomGeometry(stack, model.renderType(resourcelocation), (pose, vertexConsumer) -> {
            PoseStack s = new PoseStack();
            s.mulPose(pose.pose());
            model.renderToBuffer(s, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        });

        stack.popPose();
    }
}
