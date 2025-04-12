package com.momo.messentials.controller;

import com.momo.messentials.data.PreviousPlayerLocationData;
import net.minecraft.server.command.ServerCommandSource;
import com.momo.messentials.service.ServerStateService;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.context.CommandContext;
import com.momo.messentials.service.PlayerService;
import net.minecraft.server.world.ServerWorld;
import exception.NoLocationSavedException;
import net.minecraft.util.Formatting;
import com.momo.messentials.Utils;
import net.minecraft.text.Text;
import java.util.*;

import static net.minecraft.data.DataProvider.LOGGER;

public class CommandController {
    ServerStateService serverStateService;

    public CommandController(ServerStateService serverStateService){
        this.serverStateService = serverStateService;
    }

    public int HandleSpawnCommand(ServerPlayerEntity player, ServerWorld overworld, CommandContext<ServerCommandSource> context){
        try{
            serverStateService.SetPlayerPreviousLocation(player, overworld.getPersistentStateManager());
            PlayerService.TeleportPlayer(player, overworld.getSpawnPos().getX(), overworld.getSpawnPos().getY(), overworld.getSpawnPos().getZ(), player.getYaw(), player.getPitch(), overworld);
        }catch (NullPointerException ex) {
            player.sendMessage(Text.literal("There was an unexpected error trying to teleport.").formatted(Formatting.RED));
            LOGGER.error(Arrays.toString(ex.getStackTrace()));
        }

        return 0;
    }

    public int HandleBackCommand(ServerPlayerEntity player, ServerWorld overworld, CommandContext<ServerCommandSource> context){
        try {
            PreviousPlayerLocationData playerLastLocation = serverStateService.getPreviousPlayerLocationByUUID(player.getUuid(), overworld.getPersistentStateManager());
            serverStateService.SetPlayerPreviousLocation(player, overworld.getPersistentStateManager());

            ServerWorld dimension = Utils.DimensionStringToServerWorld(Objects.requireNonNull(player.getServer()), playerLastLocation.dimension);
            PlayerService.TeleportPlayer(player, playerLastLocation.x, playerLastLocation.y, playerLastLocation.z, playerLastLocation.yaw, playerLastLocation.pitch, dimension);
        }
        catch (NoLocationSavedException ex){
            player.sendMessage(Text.literal("No back location saved.").formatted(Formatting.RED));
        }
        catch (Exception e) {
            player.sendMessage(Text.literal("There was an unexpected error trying to teleport back.").formatted(Formatting.RED));
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        }

        return 0;
    }
}