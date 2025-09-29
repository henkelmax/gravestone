package de.maxhenkel.gravestone.tileentity.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;

import java.util.UUID;

public class GravestoneRenderState extends BlockEntityRenderState {

    public FormattedCharSequence name;
    public Direction direction;
    public boolean renderHead;
    public UUID playerId;

}
