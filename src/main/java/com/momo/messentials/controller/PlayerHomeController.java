package com.momo.messentials.controller;

import com.momo.messentials.state.PreviousPlayerLocationState;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.context.CommandContext;
import com.momo.messentials.service.PlayerService;
import com.momo.messentials.state.PlayerHomeState;
import com.momo.messentials.data.PlayerHomeData;
import net.minecraft.server.world.ServerWorld;
import exception.NoLocationSavedException;
import net.minecraft.util.Formatting;
import com.momo.messentials.Utils;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import java.util.Objects;
import java.util.Arrays;

import static net.minecraft.data.DataProvider.LOGGER;

public class PlayerHomeController {

    /**
     * Handles the `/home set` command execution for a player.
     * Sets the player's current location as their home location.
     * Notifies the player once their home has been set successfully.
     *
     * @param context The command context that provides the source (player) executing the command.
     * @return 1 if the command executes successfully, indicating success.
     */
    public int SetPlayerHome(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player == null) return 0;

        try{
            ServerWorld serverWorld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
            PlayerHomeState state = new PlayerHomeState().get(Objects.requireNonNull(serverWorld).getPersistentStateManager());

            state.setPlayerHomeLocation(player);

            player.sendMessage(Text.literal("Home set").formatted(Formatting.AQUA));
        } catch (Exception e) {
            player.sendMessage(Text.literal("There was an unexpected error trying to set player home.").formatted(Formatting.RED));
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        }

        return 1;
    }

    /**
     * Handles the `/home` command execution to teleport the player to their saved home location.
     * Teleports the player to their home if set and saves their current location for potential use with the `/back` command.
     * If no home is set, the player is notified with an error message.
     *
     * @param context The command context that provides the source (player) executing the command.
     * @return 1 if the command executes successfully, indicating success.
     */
    public int TeleportPlayerToHome(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if(player == null) return 0;

        try{
            ServerWorld serverWorld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
            PlayerHomeState state = new PlayerHomeState().get(Objects.requireNonNull(serverWorld).getPersistentStateManager());
            PreviousPlayerLocationState locationState = new PreviousPlayerLocationState().get(serverWorld.getPersistentStateManager());

            PlayerHomeData playerHome = state.getPlayerHome(player.getUuid());
            locationState.setPlayerLocation(player);

            ServerWorld dimension = Utils.DimensionStringToServerWorld(player.getServer(), playerHome.Dimension);
            PlayerService.TeleportPlayer(player, playerHome.XCoordinate, playerHome.YCoordinate, playerHome.ZCoordinate, playerHome.PlayerYaw, playerHome.PlayerPitch, dimension);
        }
        catch (NoLocationSavedException ex){
            player.sendMessage(Text.literal("No home set. Set a home by doing /home set").formatted(Formatting.YELLOW));
        }
        catch (Exception e) {
            player.sendMessage(Text.literal("There was an unexpected error trying to teleport home.").formatted(Formatting.RED));
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        }

        return 1;
    }
}
