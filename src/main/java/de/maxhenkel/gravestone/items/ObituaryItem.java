package de.maxhenkel.gravestone.items;

import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.death.DeathManager;
import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.GravestoneMod;
import de.maxhenkel.gravestone.net.MessageOpenObituary;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;

public class ObituaryItem extends Item {

    public ObituaryItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level world, Player p, InteractionHand hand) {
        if (!(p instanceof ServerPlayer player)) {
            return InteractionResult.SUCCESS;
        }

        ItemStack itemInHand = player.getItemInHand(hand);
        convert(itemInHand);
        Death death = fromStack(player, itemInHand);

        if (death == null) {
            player.displayClientMessage(Component.translatable("message.gravestone.death_not_found"), true);
        } else if (player.isShiftKeyDown()) {
            if (player.hasPermissions(player.level().getServer().operatorUserPermissionLevel())) {
                Component replace = ComponentUtils.wrapInSquareBrackets(Component.translatable("message.gravestone.restore.replace"))
                        .withStyle((style) -> style
                                .applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent.SuggestCommand("/restore @s " + death.getId().toString() + " replace"))
                                .withHoverEvent(new HoverEvent.ShowText(Component.translatable("message.gravestone.restore.replace.description")))
                        );
                Component add = ComponentUtils.wrapInSquareBrackets(Component.translatable("message.gravestone.restore.add"))
                        .withStyle((style) -> style
                                .applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent.SuggestCommand("/restore @s " + death.getId().toString() + " add"))
                                .withHoverEvent(new HoverEvent.ShowText(Component.translatable("message.gravestone.restore.add.description")))
                        );
                player.sendSystemMessage(Component.translatable("message.gravestone.restore").append(" ").append(replace).append(" ").append(add));
            }
        } else {
            PacketDistributor.sendToPlayer(player, new MessageOpenObituary(death));
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    public Death fromStack(ServerPlayer player, ItemStack stack) {
        DeathInfo deathInfo = stack.get(GravestoneMod.DEATH_DATA_COMPONENT);
        if (deathInfo == null) {
            return null;
        }
        return DeathManager.getDeath(player.level(), deathInfo.getPlayerId(), deathInfo.getDeathId());
    }

    public ItemStack toStack(Death death) {
        ItemStack stack = new ItemStack(this);
        stack.set(GravestoneMod.DEATH_DATA_COMPONENT, new DeathInfo(death.getPlayerUUID(), death.getId()));
        return stack;
    }

    public static void convert(ItemStack stack) {
        if (!(stack.getItem() instanceof ObituaryItem)) {
            return;
        }
        if (stack.has(GravestoneMod.DEATH_DATA_COMPONENT)) {
            return;
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }
        CompoundTag compoundTag = customData.copyTag();
        Optional<CompoundTag> deathOptional = compoundTag.getCompound("Death");
        if (deathOptional.isEmpty()) {
            return;
        }
        CompoundTag death = deathOptional.get();
        DeathInfo info = new DeathInfo(death.read("PlayerUUID", UUIDUtil.CODEC).orElse(Util.NIL_UUID), death.read("DeathID", UUIDUtil.CODEC).orElse(Util.NIL_UUID));
        compoundTag.remove("Death");
        if (compoundTag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));
        }
        stack.set(GravestoneMod.DEATH_DATA_COMPONENT, info);
    }
}
