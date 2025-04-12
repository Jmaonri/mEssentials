package com.momo.messentials.service;

import com.momo.messentials.state.PreviousPlayerLocationState;
import com.momo.messentials.data.PreviousPlayerLocationData;
import net.minecraft.server.network.ServerPlayerEntity;
import com.momo.messentials.interfaces.StateInterface;
import com.momo.messentials.state.PlayerHomeState;
import net.minecraft.world.PersistentStateManager;
import com.momo.messentials.data.PlayerHomeData;
import exception.NoLocationSavedException;
import java.util.UUID;
import java.util.Map;

public class ServerStateService {
    PreviousPlayerLocationState previousPlayerLocationState;
    PlayerHomeState playerHomeState;

    private enum StateKeys{
        PLAYER_LAST_LOCATION_STATE,
        PLAYER_HOME_STATE
    }

    private final Map<StateKeys, StateInterface> StateKeysMap;

    public ServerStateService(PreviousPlayerLocationState previousPlayerLocationState, PlayerHomeState playerHomeState) {
        this.previousPlayerLocationState = previousPlayerLocationState;
        this.playerHomeState = playerHomeState;

        StateKeysMap = Map.of(
                StateKeys.PLAYER_LAST_LOCATION_STATE, previousPlayerLocationState,
                StateKeys.PLAYER_HOME_STATE, playerHomeState
        );
    }

    private Object GetOrCreateState(StateKeys stateKey, PersistentStateManager persistentStateManager) {
        var state = StateKeysMap.get(stateKey);
        if(state == null) throw new NullPointerException("State Key returned null value");

        return state.get(persistentStateManager);
    }

    public void SetPlayerPreviousLocation(ServerPlayerEntity player, PersistentStateManager persistentStateManager){
        previousPlayerLocationState = (PreviousPlayerLocationState) GetOrCreateState(StateKeys.PLAYER_LAST_LOCATION_STATE, persistentStateManager);
        previousPlayerLocationState.setPlayerLocation(player);
    }

    public PreviousPlayerLocationData getPreviousPlayerLocationByUUID(UUID playerUUID, PersistentStateManager persistentStateManager) throws NoLocationSavedException {
        previousPlayerLocationState = (PreviousPlayerLocationState) GetOrCreateState(StateKeys.PLAYER_LAST_LOCATION_STATE, persistentStateManager);
        return previousPlayerLocationState.getPlayerLocation(playerUUID);
    }

    public void SetPlayerHome(ServerPlayerEntity player, PersistentStateManager persistentStateManager){
        playerHomeState = (PlayerHomeState) GetOrCreateState(StateKeys.PLAYER_HOME_STATE, persistentStateManager);
        playerHomeState.setPlayerHomeLocation(player);
    }

    public PlayerHomeData GetPlayerHomeDataByUUID(UUID playerUUID, PersistentStateManager persistentStateManager) throws NoLocationSavedException {
        playerHomeState = (PlayerHomeState) GetOrCreateState(StateKeys.PLAYER_HOME_STATE, persistentStateManager);
        return playerHomeState.getPlayerHome(playerUUID);
    }
}