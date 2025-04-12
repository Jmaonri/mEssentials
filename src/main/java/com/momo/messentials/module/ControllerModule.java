package com.momo.messentials.module;

import com.momo.messentials.controller.PlayerHomeController;
import com.momo.messentials.controller.CommandController;

public class ControllerModule {
    public PlayerHomeController playerHomeController;
    public CommandController commandController;

    public ControllerModule(ServiceModule serviceModule) {
        playerHomeController = new PlayerHomeController(serviceModule.serverStateService);
        commandController = new CommandController(serviceModule.serverStateService);
    }
}