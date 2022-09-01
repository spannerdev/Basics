package com.spanner.basics;

import com.spanner.basics.command.GamemodeCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.extensions.Extension;
import org.slf4j.Logger;

public class Basics extends Extension {

	Logger logger;

	public void load() {
		this.logger = MinecraftServer.LOGGER;
		logger.info("Hello from Basics v0.1.0");
	}

	public void loadCommands() {
		CommandManager commandManager =	MinecraftServer.getCommandManager();

		commandManager.register(new GamemodeCommand());
	}

	@Override
	public void initialize() {
		load();
		loadCommands();
	}

	@Override
	public void terminate() {

	}
}
