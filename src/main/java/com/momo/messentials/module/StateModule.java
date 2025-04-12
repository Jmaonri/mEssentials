package com.momo.messentials.module;

import com.momo.messentials.state.PreviousPlayerLocationState;
import com.momo.messentials.state.PlayerHomeState;

public class StateModule {
    public PreviousPlayerLocationState previousPlayerLocationState;
    public PlayerHomeState playerHomeState;

    public StateModule(){
        previousPlayerLocationState = new PreviousPlayerLocationState();
        playerHomeState = new PlayerHomeState();
    }
}