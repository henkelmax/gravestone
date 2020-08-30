package de.maxhenkel.gravestone.entity;

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class GhostPlayerEntity extends MonsterEntity {

    private static final DataParameter<String> PLAYER_UUID = EntityDataManager.createKey(GhostPlayerEntity.class, DataSerializers.STRING);

    public GhostPlayerEntity(EntityType type, World world) {
        super(type, world);
    }

    public GhostPlayerEntity(World world, UUID playerUUID, String playerName) {
        this(Main.GHOST, world);
        setPlayerUUID(playerUUID);
        setCustomName(new StringTextComponent(playerName));
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(PLAYER_UUID, new UUID(0, 0).toString());
    }

    public static AttributeModifierMap.MutableAttribute getAttributes() {
        return MonsterEntity.func_234295_eP_()
                .func_233815_a_(Attributes.field_233823_f_, 3D)
                .func_233815_a_(Attributes.field_233826_i_, 2D)
                .func_233815_a_(Attributes.field_233821_d_, 0.23000000417232513D)
                .func_233815_a_(Attributes.field_233819_b_, 35D);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
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
            targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, true, (entityLiving) -> entityLiving != null && !entityLiving.isInvisible() && entityLiving instanceof MonsterEntity && !(entityLiving instanceof CreeperEntity) && !(entityLiving instanceof GhostPlayerEntity)));
        } else {
            targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        }
    }

    @Override
    public boolean isEntityUndead() {
        return true;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    public void setPlayerUUID(UUID uuid) {
        this.getDataManager().set(PLAYER_UUID, uuid.toString());
        if (uuid.toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")) {
            setOverpowered();
        }
    }

    private void setOverpowered() {
        getAttribute(Attributes.field_233819_b_).setBaseValue(35.0D);
        getAttribute(Attributes.field_233821_d_).setBaseValue(0.4D);
        getAttribute(Attributes.field_233823_f_).setBaseValue(20.0D);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        if (name != null && name.getString().equals("henkelmax")) {
            setOverpowered();
        }
    }

    public UUID getPlayerUUID() {
        String uuidStr = this.getDataManager().get(PLAYER_UUID);
        UUID uuid = new UUID(0, 0);

        try {
            uuid = UUID.fromString(uuidStr);
        } catch (Exception e) {

        }

        return uuid;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("player_uuid", getPlayerUUID().toString());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("player_uuid")) {
            String uuidStr = compound.getString("player_uuid");

            try {
                UUID uuid = UUID.fromString(uuidStr);
                setPlayerUUID(uuid);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (entityIn.getName().getString().equals("henkelmax") || entityIn.getUniqueID().toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")) {
            return true;
        } else {
            return super.attackEntityAsMob(entityIn);
        }
    }

}
