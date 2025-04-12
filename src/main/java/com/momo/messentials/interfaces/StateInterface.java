package com.momo.messentials.interfaces;

import net.minecraft.world.PersistentStateManager;

public interface StateInterface {
    Object get(PersistentStateManager serverState);
}