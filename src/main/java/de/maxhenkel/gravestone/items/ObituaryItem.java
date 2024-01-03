package de.maxhenkel.gravestone.items;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.death.DeathManager;
import de.maxhenkel.gravestone.net.MessageOpenObituary;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

public class ObituaryItem extends Item {

    public ObituaryItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player p, InteractionHand hand) {
        if (!(p instanceof ServerPlayer)) {
            return InteractionResultHolder.success(p.getItemInHand(hand));
        }
        ServerPlayer player = (ServerPlayer) p;
        Death death = fromStack(player, player.getItemInHand(hand));

        if (death == null) {
            player.displayClientMessage(Component.translatable("message.gravestone.death_not_found"), true);
        } else if (player.isShiftKeyDown()) {
            if (player.hasPermissions(player.server.getOperatorUserPermissionLevel())) {
                Component replace = ComponentUtils.wrapInSquareBrackets(Component.translatable("message.gravestone.restore.replace"))
                        .withStyle((style) -> style
                                .applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " replace"))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.gravestone.restore.replace.description")))
                        );
                Component add = ComponentUtils.wrapInSquareBrackets(Component.translatable("message.gravestone.restore.add"))
                        .withStyle((style) -> style
                                .applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/restore @s " + death.getId().toString() + " add"))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.gravestone.restore.add.description")))
                        );
                player.sendSystemMessage(Component.translatable("message.gravestone.restore").append(" ").append(replace).append(" ").append(add));
            }
        } else {
            PacketDistributor.PLAYER.with(player).send(new MessageOpenObituary(death));
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
        return DeathManager.getDeath(player.serverLevel(), death.getUUID("PlayerUUID"), death.getUUID("DeathID"));
    }

    public ItemStack toStack(Death death) {
        ItemStack stack = new ItemStack(this);
        CompoundTag d = stack.getOrCreateTagElement("Death");
        d.putUUID("PlayerUUID", death.getPlayerUUID());
        d.putUUID("DeathID", death.getId());
        return stack;
    }
}
