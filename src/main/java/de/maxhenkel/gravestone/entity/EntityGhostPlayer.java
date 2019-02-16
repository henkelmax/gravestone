package de.maxhenkel.gravestone.entity;

import java.util.UUID;
import javax.annotation.Nullable;
import com.google.common.base.Predicate;
import de.maxhenkel.gravestone.Config;
import de.maxhenkel.gravestone.Main;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class EntityGhostPlayer extends EntityMob {

    private static final DataParameter<String> PLAYER_UUID = EntityDataManager
            .<String>createKey(EntityGhostPlayer.class, DataSerializers.STRING);

    public EntityGhostPlayer(World world) {
        super(Main.ghost, world);
        this.setSize(0.6F, 1.95F);
    }

    public EntityGhostPlayer(World world, UUID playerUUID, String playerName) {
        this(world);

        this.setPlayerUUID(playerUUID);
        this.setCustomName(new TextComponentString(playerName));
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return false;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));

        if (Config.friendlyGhost) {
            this.targetTasks.addTask(10, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, false, true, new Predicate<EntityLiving>() {
                public boolean apply(@Nullable EntityLiving entityLiving) {
                    return entityLiving != null && IMob.VISIBLE_MOB_SELECTOR.test(entityLiving) && !(entityLiving instanceof EntityCreeper) && !(entityLiving instanceof EntityGhostPlayer);
                }
            }));
        } else {
            this.targetTasks.addTask(10, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        }

    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
        this.getDataManager().register(PLAYER_UUID, new UUID(0, 0).toString());
    }

    @Override
    public boolean isEntityUndead() {
        return true;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return Main.ghostLootTable;
    }

    public void setPlayerUUID(UUID uuid) {
        this.getDataManager().set(PLAYER_UUID, uuid.toString());
        if (uuid.toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")) {
            setOverpowered();
        }
    }

    private void setOverpowered() {
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20.0D);
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        if (name.getUnformattedComponentText().equals("henkelmax")) {
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
    public void writeAdditional(NBTTagCompound compound) {
        super.writeAdditional(compound);
        compound.setString("player_uuid", getPlayerUUID().toString());
    }

    @Override
    public void readAdditional(NBTTagCompound compound) {
        super.readAdditional(compound);
        if (compound.hasKey("player_uuid")) {
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
        if (entityIn.getName().equals("henkelmax") || entityIn.getUniqueID().toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")) {
            return true;
        } else {
            return super.attackEntityAsMob(entityIn);
        }
    }

}
