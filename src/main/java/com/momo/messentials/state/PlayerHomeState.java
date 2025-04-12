package com.momo.messentials.state;

import net.minecraft.server.network.ServerPlayerEntity;
import com.momo.messentials.interfaces.StateInterface;
import net.minecraft.world.PersistentStateManager;
import com.momo.messentials.data.PlayerHomeData;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;
import exception.NoLocationSavedException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class PlayerHomeState extends PersistentState implements StateInterface {
    // HashMap containing all players homes.
    private final Map<UUID, PlayerHomeData> playerHomes = new HashMap<>();
    // The name under which the player home nbt data is saved.
    private final String NbtKeyName = "players_home_locations";

    /**
     * Writes the player home data to an NBT compound.
     * This method serializes the player home data (dimension, coordinates, and UUID) into an NBT list
     * that can be stored in the persistent world data.
     *
     * @param nbt The NBT compound to write the data to.
     * @param registries The registry lookup for the world registries.
     * @return The updated NBT compound containing the serialized player home data.
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtList list = new NbtList();
        for (Map.Entry<UUID, PlayerHomeData> entry : playerHomes.entrySet()) {
            NbtCompound tag = new NbtCompound();

            tag.putString("player_dimension", entry.getValue().Dimension);
            tag.putDouble("x_coordinate", entry.getValue().XCoordinate);
            tag.putDouble("y_coordinate", entry.getValue().YCoordinate);
            tag.putDouble("z_coordinate", entry.getValue().ZCoordinate);
            tag.putFloat("player_pitch", entry.getValue().PlayerPitch);
            tag.putFloat("player_yaw", entry.getValue().PlayerYaw);
            tag.putString("UUID", entry.getKey().toString());

            list.add(tag);
        }
        nbt.put(NbtKeyName, list);
        return nbt;
    }

    /**
     * Sets the player's current location as their home.
     * This method saves the player's position (coordinates, dimension, yaw, and pitch)
     * in a map, associating it with their unique UUID.
     *
     * @param player The player whose location is being set as their home.
     */
    public void setPlayerHomeLocation(ServerPlayerEntity player) {
        var pos = player.getPos();
        String dimension = player.getWorld().getRegistryKey().getValue().toString();
        playerHomes.put(player.getUuid(), new PlayerHomeData((int) pos.x, (int) pos.y, (int) pos.z, dimension, player.getYaw(), player.getPitch()));
        markDirty();
    }

    /**
     * Retrieves the home location data for a player based on their UUID.
     * This method checks if the provided UUID has a saved home location and returns the corresponding data.
     * If no home is saved for the player, it throws a `NoLocationSavedException`.
     *
     * @param uuid The UUID of the player whose home data is being retrieved.
     * @return The `PlayerHomeData` associated with the player's UUID.
     * @throws NoLocationSavedException If no home location is saved for the provided UUID.
     */
    public PlayerHomeData getPlayerHome(UUID uuid) throws NoLocationSavedException {
        if(!playerHomes.containsKey(uuid)) throw new NoLocationSavedException("There is no saved home location for provided user uuid.");
        return playerHomes.get(uuid);
    }

    /**
     * Creates a `PlayerHomeState` object from the provided NBT data.
     * This method deserializes the player home data from the NBT compound and populates the `PlayerHomeState` instance.
     * It reads the player's home information such as coordinates, dimension, and rotation values, and stores them
     * in a map using the player's UUID as the key.
     *
     * @param tag The NBT compound containing the serialized player home data.
     * @param registryLookup The registry lookup for world registries (unused in this method but required for compatibility).
     * @return A `PlayerHomeState` instance populated with player home data from the NBT compound.
     */
    public static PlayerHomeState createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        PlayerHomeState state = new PlayerHomeState();

        if (!tag.contains(state.NbtKeyName)) return state;

        NbtList list = tag.getList(state.NbtKeyName, NbtElement.COMPOUND_TYPE);

        for (NbtElement element : list) {
            NbtCompound compound = (NbtCompound) element;

            String dimension = compound.getString("player_dimension");
            UUID uuid = UUID.fromString(compound.getString("UUID"));
            float pitch = compound.getFloat("player_pitch");
            float yaw = compound.getFloat("player_yaw");
            int x = compound.getInt("x_coordinate");
            int y = compound.getInt("y_coordinate");
            int z = compound.getInt("z_coordinate");

            state.playerHomes.put(uuid, new PlayerHomeData(x, y, z, dimension, yaw, pitch));
        }

        return state;
    }

    /**
     * Creates and returns a new instance of `PlayerHomeState`.
     * This method is used to instantiate a fresh `PlayerHomeState` object with no pre-existing data.
     *
     * @return A new `PlayerHomeState` instance.
     */
    public static PlayerHomeState createNew(){
        return new PlayerHomeState();
    }

    /**
     * Defines the persistent state type for `PlayerHomeState`.
     * This static field provides a reference to the `PersistentState.Type` for `PlayerHomeState`, which allows
     * it to be registered and managed by the server's persistent state system.
     * The `createNew` method is used to create a fresh instance, while `createFromNbt` handles the deserialization
     * of the state from NBT data.
     */
    private static final PersistentState.Type<PlayerHomeState> type = new PersistentState.Type<>(
            PlayerHomeState::createNew,
            PlayerHomeState::createFromNbt,
            null
    );

    /**
     * Retrieves or creates the `PlayerHomeState` from the persistent state manager.
     * This method fetches the `PlayerHomeState` associated with the given server state. If it does not exist,
     * a new instance is created and registered with the provided type and NBT key name.
     *
     * @param serverState The persistent state manager to retrieve or create the state from.
     * @return The `PlayerHomeState` instance, either retrieved from the persistent state or newly created.
     */
    public PlayerHomeState get(PersistentStateManager serverState) {
        return serverState.getOrCreate(type, NbtKeyName);
    }
}