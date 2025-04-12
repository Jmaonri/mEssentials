package com.momo.messentials.state;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentStateManager;
import com.momo.messentials.data.PreviousPlayerLocationData;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;
import exception.NoLocationSavedException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class PreviousPlayerLocationState extends PersistentState {
    // HashMap containing all last location's homes.
    private final Map<UUID, PreviousPlayerLocationData> previousPlayerLocations = new HashMap<>();
    // The name under which the player last location nbt data is saved.
    private final String NbtKeyName = "players_last_locations";

    /**
     * Serializes the state of the previous player locations to an NBT compound.
     * This method converts the stored player locations (with their dimensions, coordinates, yaw, and pitch)
     * into an NBT format for persistent saving.
     *
     * @param nbt The NBT compound to which the player locations will be written.
     * @param registries A lookup for the registries, unused in this method but passed for compatibility.
     * @return The updated NBT compound containing the player locations.
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtList list = new NbtList();
        for (Map.Entry<UUID, PreviousPlayerLocationData> entry : previousPlayerLocations.entrySet()) {
            NbtCompound tag = new NbtCompound();

            tag.putString("player_dimension", entry.getValue().dimension);
            tag.putFloat("player_pitch", entry.getValue().pitch);
            tag.putDouble("x_coordinate", entry.getValue().x);
            tag.putDouble("y_coordinate", entry.getValue().y);
            tag.putDouble("z_coordinate", entry.getValue().z);
            tag.putFloat("player_yaw", entry.getValue().yaw);
            tag.putString("UUID", entry.getKey().toString());

            list.add(tag);
        }
        nbt.put(NbtKeyName, list);
        return nbt;
    }

    /**
     * Saves the current location (coordinates, dimension, yaw, and pitch) of the given player.
     * This method captures the player's position and stores it in the `previousPlayerLocations` map,
     * associating it with the player's unique UUID.
     * It marks the state as dirty to ensure that it will be saved persistently.
     *
     * @param player The player whose location is to be saved.
     */
    public void setPlayerLocation(ServerPlayerEntity player) {
        var pos = player.getPos();
        String dimension = player.getWorld().getRegistryKey().getValue().toString();
        previousPlayerLocations.put(player.getUuid(), new PreviousPlayerLocationData((int) pos.x, (int) pos.y, (int) pos.z, dimension, player.getYaw(), player.getPitch()));
        markDirty();
    }

    /**
     * Retrieves the previously saved location of a player based on their UUID.
     * This method looks up the player's last saved location from the `previousPlayerLocations` map.
     * If no location is found for the provided UUID, a {@link NoLocationSavedException} is thrown.
     *
     * @param uuid The UUID of the player whose last location is to be retrieved.
     * @return The saved last location data for the player.
     * @throws NoLocationSavedException If no location is found for the provided player UUID.
     */
    public PreviousPlayerLocationData getPlayerLocation(UUID uuid) throws NoLocationSavedException {
        if(!previousPlayerLocations.containsKey(uuid)) throw new NoLocationSavedException("There is no saved previous location for provided user uuid.");
        return previousPlayerLocations.get(uuid);
    }

    /**
     * Creates an instance of {@link PreviousPlayerLocationState} from an NBT compound.
     * This method reads the saved player location data from the provided NBT compound and populates
     * the `previousPlayerLocations` map with the data for each player.
     *
     * @param tag The NBT compound containing the saved data for previous player locations.
     * @param registryLookup The registry lookup for handling additional resource registrations (not used in this method).
     * @return A new {@link PreviousPlayerLocationState} instance with the loaded data.
     */
    public static PreviousPlayerLocationState createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        PreviousPlayerLocationState state = new PreviousPlayerLocationState();

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

            state.previousPlayerLocations.put(uuid, new PreviousPlayerLocationData(x, y, z, dimension, yaw, pitch));
        }

        return state;
    }

    /**
     * Creates a new instance of {@link PreviousPlayerLocationState}.
     * This method initializes and returns a fresh {@link PreviousPlayerLocationState} object,
     * which has no saved player locations by default.
     *
     * @return A new {@link PreviousPlayerLocationState} instance.
     */
    public static PreviousPlayerLocationState createNew(){
        return new PreviousPlayerLocationState();
    }

    /**
     * A {@link Type} instance that defines how the {@link PreviousPlayerLocationState} is created
     * and how it is deserialized from NBT data.
     * This field is used by the {@link PersistentStateManager} to manage the lifecycle of the
     * {@link PreviousPlayerLocationState}, including loading it from NBT data and creating new instances
     * when necessary.
     * The type is configured with:
     * - A factory method {@link PreviousPlayerLocationState#createNew} to create a new instance of the state.
     * - A deserialization method {@link PreviousPlayerLocationState#createFromNbt} to read the state from NBT.
     * - The third parameter is set to {@code null} as no data fixer is provided.
     */
    private static final Type<PreviousPlayerLocationState> type = new Type<>(
            PreviousPlayerLocationState::createNew,
            PreviousPlayerLocationState::createFromNbt,
            null
    );

    /**
     * Retrieves or creates an instance of {@link PreviousPlayerLocationState} from the
     * {@link PersistentStateManager}. If the state does not already exist, it will be created
     * using the {@link PreviousPlayerLocationState#type} configuration.
     * This method ensures that the state is loaded or initialized, depending on whether it already exists
     * in the persistent storage, allowing for access to the player's previous location data.
     *
     * @param serverState The {@link PersistentStateManager} responsible for managing state data.
     * @return The {@link PreviousPlayerLocationState} instance containing the player's previous location data.
     */
    public PreviousPlayerLocationState get(PersistentStateManager serverState) {
        return serverState.getOrCreate(type, NbtKeyName);
    }
}

