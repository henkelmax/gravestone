package de.maxhenkel.gravestone.items;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.death.DeathManager;
import de.maxhenkel.corelib.net.NetUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.net.MessageOpenObituary;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ObituaryItem extends Item {

    public ObituaryItem() {
        super(new Item.Properties().maxStackSize(1));
        this.setRegistryName("obituary");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity p, Hand hand) {
        if (!(p instanceof ServerPlayerEntity)) {
            return ActionResult.resultSuccess(p.getHeldItem(hand));
        }
        ServerPlayerEntity player = (ServerPlayerEntity) p;
        Death death = fromStack(player, player.getHeldItem(hand));

        if (player.isSneaking() && player.abilities.isCreativeMode) {
            if (player instanceof ServerPlayerEntity) {
                //TODO
                /*NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {

                    @Nullable
                    @Override
                    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return ChestContainer.createGeneric9X6(id, playerInventory, new DeathItemsInventory(death));
                    }

                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent(ObituaryItem.this.getTranslationKey());
                    }
                });*/
            }
        } else {
            NetUtils.sendTo(Main.SIMPLE_CHANNEL, player, new MessageOpenObituary(death));
        }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    @Nullable
    public Death fromStack(ServerPlayerEntity player, ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound == null || !compound.contains("Death")) {
            return null;
        }
        CompoundNBT death = compound.getCompound("Death");
        return DeathManager.getDeath(player.getServerWorld(), death.getUniqueId("PlayerUUID"), death.getUniqueId("DeathID"));
    }

    public ItemStack toStack(Death death) {
        ItemStack stack = new ItemStack(this);
        CompoundNBT d = stack.getOrCreateChildTag("Death");
        d.putUniqueId("PlayerUUID", death.getPlayerUUID());
        d.putUniqueId("DeathID", death.getId());
        return stack;
    }
}
