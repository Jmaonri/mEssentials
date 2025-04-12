package com.momo.messentials;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import com.momo.messentials.module.ControllerModule;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import java.util.HashMap;

public class Router {

    @FunctionalInterface
    interface CommandHandler {
        int execute(ServerPlayerEntity player, ServerWorld overworld, CommandContext<ServerCommandSource> commandContext);
        default int handle(CommandContext<ServerCommandSource> commandContext) {
            try{
                ServerPlayerEntity player = commandContext.getSource().getPlayer();
                if(player == null) return 0;

                ServerWorld world = Utils.GetOverworld(player);
                return execute(player, world, commandContext);
            } catch (Exception e){
                commandContext.getSource().sendError(Text.literal("Could not get player from context.").formatted(Formatting.RED));
            }

            return 0;
        }
    }

    // HashMap containing all registered routes.
    private final HashMap<String, CommandHandler> routes = new HashMap<>();

    // Register command routes here.
    public Router(ControllerModule controllerModule) {
        routes.put("spawn", controllerModule.commandController::HandleSpawnCommand);
        routes.put("back", controllerModule.commandController::HandleBackCommand);

        routes.put("home", controllerModule.playerHomeController::TeleportPlayerToHome);
        routes.put("home.set", controllerModule.playerHomeController::SetPlayerHome);
    }

    /**
     * Registers all command routes by splitting them into root commands and subcommands,
     * and then registering each one with the {@link CommandDispatcher}.
     * The method handles both root commands and subcommands by splitting the command string on a period ('.').
     * If the command contains a subcommand, it will be registered as a child of the root command.
     * The method ensures that each root command is registered only once, and it associates subcommands correctly.
     *
     * @param dispatcher The {@link CommandDispatcher} that will be used to register commands.
     */
    public void RegisterCommandRoutes(CommandDispatcher<ServerCommandSource> dispatcher){
        HashMap<String, LiteralArgumentBuilder<ServerCommandSource>> rootLiterals = new HashMap<>();

        routes.forEach((key, handler) -> {
            String[] parts = key.split("\\.");
            if (parts.length == 1) {
                dispatcher.register(CommandManager.literal(parts[0]).executes(handler::handle));
                return;
            }

            String rootCommand = parts[0];
            String subcommand = parts[1];

            // Create root command if it doesn't exist
            rootLiterals.putIfAbsent(rootCommand, CommandManager.literal(rootCommand));

            rootLiterals.get(rootCommand).then(CommandManager.literal(subcommand).executes(handler::handle));
        });

        rootLiterals.values().forEach(dispatcher::register);
    }
}