package de.maxhenkel.gravestone.tileentity;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.UUID;

public class GraveStoneTileEntity extends TileEntity implements INameable {

    protected Death death;

    protected ITextComponent customName;

    public GraveStoneTileEntity() {
        super(Main.GRAVESTONE_TILEENTITY);
        death = new Death.Builder(GraveUtils.EMPTY_UUID, GraveUtils.EMPTY_UUID).build();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("Death", death.toNBT());
        if (customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(customName));
        }
        return super.write(compound);
    }

    @Override
    public void func_230337_a_(BlockState state, CompoundNBT compound) {
        super.func_230337_a_(state, compound);

        if (compound.contains("Death")) {
            death = Death.fromNBT(compound.getCompound("Death"));
        } else { // Compatibility
            UUID playerUUID = GraveUtils.EMPTY_UUID;
            try {
                playerUUID = UUID.fromString(compound.getString("PlayerUUID"));
            } catch (Exception e) {
            }

            Death.Builder builder = new Death.Builder(playerUUID, GraveUtils.EMPTY_UUID);

            NonNullList<ItemStack> items = NonNullList.create();
            ListNBT list = compound.getList("ItemStacks", 10);
            for (int i = 0; i < list.size(); i++) {
                items.add(ItemStack.read(list.getCompound(i)));
            }

            builder.additionalItems(items);
            builder.playerName(compound.getString("PlayerName"));
            builder.timestamp(compound.getLong("DeathTime"));
        }

        if (compound.contains("CustomName")) {
            customName = ITextComponent.Serializer.func_240643_a_(compound.getString("CustomName"));
        }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.func_230337_a_(null, pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compound = new CompoundNBT();
        compound.put("Death", death.toNBT(false));
        compound.putString("CustomName", ITextComponent.Serializer.toJson(customName));
        return super.write(compound);
    }

    public Death getDeath() {
        return death;
    }

    public void setDeath(Death death) {
        this.death = death;
        markDirty();
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
        markDirty();
    }

    @Override
    public ITextComponent getName() {
        return customName != null ? customName : getDefaultName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return customName;
    }

    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent(Main.GRAVESTONE.getTranslationKey());
    }

    @Nullable
    public ITextComponent getGraveName() {
        if (!death.getPlayerName().isEmpty()) {
            return new StringTextComponent(death.getPlayerName());
        } else if (customName != null) {
            return customName;
        } else {
            return null;
        }
    }

}
