package de.maxhenkel.gravestone.tileentity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelGraveStone extends ModelBase{

	private ModelRenderer stone;
	
	public ModelGraveStone() {
		textureWidth=16;
		textureHeight=16;
		stone=new ModelRenderer(this);
		stone.addBox(1, 2, 1, 14, 10, 1);
		stone.addBox(2, 12, 1, 12, 2, 1);
		stone.addBox(3, 14, 1, 10, 1, 1);
		stone.offsetX=-0.5F;
		stone.offsetZ=-0.5F;
	
	}
	
	@Override
	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
			float p_78088_6_, float p_78088_7_) {
		super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
		stone.render(p_78088_7_);
	}
	
}
