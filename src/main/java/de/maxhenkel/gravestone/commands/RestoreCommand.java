package de.maxhenkel.gravestone.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.death.DeathManager;
import de.maxhenkel.gravestone.Main;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class RestoreCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalBuilder = Commands.literal("restore").requires((commandSource) -> commandSource.hasPermission(2));

        Command<CommandSourceStack> add = (commandSource) -> {
            UUID deathID = UuidArgument.getUuid(commandSource, "death_id");
            ServerPlayer player = EntityArgument.getPlayer(commandSource, "target");
            Death death = DeathManager.getDeath(player.getLevel(), deathID);
            if (death == null) {
                commandSource.getSource().sendFailure(Component.translatable("message.gravestone.death_id_not_found", deathID.toString()));
                return 0;
            }
            for (ItemStack stack : death.getAllItems()) {
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
            commandSource.getSource().sendSuccess(Component.translatable("message.gravestone.restore.success", player.getDisplayName()), true);
            return 1;
        };

        Command<CommandSourceStack> replace = (commandSource) -> {
            UUID deathID = UuidArgument.getUuid(commandSource, "death_id");
            ServerPlayer player = EntityArgument.getPlayer(commandSource, "target");
            Death death = DeathManager.getDeath(player.getLevel(), deathID);
            if (death == null) {
                commandSource.getSource().sendFailure(Component.translatable("message.gravestone.death_id_not_found", deathID.toString()));
                return 0;
            }
            player.getInventory().clearContent();
            NonNullList<ItemStack> itemStacks = Main.GRAVESTONE.get().fillPlayerInventory(player, death);
            for (ItemStack stack : itemStacks) {
                player.drop(stack, false);
            }
            commandSource.getSource().sendSuccess(Component.translatable("message.gravestone.restore.success", player.getDisplayName()), true);
            return 1;
        };

        literalBuilder
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("death_id", UuidArgument.uuid())
                                .then(Commands.literal("replace").executes(replace))
                                .then(Commands.literal("add").executes(add))
                        ));

        LiteralCommandNode<CommandSourceStack> register = dispatcher.register(literalBuilder);
        LiteralArgumentBuilder<CommandSourceStack> alias = Commands.literal("restoreinventory").redirect(register);
        dispatcher.register(alias);
    }

}
