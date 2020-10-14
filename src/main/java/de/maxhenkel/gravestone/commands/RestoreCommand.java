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
        LiteralArgumentBuilder<CommandSource> literalBuilder = Commands.literal("restore").requires((commandSource) -> commandSource.hasPermissionLevel(2));

        Command<CommandSource> add = (commandSource) -> {
            UUID deathID = UUIDArgument.func_239195_a_(commandSource, "death_id");
            ServerPlayerEntity player = EntityArgument.getPlayer(commandSource, "target");
            Death death = DeathManager.getDeath(player.getServerWorld(), deathID);
            if (death == null) {
                commandSource.getSource().sendErrorMessage(new TranslationTextComponent("message.gravestone.death_id_not_found", deathID.toString()));
                return 0;
            }
            for (ItemStack stack : death.getAllItems()) {
                if (!player.inventory.addItemStackToInventory(stack)) {
                    player.dropItem(stack, false);
                }
            }
            commandSource.getSource().sendFeedback(new TranslationTextComponent("message.gravestone.restore.success", player.getDisplayName()), true);
            return 1;
        };

        Command<CommandSource> replace = (commandSource) -> {
            UUID deathID = UUIDArgument.func_239195_a_(commandSource, "death_id");
            ServerPlayerEntity player = EntityArgument.getPlayer(commandSource, "target");
            Death death = DeathManager.getDeath(player.getServerWorld(), deathID);
            if (death == null) {
                commandSource.getSource().sendErrorMessage(new TranslationTextComponent("message.gravestone.death_id_not_found", deathID.toString()));
                return 0;
            }
            player.inventory.clear();
            NonNullList<ItemStack> itemStacks = Main.GRAVESTONE.fillPlayerInventory(player, death);
            for (ItemStack stack : itemStacks) {
                player.dropItem(stack, false);
            }
            commandSource.getSource().sendFeedback(new TranslationTextComponent("message.gravestone.restore.success", player.getDisplayName()), true);
            return 1;
        };

        literalBuilder
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("death_id", UUIDArgument.func_239194_a_())
                                .then(Commands.literal("replace").executes(replace))
                                .then(Commands.literal("add").executes(add))
                        ));

        dispatcher.register(literalBuilder);
    }

}
