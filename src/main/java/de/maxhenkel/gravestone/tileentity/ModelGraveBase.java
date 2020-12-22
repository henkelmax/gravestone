package de.maxhenkel.gravestone.tileentity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelGraveBase extends ModelBase{

	private ModelRenderer dirtBase;
	
	public ModelGraveBase() {
		textureWidth=16;
		textureHeight=16;
		dirtBase=new ModelRenderer(this);
		dirtBase.addBox(1, 1, 1, 14, 1, 11);
		dirtBase.addBox(0, 0, 0, 16, 1, 16);
		dirtBase.addBox(4, 1, 12, 10, 1, 2);
		dirtBase.addBox(6, 1, 14, 5, 1, 1);
		dirtBase.offsetX=-0.5F;
		dirtBase.offsetZ=-0.5F;
	}
	
	@Override
	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
			float p_78088_6_, float p_78088_7_) {
		super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
		dirtBase.render(p_78088_7_);
	}
	
}
