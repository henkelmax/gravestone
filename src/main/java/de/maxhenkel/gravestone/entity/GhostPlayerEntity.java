package de.maxhenkel.gravestone.entity;

import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.Main;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class GhostPlayerEntity extends MonsterEntity {

    private static final DataParameter<Optional<UUID>> PLAYER_UUID = EntityDataManager.defineId(GhostPlayerEntity.class, DataSerializers.OPTIONAL_UUID);
    private static final DataParameter<Byte> PLAYER_MODEL = EntityDataManager.defineId(GhostPlayerEntity.class, DataSerializers.BYTE);

    public GhostPlayerEntity(EntityType type, World world) {
        super(type, world);
    }

    public GhostPlayerEntity(World world, UUID playerUUID, ITextComponent name, NonNullList<ItemStack> equipment, byte model) {
        this(Main.GHOST, world);
        setPlayerUUID(playerUUID);
        setCustomName(name);
        setModel(model);
        Arrays.fill(armorDropChances, 0F);
        Arrays.fill(handDropChances, 0F);

        for (int i = 0; i < EquipmentSlotType.values().length; i++) {
            setItemSlot(EquipmentSlotType.values()[i], equipment.get(i));
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(PLAYER_UUID, Optional.empty());
        getEntityData().define(PLAYER_MODEL, (byte) 0);
    }

    public static AttributeModifierMap.MutableAttribute getGhostAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3D)
                .add(Attributes.ARMOR, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D)
                .add(Attributes.FOLLOW_RANGE, 35D);
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(9, new LookRandomlyGoal(this));

        if (Main.SERVER_CONFIG.friendlyGhost.get()) {
            targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, true, (entityLiving) ->
                    entityLiving != null
                            && !entityLiving.isInvisible()
                            && (entityLiving instanceof MonsterEntity || entityLiving instanceof SlimeEntity)
                            && !(entityLiving instanceof CreeperEntity)
                            && !(entityLiving instanceof GhostPlayerEntity)
            ));
        } else {
            targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        }
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }

    @Override
    public CreatureAttribute getMobType() {
        return CreatureAttribute.UNDEAD;
    }

    public void setPlayerUUID(UUID uuid) {
        this.getEntityData().set(PLAYER_UUID, Optional.of(uuid));
        if (uuid.toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")) {
            setOverpowered();
        }
    }

    private void setOverpowered() {
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(35.0D);
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(20.0D);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        if (name != null && name.getString().equals("henkelmax")) {
            setOverpowered();
        }
    }

    public UUID getPlayerUUID() {
        return getEntityData().get(PLAYER_UUID).orElse(GraveUtils.EMPTY_UUID);
    }

    public void setModel(byte model) {
        entityData.set(PLAYER_MODEL, model);
    }

    public byte getModel() {
        return entityData.get(PLAYER_MODEL);
    }

    public boolean isWearing(PlayerModelPart part) {
        return (getModel() & part.getMask()) == part.getMask();
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        getEntityData().get(PLAYER_UUID).ifPresent(uuid -> {
            compound.putUUID("PlayerUUID", uuid);
        });
        compound.putByte("Model", getModel());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("player_uuid")) { // Compatibility
            String uuidStr = compound.getString("player_uuid");
            try {
                UUID uuid = UUID.fromString(uuidStr);
                setPlayerUUID(uuid);
            } catch (Exception e) {
            }
        } else if (compound.contains("PlayerUUID")) {
            setPlayerUUID(compound.getUUID("PlayerUUID"));
        }
        setModel(compound.getByte("Model"));
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (entity.getName().getString().equals("henkelmax") || entity.getUUID().toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")) {
            return true;
        } else {
            return super.doHurtTarget(entity);
        }
    }

}
