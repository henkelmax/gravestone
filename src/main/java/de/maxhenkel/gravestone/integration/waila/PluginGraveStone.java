package de.maxhenkel.gravestone.integration.waila;

import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import de.maxhenkel.gravestone.tileentity.GraveStoneTileEntity;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class PluginGraveStone implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(HUDHandlerGraveStone.INSTANCE, TooltipPosition.HEAD, GraveStoneBlock.class);
        registrar.registerComponentProvider(HUDHandlerGraveStone.INSTANCE, TooltipPosition.BODY, GraveStoneBlock.class);
        registrar.registerBlockDataProvider(HUDHandlerGraveStone.INSTANCE, GraveStoneTileEntity.class);
    }

}