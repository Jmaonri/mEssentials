package com.momo.messentials.module;

import com.momo.messentials.service.ServerStateService;

public class ServiceModule {
    public ServerStateService serverStateService;

    public ServiceModule(StateModule stateModule) {
        serverStateService = new ServerStateService(stateModule.previousPlayerLocationState, stateModule.playerHomeState);
    }
}