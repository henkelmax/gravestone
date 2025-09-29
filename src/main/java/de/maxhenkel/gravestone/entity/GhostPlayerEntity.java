package de.maxhenkel.gravestone.entity;

import de.maxhenkel.gravestone.GraveUtils;
import de.maxhenkel.gravestone.GravestoneMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Optional;
import java.util.UUID;

public class GhostPlayerEntity extends Monster {

    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> PLAYER_UUID = SynchedEntityData.defineId(GhostPlayerEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final EntityDataAccessor<Byte> PLAYER_MODEL = SynchedEntityData.defineId(GhostPlayerEntity.class, EntityDataSerializers.BYTE);

    public GhostPlayerEntity(EntityType type, Level world) {
        super(type, world);
    }

    public GhostPlayerEntity(Level world, UUID playerUUID, Component name, EnumMap<EquipmentSlot, ItemStack> equipment, byte model) {
        this(GravestoneMod.GHOST.get(), world);
        setPlayerUUID(playerUUID);
        setCustomName(name);
        setModel(model);
        //TODO Check
        //Arrays.fill(armorDropChances, 0F);
        //Arrays.fill(handDropChances, 0F);

        for (EnumMap.Entry<EquipmentSlot, ItemStack> entry : equipment.entrySet()) {
            setItemSlot(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PLAYER_UUID, Optional.empty());
        builder.define(PLAYER_MODEL, (byte) 0);
    }

    public static AttributeSupplier getGhostAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 3D)
                .add(Attributes.ARMOR, 2D)
                .add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D)
                .add(Attributes.FOLLOW_RANGE, 35D).build();
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        if (GravestoneMod.SERVER_CONFIG.friendlyGhost.get()) {
            targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, true, (entity, level) ->
                    entity != null
                            && !entity.isInvisible()
                            && (entity instanceof Monster || entity instanceof Slime)
                            && !(entity instanceof Creeper)
                            && !(entity instanceof GhostPlayerEntity)
            ));
        } else {
            targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }

    public void setPlayerUUID(UUID uuid) {
        this.getEntityData().set(PLAYER_UUID, Optional.of(EntityReference.of(uuid)));
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
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        if (name != null && name.getString().equals("henkelmax")) {
            setOverpowered();
        }
    }

    public UUID getPlayerUUID() {
        return getEntityData().get(PLAYER_UUID).map(EntityReference::getUUID).orElse(GraveUtils.EMPTY_UUID);
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
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        getEntityData().get(PLAYER_UUID).ifPresent(uuid -> {
            valueOutput.store("PlayerUUID", UUIDUtil.CODEC, uuid.getUUID());
        });
        valueOutput.putByte("Model", getModel());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        valueInput.read("PlayerUUID", UUIDUtil.CODEC).ifPresent(this::setPlayerUUID);
        setModel(valueInput.getByteOr("Model", (byte) 0));
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity entity) {
        if (entity.getName().getString().equals("henkelmax") || entity.getUUID().toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")) {
            return true;
        } else {
            return super.doHurtTarget(level, entity);
        }
    }
}
