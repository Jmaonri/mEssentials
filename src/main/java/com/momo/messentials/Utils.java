package com.momo.messentials;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import java.util.Objects;

public class Utils {
    /**
     * Converts a dimension string to a {@link ServerWorld} instance.
     * This method attempts to parse the provided dimension string into an {@link Identifier},
     * then converts it into a {@link RegistryKey<World>} to retrieve the corresponding {@link ServerWorld} from the server.
     *
     * @param server The {@link MinecraftServer} instance used to retrieve the world.
     * @param dimension The string representation of the dimension, typically the dimension's identifier.
     * @return The {@link ServerWorld} corresponding to the provided dimension string, or {@code null} if the dimension is invalid.
     * @throws NullPointerException If the dimension string is invalid or cannot be parsed to a valid world key.
     */
    public static ServerWorld DimensionStringToServerWorld(MinecraftServer server, String dimension){
        Identifier dimensionId = Identifier.tryParse(dimension);
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, dimensionId);
        return server.getWorld(worldKey);
    }

    public static ServerWorld GetOverworld(ServerPlayerEntity player) throws NullPointerException{
        return Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
    }
}