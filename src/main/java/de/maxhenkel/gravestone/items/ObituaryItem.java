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
        super(new Item.Properties().stacksTo(1));
        this.setRegistryName("obituary");
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity p, Hand hand) {
        if (!(p instanceof ServerPlayerEntity)) {
            return ActionResult.success(p.getItemInHand(hand));
        }
        ServerPlayerEntity player = (ServerPlayerEntity) p;
        Death death = fromStack(player, player.getItemInHand(hand));

        if (death == null) {
            player.displayClientMessage(new TranslationTextComponent("message.gravestone.death_not_found"), true);
        } else if (player.isShiftKeyDown()) {
            if (player.hasPermissions(player.server.getOperatorUserPermissionLevel())) {
                ITextComponent replace = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("message.gravestone.restore.replace"))
                        .withStyle((style) -> style
                                .applyFormat(TextFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " replace"))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("message.gravestone.restore.replace.description")))
                        );
                ITextComponent add = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("message.gravestone.restore.add"))
                        .withStyle((style) -> style
                                .applyFormat(TextFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " add"))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("message.gravestone.restore.add.description")))
                        );
                player.sendMessage(new TranslationTextComponent("message.gravestone.restore").append(" ").append(replace).append(" ").append(add), Util.NIL_UUID);
            }
        } else {
            NetUtils.sendTo(Main.SIMPLE_CHANNEL, player, new MessageOpenObituary(death));
        }
        return ActionResult.success(player.getItemInHand(hand));
    }

    @Nullable
    public Death fromStack(ServerPlayerEntity player, ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound == null || !compound.contains("Death")) {
            return null;
        }
        CompoundNBT death = compound.getCompound("Death");
        return DeathManager.getDeath(player.getLevel(), death.getUUID("PlayerUUID"), death.getUUID("DeathID"));
    }

    public ItemStack toStack(Death death) {
        ItemStack stack = new ItemStack(this);
        CompoundNBT d = stack.getOrCreateTagElement("Death");
        d.putUUID("PlayerUUID", death.getPlayerUUID());
        d.putUUID("DeathID", death.getId());
        return stack;
    }
}
