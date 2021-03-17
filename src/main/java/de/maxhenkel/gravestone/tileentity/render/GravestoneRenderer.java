package de.maxhenkel.gravestone.tileentity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.corelib.client.PlayerSkins;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

import java.util.UUID;

public class GravestoneRenderer extends TileEntityRenderer<GraveStoneTileEntity> {

    public GravestoneRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(GraveStoneTileEntity grave, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ITextComponent name = grave.getGraveName();
        if (name == null) {
            return;
        }

        Direction direction = grave.getBlockState().getValue(GraveStoneBlock.FACING);

        matrixStack.pushPose();
        matrixStack.translate(0.5D, 1D, 0.5D);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F + direction.toYRot()));

        FontRenderer font = renderer.getFont();

        int textWidth = font.width(name.getString());
        double textScale = Math.min(0.8D / textWidth, 0.02D);

        matrixStack.translate(0D, 0.3D, 0.37D);
        matrixStack.scale((float) textScale, (float) textScale, (float) textScale);
        float left = (float) (-textWidth / 2);
        font.drawInBatch(name.getString(), left, 0F, Main.CLIENT_CONFIG.graveTextColor, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
        matrixStack.popPose();

        BlockState state = grave.getLevel().getBlockState(grave.getBlockPos().below());

        boolean render = state.isRedstoneConductor(grave.getLevel(), grave.getBlockPos()); //TODO fix with slime block
        UUID playerUUID = grave.getDeath().getPlayerUUID();
        if (playerUUID != null && !playerUUID.equals(GraveUtils.EMPTY_UUID) && Main.CLIENT_CONFIG.renderSkull.get() && render) {
            renderSkull(playerUUID, name.getString(), direction, matrixStack, buffer, combinedLight);
        }
    }

    public void renderSkull(UUID uuid, String name, Direction rotation, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight) {
        HumanoidHeadModel model = new HumanoidHeadModel();
        ResourceLocation resourcelocation = PlayerSkins.getSkin(uuid, name);

        matrixStack.pushPose();

        matrixStack.translate(0.5D, 0D, 0.5D);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180F - rotation.toYRot()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-26F));
        matrixStack.translate(0D, -0.14D, 0.18D);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180F));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-61F));

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        model.renderToBuffer(matrixStack, buffer.getBuffer(model.renderType(resourcelocation)), combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.popPose();
    }
}
