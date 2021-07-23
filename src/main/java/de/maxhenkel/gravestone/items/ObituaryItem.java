package de.maxhenkel.gravestone.items;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.death.DeathManager;
import de.maxhenkel.corelib.net.NetUtils;
import de.maxhenkel.gravestone.Main;
import de.maxhenkel.gravestone.net.MessageOpenObituary;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ObituaryItem extends Item {

    public ObituaryItem() {
        super(new Item.Properties().stacksTo(1));
        this.setRegistryName("obituary");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player p, InteractionHand hand) {
        if (!(p instanceof ServerPlayer)) {
            return InteractionResultHolder.success(p.getItemInHand(hand));
        }
        ServerPlayer player = (ServerPlayer) p;
        Death death = fromStack(player, player.getItemInHand(hand));

        if (death == null) {
            player.displayClientMessage(new TranslatableComponent("message.gravestone.death_not_found"), true);
        } else if (player.isShiftKeyDown()) {
            if (player.hasPermissions(player.server.getOperatorUserPermissionLevel())) {
                Component replace = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("message.gravestone.restore.replace"))
                        .withStyle((style) -> style
                                .applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " replace"))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("message.gravestone.restore.replace.description")))
                        );
                Component add = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("message.gravestone.restore.add"))
                        .withStyle((style) -> style
                                .applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " add"))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("message.gravestone.restore.add.description")))
                        );
                player.sendMessage(new TranslatableComponent("message.gravestone.restore").append(" ").append(replace).append(" ").append(add), Util.NIL_UUID);
            }
        } else {
            NetUtils.sendTo(Main.SIMPLE_CHANNEL, player, new MessageOpenObituary(death));
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Nullable
    public Death fromStack(ServerPlayer player, ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound == null || !compound.contains("Death")) {
            return null;
        }
        CompoundTag death = compound.getCompound("Death");
        return DeathManager.getDeath(player.getLevel(), death.getUUID("PlayerUUID"), death.getUUID("DeathID"));
    }

    public ItemStack toStack(Death death) {
        ItemStack stack = new ItemStack(this);
        CompoundTag d = stack.getOrCreateTagElement("Death");
        d.putUUID("PlayerUUID", death.getPlayerUUID());
        d.putUUID("DeathID", death.getId());
        return stack;
    }
}
