package de.maxhenkel.gravestone.integration.waila;

import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;

public class GravestoneDataProvider implements IServerDataProvider<BlockAccessor> {

    public static final GravestoneDataProvider INSTANCE = new GravestoneDataProvider();

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Main.MODID, "grave_data");

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof GraveStoneTileEntity grave) {
            compoundTag.putInt("ItemCount", (int) grave.getDeath().getAllItems().stream().filter(itemStack -> !itemStack.isEmpty()).count());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
