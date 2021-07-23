package de.maxhenkel.gravestone.tileentity;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class GraveStoneTileEntity extends BlockEntity implements Nameable {

    protected Death death;

    protected Component customName;

    public GraveStoneTileEntity(BlockPos pos, BlockState state) {
        super(Main.GRAVESTONE_TILEENTITY, pos, state);
        death = new Death.Builder(GraveUtils.EMPTY_UUID, GraveUtils.EMPTY_UUID).build();
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.put("Death", death.toNBT());
        if (customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName));
        }
        return super.save(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        if (compound.contains("Death")) {
            death = Death.fromNBT(compound.getCompound("Death"));
        } else { // Compatibility
            UUID playerUUID = GraveUtils.EMPTY_UUID;
            try {
                playerUUID = UUID.fromString(compound.getString("PlayerUUID"));
            } catch (Exception e) {
            }

            Death.Builder builder = new Death.Builder(playerUUID, UUID.randomUUID());

            NonNullList<ItemStack> items = NonNullList.create();
            ListTag list = compound.getList("ItemStacks", 10);
            for (int i = 0; i < list.size(); i++) {
                items.add(ItemStack.of(list.getCompound(i)));
            }

            builder.additionalItems(items);
            builder.playerName(compound.getString("PlayerName"));
            builder.timestamp(compound.getLong("DeathTime"));
            death = builder.build();
        }

        if (compound.contains("CustomName")) {
            customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compound = new CompoundTag();
        compound.put("Death", death.toNBT(false));
        compound.putString("CustomName", Component.Serializer.toJson(customName));
        return super.save(compound);
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
            return new TranslatableComponent(Main.GRAVESTONE.getDescriptionId());
        }
        return new TranslatableComponent("message.gravestone.grave_of", name);
    }

    @Nullable
    public Component getGraveName() {
        if (!death.getPlayerName().isEmpty()) {
            return new TextComponent(death.getPlayerName());
        } else if (customName != null) {
            return customName;
        } else {
            return null;
        }
    }

}
