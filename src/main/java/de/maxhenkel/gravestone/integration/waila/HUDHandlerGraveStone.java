package de.maxhenkel.gravestone.integration.waila;

import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HUDHandlerGraveStone implements IComponentProvider, IServerDataProvider<BlockEntity> {

    public static final HUDHandlerGraveStone INSTANCE = new HUDHandlerGraveStone();

    private static final ResourceLocation OBJECT_NAME_TAG = new ResourceLocation("waila", "object_name");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof GraveStoneTileEntity grave) {
            if (blockAccessor.getTooltipPosition().equals(TooltipPosition.BODY)) {
                Component time = GraveUtils.getDate(grave.getDeath().getTimestamp());
                if (time != null) {
                    iTooltip.add(new TranslatableComponent("message.gravestone.date_of_death", time));
                }

                CompoundTag data = blockAccessor.getServerData();
                if (data.contains("ItemCount")) {
                    iTooltip.add(new TranslatableComponent("message.gravestone.item_count", data.getInt("ItemCount")));
                }
            } else if (blockAccessor.getTooltipPosition().equals(TooltipPosition.HEAD)) {
                iTooltip.remove(OBJECT_NAME_TAG);
                iTooltip.add(new TextComponent(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), grave.getName().getString())).withStyle(ChatFormatting.WHITE));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
        if (blockEntity instanceof GraveStoneTileEntity grave) {
            compoundTag.putInt("ItemCount", (int) grave.getDeath().getAllItems().stream().filter(itemStack -> !itemStack.isEmpty()).count());
        }

    }
}