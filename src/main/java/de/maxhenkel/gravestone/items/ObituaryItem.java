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
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
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

        if (player.isSneaking()) {
            if (player.hasPermissionLevel(player.server.getOpPermissionLevel())) {
                ITextComponent replace = TextComponentUtils.func_240647_a_(new TranslationTextComponent("message.gravestone.restore.replace"))
                        .func_240700_a_((style) -> style
                                .func_240723_c_(TextFormatting.GREEN)
                                .func_240715_a_(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " replace"))
                                .func_240716_a_(new HoverEvent(HoverEvent.Action.field_230550_a_, new TranslationTextComponent("message.gravestone.restore.replace.description")))
                        );
                ITextComponent add = TextComponentUtils.func_240647_a_(new TranslationTextComponent("message.gravestone.restore.add"))
                        .func_240700_a_((style) -> style
                                .func_240723_c_(TextFormatting.GREEN)
                                .func_240715_a_(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " add"))
                                .func_240716_a_(new HoverEvent(HoverEvent.Action.field_230550_a_, new TranslationTextComponent("message.gravestone.restore.add.description")))
                        );
                player.sendMessage(new TranslationTextComponent("message.gravestone.restore").func_240702_b_(" ").func_230529_a_(replace).func_240702_b_(" ").func_230529_a_(add), Util.field_240973_b_);
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
