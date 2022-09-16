package com.spanner.basics;

import com.spanner.basics.command.GamemodeCommand;
import com.spanner.basics.command.GiveCommand;
import com.spanner.basics.command.TeleportCommand;
import com.spanner.basics.config.Config;
import com.spanner.basics.config.ConfigLoader;
import com.spanner.basics.lang.Translator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.permission.Permission;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;

import java.util.Locale;

public class Basics extends Extension {
	final static String VERSION = "0.8.0";

	Logger logger;
	Translator translator;
	Config config;

	Locale[] supportedLangs = { Locale.UK, Locale.FRANCE };

	EventNode permissionHandlerEventNode;

	private void givePlayerGroupPerms(Player p, String group) {
		JSONArray perms = config.get("permission.group."+group+".permissions");
		JSONArray inherit = config.get("permission.group."+group+".inherit");
		if (perms == null || inherit == null) {
			logger.warn("Permissions group "+group+" has invalid configuration");
			return;
		}

		for (int i = 0; i < perms.size(); i++) {
			p.addPermission(new Permission((String) perms.get(i)));
		}
		for (int i = 0; i < inherit.size(); i++) {
			givePlayerGroupPerms(p, (String) inherit.get(i));
		}
	}

	public void loadConfig() {
		Config config = ConfigLoader.load(this);
		this.config = config;

		translator.setEnabled(config.get("translate.enabled"));
		translator.setDefaultLocale((String) config.get("translate.default_locale"));

		GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
		if (config.get("permission.enabled")) {
			this.permissionHandlerEventNode = globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
				Player p = event.getPlayer();
				String uuid = p.getUuid().toString();
				String group = config.get("permission.user."+uuid+".group");
				Integer permissionLevel = (Integer)config.get("permission.user."+uuid+".permission_level");
				if (permissionLevel != null) p.setPermissionLevel(permissionLevel);
				if (group == null) group = "default";
				givePlayerGroupPerms(p,group);
			});
		} else {
			if (permissionHandlerEventNode != null) {
				globalEventHandler.removeChild(permissionHandlerEventNode);
			}
		}
	}

	public void loadCommands() {
		CommandManager commandManager =	MinecraftServer.getCommandManager();

		if (config.get("command.teleport.enabled")) commandManager.register(new TeleportCommand(this));
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
