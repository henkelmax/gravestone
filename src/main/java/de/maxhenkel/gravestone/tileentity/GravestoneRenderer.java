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
import org.lwjgl.opengl.GL11;
import de.maxhenkel.gravestone.Config;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;

import java.util.UUID;

public class GravestoneRenderer extends TileEntityRenderer<GraveStoneTileEntity> {

    private boolean renderSkull;

    public GravestoneRenderer() {
        this.renderSkull = Config.renderSkull;
    }

    @Override
    public void render(GraveStoneTileEntity target, double x, double y, double z, float partialTicks, int destroyStage) {
        String name = target.getPlayerName();
        System.out.println("123");
        if (name == null || name.isEmpty()) {
            return;
        }

        Direction facing = target.getBlockState().get(GraveStoneBlock.FACING);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.0F, (float) z + 0.5F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

        GL11.glRotatef(-getDirection(facing), 0.0F, 1.0F, 0.0F);

        FontRenderer renderer = getFontRenderer();

        if (renderer != null) {
            int textWidth = renderer.getStringWidth(name);
            float textScale = 0.8F / textWidth;
            textScale = Math.min(textScale, 0.02F);

            GL11.glTranslatef(-(textScale * textWidth) / 2.0F, 0.3F, 0.37F);

            GL11.glScalef(textScale, textScale, textScale);

            GL11.glDepthMask(false);
            renderer.drawString(name, 0, 0, 0);
            GL11.glDepthMask(true);
        }

        GL11.glPopMatrix();

        boolean render = false;

        BlockState state = target.getWorld().getBlockState(target.getPos().down());
        if (state == null) {
            return;
        }

        Block block = state.getBlock();
        if (block == null) {
            return;
        }

        if (block.func_220081_d(state, target.getWorld(), target.getPos().down())) {//is opaque
            render = true;
        }

        if (target.renderHead() && target.getPlayerUUID() != null && !target.getPlayerUUID().isEmpty() && renderSkull && render) {
            try {
                renderSkull(x, y, z, target.getPlayerUUID(), target.getPlayerName(), facing);
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

        GL11.glRotatef(180 - getDirection(rotation), 0.0F, 1.0F, 0.0F);

        GL11.glTranslatef(0.0F, 0.14F, -0.18F);

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
