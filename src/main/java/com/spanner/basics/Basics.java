package com.spanner.basics;

import com.spanner.basics.command.GamemodeCommand;
import com.spanner.basics.lang.Translator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.extensions.Extension;
import org.slf4j.Logger;

import java.util.Locale;

public class Basics extends Extension {

	Logger logger;
	Translator translator;

	Locale[] supportedLangs = { Locale.UK, Locale.FRANCE };

	public void load() {
		this.logger = getLogger();
		this.translator = new Translator(this);

		for (Locale locale : supportedLangs) {
			translator.loadLanguage(locale);
		}

		logger.info("Hello from Basics v0.2.0");
	}

	public void loadCommands() {
		CommandManager commandManager =	MinecraftServer.getCommandManager();

		commandManager.register(new GamemodeCommand(this));
	}

	@Override
	public void initialize() {
		load();
		loadCommands();
	}

	@Override
	public void terminate() {

	}

	public Translator getTranslator() { return this.translator; }
}
