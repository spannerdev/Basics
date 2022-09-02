package com.spanner.basics;

import com.spanner.basics.command.GamemodeCommand;
import com.spanner.basics.command.GiveCommand;
import com.spanner.basics.config.Config;
import com.spanner.basics.config.ConfigLoader;
import com.spanner.basics.lang.Translator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.extensions.Extension;
import org.slf4j.Logger;

import java.util.Locale;

public class Basics extends Extension {
	final static String VERSION = "0.4.1";

	Logger logger;
	Translator translator;
	Config config;

	Locale[] supportedLangs = { Locale.UK, Locale.FRANCE };

	public void loadConfig() {
		Config config = ConfigLoader.load(this);
		this.config = config;

		translator.setEnabled(config.get("translation.enabled"));
		translator.setDefaultLocale((String) config.get("translation.default_locale"));
	}

	public void loadCommands() {
		CommandManager commandManager =	MinecraftServer.getCommandManager();

		if (config.get("command.gamemode.enabled")) commandManager.register(new GamemodeCommand(this));
		if (config.get("command.give.enabled"))	commandManager.register(new GiveCommand(this));
	}

	@Override
	public void initialize() {
		this.logger = getLogger();
		this.translator = new Translator(this);

		for (Locale locale : supportedLangs) {
			translator.loadLanguage(locale);
		}

		loadConfig();
		loadCommands();

		logger.info("Hello from Basics v"+VERSION);
	}

	@Override
	public void terminate() {

	}

	public Translator getTranslator() { return this.translator; }
	public Config getConfig() { return this.config; }
}
