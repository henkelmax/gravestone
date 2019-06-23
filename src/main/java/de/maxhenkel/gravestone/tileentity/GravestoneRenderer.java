package de.maxhenkel.gravestone.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.util.PlayerSkins;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import de.maxhenkel.gravestone.Config;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;

import java.util.UUID;

public class GravestoneRenderer extends TileEntityRenderer<GraveStoneTileEntity> {

    public GravestoneRenderer() {

    }

    @Override
    public void render(GraveStoneTileEntity target, double x, double y, double z, float partialTicks, int destroyStage) {
        String name = target.getPlayerName();

        if (name == null || name.isEmpty()) {
            return;
        }

        Direction direction = target.getBlockState().get(GraveStoneBlock.FACING);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5D, y + 1.0D, z + 0.5D);
        GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);

        GlStateManager.rotatef(-getDirection(direction), 0.0F, 1.0F, 0.0F);

        FontRenderer renderer = getFontRenderer();

        if (renderer != null) {
            int textWidth = renderer.getStringWidth(name);
            float textScale = 0.8F / textWidth;
            textScale = Math.min(textScale, 0.02F);

            GlStateManager.translatef(-(textScale * textWidth) / 2.0F, 0.3F, 0.37F);

            GlStateManager.scalef(textScale, textScale, textScale);

            GlStateManager.depthMask(false);
            renderer.drawString(name, 0, 0, 0);
            GlStateManager.depthMask(true);
        }

        GlStateManager.popMatrix();

        boolean render = false;

        BlockState state = target.getWorld().getBlockState(target.getPos().down());
        if (state == null) {
            return;
        }

        Block block = state.getBlock();
        if (block == null) {
            return;
        }

        if (block.isNormalCube(state, target.getWorld(), target.getPos().down())) {//is opaque
            render = true;
        }

        if (target.renderHead() && target.getPlayerUUID() != null && !target.getPlayerUUID().isEmpty() && Config.renderSkull && render) {
            try {
                renderSkull(x, y, z, target.getPlayerUUID(), target.getPlayerName(), direction);
            } catch (Exception e) {
            }
        }
    }

    public void renderSkull(double x, double y, double z, String uuid, String name, Direction rotation) {
        HumanoidHeadModel model = new HumanoidHeadModel();
        ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();

        if (uuid != null) {
            try {
                resourcelocation = PlayerSkins.getSkin(UUID.fromString(uuid), name);
            } catch (Exception e) {
            }
        }

        this.bindTexture(resourcelocation);

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        GlStateManager.translated(x + 0.5D, y, z + 0.5D);

        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlphaTest();//enableAlpha

        GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);

        float yaw = 26F;
        float pitch = -61.0F;
        float scale = 0.0625F;

        GlStateManager.rotatef(180 - getDirection(rotation), 0.0F, 1.0F, 0.0F);

        GlStateManager.translatef(0.0F, 0.14F, -0.18F);

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        model.func_217104_a(0.0F, 0.0F, 0.0F, yaw, pitch, scale);

        GlStateManager.popMatrix();
    }

    private int getDirection(Direction facing) {
        switch (facing) {
            case EAST:
                return 270;
            case SOUTH:
                return 180;
            case WEST:
                return 90;
            case NORTH:
            default:
                return 0;
        }
    }

}
