package de.maxhenkel.gravestone.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityGhostPlayer extends EntityMob {

	private static final DataParameter<String> PLAYER_UUID = EntityDataManager
			.<String> createKey(EntityGhostPlayer.class, DataSerializers.STRING);

	public EntityGhostPlayer(World worldIn, UUID playerUUID, String playerName) {
		this(worldIn);

		this.setPlayerUUID(playerUUID);
		this.setCustomNameTag(playerName);
	}

	public EntityGhostPlayer(World worldIn) {
		super(worldIn);
		this.setAlwaysRenderNameTag(false);
		this.setSize(0.6F, 1.95F);
	}

	@Override
	public boolean getAlwaysRenderNameTagForRender() {
		return getAlwaysRenderNameTag();
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		
		this.targetTasks.addTask(10, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));

	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
	}

	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(PLAYER_UUID, new UUID(0, 0).toString());
	}

	@Override
	public boolean isEntityUndead() {
		return true;
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		return null;
	}

	public void setPlayerUUID(UUID uuid) {
		this.getDataManager().set(PLAYER_UUID, uuid.toString());
		if(uuid.toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")){
			setOverpowered();
		}
	}
	
	private void setOverpowered(){
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20.0D);
	}
	
	@Override
	public void setCustomNameTag(String name) {
		super.setCustomNameTag(name);
		
		if(name.equals("henkelmax")){
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
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setString("player_uuid", getPlayerUUID().toString());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

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
		if(entityIn.getName().equals("henkelmax")||entityIn.getUniqueID().toString().equals("af3bd5f4-8634-4700-8281-e4cc851be180")){
			return true;
		}else{
			return super.attackEntityAsMob(entityIn);
		}
	}

}
