package de.maxhenkel.gravestone.tileentity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.util.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.terraingen.BiomeEvent.GetWaterColor;

public class TileentitySpecialRendererGraveStone extends TileEntitySpecialRenderer {

	private boolean renderSkull;
	//private HashMap<String, GameProfile> players;
	private ModelGraveBase graveModel;
	private ModelGraveStone graveModelStone;

	public TileentitySpecialRendererGraveStone() {
		this.renderSkull = Config.instance().renderSkull;
		//this.players = new HashMap<String, GameProfile>();
		this.graveModel = new ModelGraveBase();
		this.graveModelStone = new ModelGraveStone();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {

		if (!(te instanceof TileEntityGraveStone)) {
			return;
		}

		TileEntityGraveStone target = (TileEntityGraveStone) te;

		// Model
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glRotatef(getDirection(target.getBlockMetadata()), 0.0F, 1F, 0.0F);

		bindTexture(new ResourceLocation("minecraft:textures/blocks/dirt.png"));
		graveModel.render((Entity) null, 0, -0.1F, 0, 0, 0, 0.0625F);

		bindTexture(new ResourceLocation("minecraft:textures/blocks/cobblestone.png"));
		graveModelStone.render((Entity) null, 0, -0.1F, 0, 0, 0, 0.0625F);

		GL11.glPopMatrix();

		// Text

		String name = target.getPlayerName();

		if (name == null || name.isEmpty()) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.0F, (float) z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

		GL11.glRotatef(-getDirection(target.getBlockMetadata()), 0.0F, 1.0F, 0.0F);

		FontRenderer renderer = func_147498_b();

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

		BlockPos pos=new BlockPos(te.xCoord, te.yCoord, te.zCoord).down();
		
		Block block = target.getWorldObj().getBlock(pos.getX(), pos.getY(), pos.getZ());
		
		if (block == null) {
			return;
		}
		
		if (block.isOpaqueCube()) {
			render = true;
		}

		if (target.renderHead() && target.getPlayerUUID() != null && !target.getPlayerUUID().isEmpty() && renderSkull
				&& render) {
			try {
				//renderSkull(x, y, z, partialTicks, new GameProfile(UUID.fromString(target.getPlayerUUID()), target.getPlayerName()), target.getBlockMetadata());
			} catch (Exception e) {
			}
		}
	}

	public void renderSkull(double x, double y, double z, float delta, GameProfile gameProfile, int rotation) {
		ModelBase modelSkeletonHead = new ModelHumanoidHead();
		ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;

		if (gameProfile != null) {
			Minecraft minecraft = Minecraft.getMinecraft();
			Map map = minecraft.func_152342_ad().func_152788_a(gameProfile);

			if (map.containsKey(Type.SKIN)) {
				resourcelocation = minecraft.func_152342_ad()
						.func_152792_a((MinecraftProfileTexture) map.get(Type.SKIN), Type.SKIN);
			}
		}


		GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
		
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		GL11.glTranslated(x + 0.5F, y, z + 0.5F);

		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		float yaw = 26F;
		float pitch = -61.0F;
		float scale = 0.0625F;

		GL11.glRotatef(180 - getDirection(rotation), 0.0F, 1.0F, 0.0F);

		GL11.glTranslatef(0.0F, 0.14F, -0.18F);
		
		this.bindTexture(resourcelocation);
		modelSkeletonHead.render((Entity) null, 0.0F, 0.0F, 0.0F, yaw, pitch, scale);

		GL11.glPopMatrix();
	}

	/*public void renderSkull(double x, double y, double z, String uuid, String name, int rotation) {

		ModelBase modelbase = new ModelHumanoidHead();
		ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();

		if (uuid != null) {
			GameProfile profile = getGameProfile(uuid, name);
			Minecraft minecraft = Minecraft.getMinecraft();
			Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

			if (map.containsKey(Type.SKIN)) {
				resourcelocation = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture) map.get(Type.SKIN),
						Type.SKIN);
			} else {
				UUID id = EntityPlayer.getUUID(profile);
				resourcelocation = DefaultPlayerSkin.getDefaultSkin(id);
			}
		}

		this.bindTexture(resourcelocation);

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();

		GlStateManager.translate(x + 0.5F, y, z + 0.5F);

		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlpha();

		// GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
		GlStateManager.enableBlend();

		float yaw = 26F;
		float pitch = -61.0F;
		float scale = 0.0625F;

		GL11.glRotatef(180 - getDirection(rotation), 0.0F, 1.0F, 0.0F);

		GL11.glTranslatef(0.0F, 0.14F, -0.18F);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		modelbase.render((Entity) null, 0.0F, 0.0F, 0.0F, yaw, pitch, scale);

		GlStateManager.popMatrix();

	}*/

	private int getDirection(int i) {
		switch (i) {
		case 0:
			return 0;
		case 1:
			return 270;
		case 2:
			return 180;
		case 3:
			return 90;
		default:
			return 0;
		}
	}

}
