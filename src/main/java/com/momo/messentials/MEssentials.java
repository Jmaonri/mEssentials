package com.momo.messentials;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.momo.messentials.module.ControllerModule;
import com.momo.messentials.module.ServiceModule;
import com.momo.messentials.module.StateModule;
import net.fabricmc.api.ModInitializer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class MEssentials implements ModInitializer {
	public static final String MOD_ID = "messentials";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		try{
			CommandRegistrationCallback.EVENT.register((
					dispatcher,
					registryAccess,
					environment
			) -> {
				StateModule stateModule = new StateModule();
				ServiceModule serviceModule = new ServiceModule(stateModule);
				ControllerModule controllerModule = new ControllerModule(serviceModule);

				Router router = new Router(controllerModule);
				router.RegisterCommandRoutes(dispatcher);
			});
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
}