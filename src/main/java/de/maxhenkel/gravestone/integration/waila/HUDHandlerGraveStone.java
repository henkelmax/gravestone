package de.maxhenkel.gravestone.integration.waila;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerGraveStone implements IComponentProvider, IServerDataProvider<TileEntity> {

    static final ResourceLocation OBJECT_NAME_TAG = new ResourceLocation("waila", "object_name");
    static final ResourceLocation CONFIG_SHOW_REGISTRY = new ResourceLocation("waila", "show_registry");
    static final ResourceLocation REGISTRY_NAME_TAG = new ResourceLocation("waila", "registry_name");

    static final HUDHandlerGraveStone INSTANCE = new HUDHandlerGraveStone();

    @Override
    public void appendHead(List<ITextComponent> t, IDataAccessor accessor, IPluginConfig config) {
        ITaggableList<ResourceLocation, ITextComponent> tooltip = (ITaggableList<ResourceLocation, ITextComponent>) t;
        tooltip.setTag(OBJECT_NAME_TAG, new StringTextComponent(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), ((GraveStoneTileEntity) accessor.getTileEntity()).getName().getString())));
        if (config.get(CONFIG_SHOW_REGISTRY)) {
            tooltip.setTag(REGISTRY_NAME_TAG, new StringTextComponent(accessor.getBlock().getRegistryName().toString()).func_240699_a_(TextFormatting.GRAY));
        }
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if (!(accessor.getTileEntity() instanceof GraveStoneTileEntity)) {
            return;
        }

        GraveStoneTileEntity grave = (GraveStoneTileEntity) accessor.getTileEntity();

        ITextComponent time = GraveUtils.getDate(grave.getDeath().getTimestamp());
        if (time != null) {
            tooltip.add(new TranslationTextComponent("message.gravestone.date_of_death", time));
        }

        CompoundNBT data = accessor.getServerData();
        if (data.contains("ItemCount")) {
            tooltip.add(new TranslationTextComponent("message.gravestone.item_count", data.getInt("ItemCount")));
        }
    }

    @Override
    public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity serverPlayerEntity, World world, TileEntity grave) {
        Death death = ((GraveStoneTileEntity) grave).getDeath();
        compoundNBT.putInt("ItemCount", (int) death.getAllItems().stream().filter(itemStack -> !itemStack.isEmpty()).count());
    }

}