package com.momo.messentials;

import com.momo.messentials.controller.PlayerHomeController;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.momo.messentials.controller.CommandController;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import com.mojang.brigadier.CommandDispatcher;
import java.util.HashMap;

public class Router {

    @FunctionalInterface
    interface CommandHandler {
        int handle(CommandContext<ServerCommandSource> commandContext);
    }

    // HashMap containing all registered routes.
    private final HashMap<String, CommandHandler> routes = new HashMap<>();

    // Register command routes here.
    public Router() {
        CommandController commandController = new CommandController();
        PlayerHomeController playerHomeController = new PlayerHomeController();

        routes.put("spawn", commandController::HandleSpawnCommand);
        routes.put("back", commandController::HandleBackCommand);

        routes.put("home", playerHomeController::TeleportPlayerToHome);
        routes.put("home.set", playerHomeController::SetPlayerHome);
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
