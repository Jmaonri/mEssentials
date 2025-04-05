package com.momo.messentials.service;

import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.EnumSet;
import java.util.Set;

public class PlayerService {

    /**
     * Teleports the specified player to the given coordinates in the specified dimension.
     * The player's position, yaw, and pitch are updated based on the provided values.
     * This method ensures that the teleportation occurs without any additional position flags.
     *
     * @param player The player to teleport.
     * @param xCoordinate The X-coordinate to teleport the player to.
     * @param yCoordinate The Y-coordinate to teleport the player to.
     * @param zCoordinate The Z-coordinate to teleport the player to.
     * @param playerYaw The yaw (horizontal rotation) of the player after teleportation.
     * @param playerPitch The pitch (vertical rotation) of the player after teleportation.
     * @param dimension The `ServerWorld` dimension where the player will be teleported.
     */
    public static void TeleportPlayer(ServerPlayerEntity player, int xCoordinate, int yCoordinate, int zCoordinate, float playerYaw, float playerPitch, ServerWorld dimension){
        Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
        player.teleport(dimension, xCoordinate, yCoordinate, zCoordinate, flags, playerYaw, playerPitch, false);
    }
}
