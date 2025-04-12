package com.momo.messentials.controller;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import com.momo.messentials.service.ServerStateService;
import com.mojang.brigadier.context.CommandContext;
import com.momo.messentials.service.PlayerService;
import com.momo.messentials.data.PlayerHomeData;
import net.minecraft.server.world.ServerWorld;
import exception.NoLocationSavedException;
import net.minecraft.util.Formatting;
import com.momo.messentials.Utils;
import net.minecraft.text.Text;
import java.util.Objects;
import java.util.Arrays;

import static net.minecraft.data.DataProvider.LOGGER;

public class PlayerHomeController {
    ServerStateService serverStateService;

    public PlayerHomeController(ServerStateService serverStateService) {
        this.serverStateService = serverStateService;
    }

    public int SetPlayerHome(ServerPlayerEntity player, ServerWorld overworld, CommandContext<ServerCommandSource> context){
        try{
            serverStateService.SetPlayerHome(player, overworld.getPersistentStateManager());

            player.sendMessage(Text.literal("Home set").formatted(Formatting.AQUA));
        } catch (Exception e) {
            player.sendMessage(Text.literal("There was an unexpected error trying to set player home.").formatted(Formatting.RED));
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        }

        return 0;
    }

    public int TeleportPlayerToHome(ServerPlayerEntity player, ServerWorld overworld, CommandContext<ServerCommandSource> context){
        try{
            PlayerHomeData playerHome = serverStateService.GetPlayerHomeDataByUUID(player.getUuid(), overworld.getPersistentStateManager());
            serverStateService.SetPlayerPreviousLocation(player, overworld.getPersistentStateManager());

            ServerWorld dimension = Utils.DimensionStringToServerWorld(Objects.requireNonNull(player.getServer()), playerHome.Dimension);
            PlayerService.TeleportPlayer(player, playerHome.XCoordinate, playerHome.YCoordinate, playerHome.ZCoordinate, playerHome.PlayerYaw, playerHome.PlayerPitch, dimension);
        }
        catch (NoLocationSavedException ex){
            player.sendMessage(Text.literal("No home set. Set a home by doing /home set").formatted(Formatting.YELLOW));
        }
        catch (Exception e) {
            player.sendMessage(Text.literal("There was an unexpected error trying to teleport home.").formatted(Formatting.RED));
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        }

        return 0;
    }
}