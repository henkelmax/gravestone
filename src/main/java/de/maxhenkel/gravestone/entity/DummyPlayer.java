package de.maxhenkel.gravestone.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;

public class DummyPlayer extends RemotePlayer {

    private final byte model;

    public DummyPlayer(ClientLevel world, GameProfile gameProfile, NonNullList<ItemStack> equipment, byte model) {
        super(world, gameProfile);
        this.model = model;
        for (EquipmentSlot type : EquipmentSlot.values()) {
            setItemSlot(type, equipment.get(type.ordinal()));
        }
        AttributeInstance attribute = getAttributes().getInstance(ForgeMod.NAMETAG_DISTANCE.get());
        if (attribute != null) {
            attribute.setBaseValue(0D);
        }
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isModelPartShown(PlayerModelPart part) {
        return (model & part.getMask()) == part.getMask();
    }
}
