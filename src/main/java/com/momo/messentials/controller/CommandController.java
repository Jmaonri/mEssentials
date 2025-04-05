package com.momo.messentials.controller;

import com.momo.messentials.state.PreviousPlayerLocationState;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.context.CommandContext;
import com.momo.messentials.data.LastLocationData;
import com.momo.messentials.service.PlayerService;
import net.minecraft.server.world.ServerWorld;
import exception.NoLocationSavedException;
import net.minecraft.util.Formatting;
import com.momo.messentials.Utils;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import java.util.*;

import static net.minecraft.data.DataProvider.LOGGER;

public class CommandController {

    /**
     * Handles the `/spawn` command execution for a player.
     * Teleports the player to the world's spawn point while saving their current location for potential use with the `/back` command.
     *
     * @param context The command context that provides the source (player) executing the command.
     * @return 1 if the command executes successfully, indicating success.
     */
    public int HandleSpawnCommand(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player == null) return 0;

        try{
            ServerWorld serverWorld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
            PreviousPlayerLocationState state = new PreviousPlayerLocationState().get(Objects.requireNonNull(serverWorld).getPersistentStateManager());

            state.setPlayerLocation(player);

            ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
            PlayerService.TeleportPlayer(player, serverWorld.getSpawnPos().getX(), serverWorld.getSpawnPos().getY(), serverWorld.getSpawnPos().getZ(), player.getYaw(), player.getPitch(), overworld);
        }catch (NullPointerException ex) {
            player.sendMessage(Text.literal("There was an unexpected error trying to teleport.").formatted(Formatting.RED));
            LOGGER.error(Arrays.toString(ex.getStackTrace()));
        }

        return 1;
    }

    /**
     * Handles the `/back` command execution for a player.
     * Teleports the player back to their previously saved location, if available.
     * If no location is saved, the player is notified with an error message.
     *
     * @param context The command context that provides the source (player) executing the command.
     * @return 1 if the command executes successfully, indicating success.
     */
    public int HandleBackCommand(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player == null) return 0;

        try {
            ServerWorld serverWorld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
            PreviousPlayerLocationState state = new PreviousPlayerLocationState().get(Objects.requireNonNull(serverWorld).getPersistentStateManager());
            LastLocationData playerLastLocation = state.getPlayerLocation(player.getUuid());

            state.setPlayerLocation(player);

            ServerWorld dimension = Utils.DimensionStringToServerWorld(player.getServer(), playerLastLocation.dimension);
            PlayerService.TeleportPlayer(player, playerLastLocation.x, playerLastLocation.y, playerLastLocation.z, playerLastLocation.yaw, playerLastLocation.pitch, dimension);
        }
        catch (NoLocationSavedException ex){
            player.sendMessage(Text.literal("No back location saved.").formatted(Formatting.RED));
        }
        catch (Exception e) {
            player.sendMessage(Text.literal("There was an unexpected error trying to teleport back.").formatted(Formatting.RED));
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        }

        return 1;
    }
}

