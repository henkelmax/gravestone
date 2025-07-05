package de.maxhenkel.gravestone.tileentity;

import de.maxhenkel.corelib.codec.CodecUtils;
import de.maxhenkel.corelib.codec.ValueInputOutputUtils;
import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.GravestoneMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.Optional;

public class GraveStoneTileEntity extends BlockEntity implements Nameable {

    protected Death death;

    @Nullable
    protected Component customName;

    public GraveStoneTileEntity(BlockPos pos, BlockState state) {
        super(GravestoneMod.GRAVESTONE_TILEENTITY.get(), pos, state);
        death = new Death.Builder(GraveUtils.EMPTY_UUID, GraveUtils.EMPTY_UUID).build();
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        death.write(valueOutput, "Death");
        if (customName != null) {
            CompoundTag tag = new CompoundTag();
            CodecUtils.toJsonString(ComponentSerialization.CODEC, customName).ifPresent(s -> tag.putString("CustomName", s));
            valueOutput.store(tag);
        }
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        death = Death.read(valueInput, "Death");
        Optional<String> optionalName = valueInput.getString("CustomName");
        optionalName.ifPresent(s -> customName = CodecUtils.fromJson(ComponentSerialization.CODEC, s).orElse(null));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        TagValueOutput valueOutput = ValueInputOutputUtils.createValueOutput(this, provider);
        saveAdditional(valueOutput);
        return ValueInputOutputUtils.toTag(valueOutput);
    }

    public Death getDeath() {
        return death;
    }

    public void setDeath(Death death) {
        this.death = death;
        setChanged();
    }

    public void setCustomName(Component name) {
        this.customName = name;
        setChanged();
    }

    @Override
    public Component getName() {
        return customName != null ? customName : getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    protected Component getDefaultName() {
        String name = death.getPlayerName();
        if (name == null || name.isEmpty()) {
            return Component.translatable(GravestoneMod.GRAVESTONE.get().getDescriptionId());
        }
        return Component.translatable("message.gravestone.grave_of", name);
    }

    @Nullable
    public Component getGraveName() {
        if (!death.getPlayerName().isEmpty()) {
            return Component.literal(death.getPlayerName());
        } else if (customName != null) {
            return customName;
        } else {
            return null;
        }
    }

}
