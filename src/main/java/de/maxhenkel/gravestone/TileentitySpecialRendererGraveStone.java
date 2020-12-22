package de.maxhenkel.gravestone;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileentitySpecialRendererGraveStone extends TileEntitySpecialRenderer {

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

	@Override
	public void renderTileEntityAt(TileEntity target, double x, double y, double z, float p_180535_8_, int p_180535_9_) {
		if(!(target instanceof TileEntityGraveStone)){
			return;
		}
		
		String name = ((TileEntityGraveStone)target).getPlayerName();

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

	}

}
