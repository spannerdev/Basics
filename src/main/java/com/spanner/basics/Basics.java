package com.spanner.basics;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import org.slf4j.Logger;

public class Basics extends Extension {

	Logger logger;

	public void load() {
		this.logger = MinecraftServer.LOGGER;
		logger.info("Hello from Basics v0.0.1");
	}

	@Override
	public void initialize() {
		load();
	}

	@Override
	public void terminate() {

	}
}
