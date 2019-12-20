package de.maxhenkel.gravestone.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.util.PlayerSkins;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class GravestoneRenderer extends TileEntityRenderer<GraveStoneTileEntity> {


    public GravestoneRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void func_225616_a_(GraveStoneTileEntity target, float f1, MatrixStack matrixStack, IRenderTypeBuffer buffer, int i1, int i2) {
        String name = target.getPlayerName();

        if (name == null || name.isEmpty()) {
            return;
        }

        Direction direction = target.getBlockState().get(GraveStoneBlock.FACING);

        matrixStack.func_227860_a_();

        matrixStack.func_227861_a_(0.5D, 1D, 0.5D);

        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(180F));

        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180F + direction.getHorizontalAngle()));

        FontRenderer renderer = field_228858_b_.getFontRenderer();

        int textWidth = renderer.getStringWidth(name);
        double textScale = Math.min(0.8D / textWidth, 0.02D);

        matrixStack.func_227861_a_(0D, 0.3D, 0.37D);

        matrixStack.func_227862_a_((float) textScale, (float) textScale, (float) textScale);

        renderer.drawString(name, 0, 0, 0);

        float left = (float) (-renderer.getStringWidth(name) / 2);
        renderer.func_228079_a_(name, left, 0F, 0x000000, false, matrixStack.func_227866_c_().func_227870_a_(), buffer, false, 0, i1);

        matrixStack.func_227865_b_();

        BlockState state = target.getWorld().getBlockState(target.getPos().down());
        if (state == null) {
            return;
        }

        Block block = state.getBlock();
        if (block == null) {
            return;
        }

        boolean render = block.isNormalCube(state, target.getWorld(), target.getPos().down());//is opaque

        if (target.renderHead() && target.getPlayerUUID() != null && !target.getPlayerUUID().isEmpty() && Config.renderSkull && render) {
            try {
                renderSkull(target.getPlayerUUID(), target.getPlayerName(), direction, matrixStack, buffer);
            } catch (Exception e) {
            }
        }
    }

    public void renderSkull(String uuid, String name, Direction rotation, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        HumanoidHeadModel model = new HumanoidHeadModel();
        ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();

        if (uuid != null) {
            try {
                resourcelocation = PlayerSkins.getSkin(UUID.fromString(uuid), name);
            } catch (Exception e) {
            }
        }

        matrixStack.func_227860_a_();

        matrixStack.func_227861_a_(0.5D, 0D, 0.5D);

        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180F - rotation.getHorizontalAngle()));
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-26F));
        matrixStack.func_227861_a_(0D, -0.14D, 0.18D);
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(180F));

        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-61F));

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        model.func_225598_a_(matrixStack, buffer.getBuffer(model.func_228282_a_(resourcelocation)), 15728880, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.func_227865_b_();
    }
}
