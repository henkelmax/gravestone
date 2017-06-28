package de.maxhenkel.gravestone.tileentity;

import java.util.UUID;
import org.lwjgl.opengl.GL11;

import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.util.PlayerSkins;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TileentitySpecialRendererGraveStone extends TileEntitySpecialRenderer<TileEntityGraveStone> {

	private boolean renderSkull;

	public TileentitySpecialRendererGraveStone() {
		this.renderSkull = Config.renderSkull;
	}

	@Override
	public void renderTileEntityAt(TileEntityGraveStone target, double x, double y, double z, float partialTicks, int destroyStage) {
		String name = target.getPlayerName();

		if (name == null || name.isEmpty()) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.0F, (float) z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

		GL11.glRotatef(-getDirection(target.getBlockMetadata()), 0.0F, 1.0F, 0.0F);

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

		IBlockState state = target.getWorld().getBlockState(target.getPos().down());
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

		if (target.renderHead() && target.getPlayerUUID() != null && !target.getPlayerUUID().isEmpty() && renderSkull && render) {
			try{
				renderSkull(x, y, z, target.getPlayerUUID(), target.getPlayerName(), target.getBlockMetadata());
			}catch(Exception e){}
		}
	}
	
	public void renderSkull(double x, double y, double z, String uuid, String name, int rotation) {

		ModelBase modelbase = new ModelHumanoidHead();
		ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();
		
		if (uuid != null) {
			try{
				resourcelocation=PlayerSkins.getSkin(UUID.fromString(uuid), name);
			}catch(Exception e){}
		}

		this.bindTexture(resourcelocation);

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();

		GlStateManager.translate(x + 0.5F, y, z + 0.5F);

		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlpha();

		GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);

		float yaw = 26F;
		float pitch = -61.0F;
		float scale = 0.0625F;

		GL11.glRotatef(180 - getDirection(rotation), 0.0F, 1.0F, 0.0F);

		GL11.glTranslatef(0.0F, 0.14F, -0.18F);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		modelbase.render((Entity) null, 0.0F, 0.0F, 0.0F, yaw, pitch, scale);

		GlStateManager.popMatrix();

	}

	private int getDirection(int i) {
		switch (i) {
		case 2:
			return 0;
		case 4:
			return 90;
		case 3:
			return 180;
		case 5:
			return 270;
		default:
			return 0;
		}
	}

}
