package de.maxhenkel.gravestone.integration.waila;

import de.maxhenkel.gravestone.GravestoneMod;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import snownee.jade.api.*;

public class GravestoneDataProvider implements IServerDataProvider<BlockAccessor> {

    public static final GravestoneDataProvider INSTANCE = new GravestoneDataProvider();

    private static final Identifier UID = Identifier.fromNamespaceAndPath(GravestoneMod.MODID, "grave_data");

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof GraveStoneTileEntity grave) {
            compoundTag.putInt("ItemCount", (int) grave.getDeath().getAllItems().stream().filter(itemStack -> !itemStack.isEmpty()).count());
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
