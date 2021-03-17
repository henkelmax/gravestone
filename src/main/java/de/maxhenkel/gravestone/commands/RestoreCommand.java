package de.maxhenkel.gravestone.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.maxhenkel.corelib.death.Death;
import de.maxhenkel.corelib.death.DeathManager;
import de.maxhenkel.gravestone.Main;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.UUIDArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class RestoreCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> literalBuilder = Commands.literal("restore").requires((commandSource) -> commandSource.hasPermission(2));

        Command<CommandSource> add = (commandSource) -> {
            UUID deathID = UUIDArgument.getUuid(commandSource, "death_id");
            ServerPlayerEntity player = EntityArgument.getPlayer(commandSource, "target");
            Death death = DeathManager.getDeath(player.getLevel(), deathID);
            if (death == null) {
                commandSource.getSource().sendFailure(new TranslationTextComponent("message.gravestone.death_id_not_found", deathID.toString()));
                return 0;
            }
            for (ItemStack stack : death.getAllItems()) {
                if (!player.inventory.add(stack)) {
                    player.drop(stack, false);
                }
            }
            commandSource.getSource().sendSuccess(new TranslationTextComponent("message.gravestone.restore.success", player.getDisplayName()), true);
            return 1;
        };

        Command<CommandSource> replace = (commandSource) -> {
            UUID deathID = UUIDArgument.getUuid(commandSource, "death_id");
            ServerPlayerEntity player = EntityArgument.getPlayer(commandSource, "target");
            Death death = DeathManager.getDeath(player.getLevel(), deathID);
            if (death == null) {
                commandSource.getSource().sendFailure(new TranslationTextComponent("message.gravestone.death_id_not_found", deathID.toString()));
                return 0;
            }
            player.inventory.clearContent();
            NonNullList<ItemStack> itemStacks = Main.GRAVESTONE.fillPlayerInventory(player, death);
            for (ItemStack stack : itemStacks) {
                player.drop(stack, false);
            }
            commandSource.getSource().sendSuccess(new TranslationTextComponent("message.gravestone.restore.success", player.getDisplayName()), true);
            return 1;
        };

        literalBuilder
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("death_id", UUIDArgument.uuid())
                                .then(Commands.literal("replace").executes(replace))
                                .then(Commands.literal("add").executes(add))
                        ));

        dispatcher.register(literalBuilder);
    }

}
