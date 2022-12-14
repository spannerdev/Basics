package com.spanner.basics.command;

import com.spanner.basics.Basics;
import com.spanner.basics.util.BasicsUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GamemodeCommand extends Command {

	Map<String,GameMode> aliasMap = new HashMap<>();

	Basics basics;
	public GamemodeCommand(Basics basics) {
		super("gamemode","gm", "gmc", "gms", "gma", "gmsp");
		this.basics = basics;

		aliasMap.put("gmc",GameMode.CREATIVE);
		aliasMap.put("gms",GameMode.SURVIVAL);
		aliasMap.put("gma",GameMode.ADVENTURE);
		aliasMap.put("gmsp",GameMode.SPECTATOR);

		setDefaultExecutor((sender, context) -> {
			if (!sender.hasPermission("basics.gamemode")) { sendNoPermission(sender); return; }
			if (!(sender instanceof Player)) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.constraint.target.player");
				return;
			}

			String cmd = context.getCommandName().toLowerCase(Locale.ROOT);
			for (String alias : aliasMap.keySet()) {
				if (cmd.equals(alias)) {
					GameMode gamemode = aliasMap.get(cmd);
					if (!senderHasPermissions(sender,gamemode)) { sendNoPermission(sender); return; }

					Player player = (Player) sender;
					player.setGameMode(gamemode);
					BasicsUtils.sendTranslate(basics,sender,"command.gamemode.self",
						Placeholder.unparsed("gamemode", gamemode.toString()));
					break;
				}
			}
		});

		var targetArg = ArgumentType.Entity("target").onlyPlayers(true);
		var gamemodeStringArg = ArgumentType.Word("gamemode").from("creative","survival","adventure","spectator");

		addSyntax((sender,context) -> {
			GameMode gamemode;
			try {
				gamemode = GameMode.valueOf(context.get(gamemodeStringArg).toUpperCase());
			} catch (IllegalArgumentException e) {
				sendUsage(sender);
				return;
			}
			gamemodeSelfCommand(sender,gamemode);
		},gamemodeStringArg);

		addSyntax((sender,context)->{
			if (!sender.hasPermission("basics.gamemode.others")) { sendNoPermission(sender); return; }

			GameMode gamemode;
			try {
				gamemode = GameMode.valueOf(context.get(gamemodeStringArg).toUpperCase());
			} catch (IllegalArgumentException e) {
				sendUsage(sender);
				return;
			}
			EntityFinder t = context.get(targetArg);
			List<Entity> targets = t.find(sender);
			for (Entity target : targets) {
				if (!(target instanceof Player)) {
					BasicsUtils.sendTranslate(basics,sender,"command.fail.constraint.target.player");
					return;
				}
			}

			if (!senderHasPermissions(sender,gamemode)) { sendNoPermission(sender); return; }

			if (targets.size() == 0) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.notfound.player",
					Placeholder.unparsed("target", context.getRaw("target")));
				return;
			}

			for (Entity entity : targets) {
				Player target = (Player)entity;
				target.setGameMode(gamemode);
				Component targetDisplayName = BasicsUtils.getName(entity);
				BasicsUtils.sendTranslate(basics,sender,"command.gamemode.other",
					Placeholder.component("target", targetDisplayName),
					Placeholder.unparsed("gamemode", gamemode.toString())
				);
			}
		},gamemodeStringArg,targetArg);

	}

	private void gamemodeSelfCommand(CommandSender sender, GameMode gamemode) {
		if (!senderHasPermissions(sender,gamemode)) { sendNoPermission(sender); return; }
		if (!(sender instanceof Player)) {
			BasicsUtils.sendTranslate(basics,sender,"command.fail.constraint.target.player");
			return;
		}

		Player player = (Player) sender;
		player.setGameMode(gamemode);
		BasicsUtils.sendTranslate(basics,sender,"command.gamemode.self",
			Placeholder.unparsed("gamemode", gamemode.toString()));
	}

	private boolean senderHasPermissions(CommandSender sender, GameMode gamemode) {
		boolean permitted = false;
		if (gamemode == GameMode.CREATIVE) {
			permitted = sender.hasPermission("basics.gamemode.creative");
		} else if (gamemode == GameMode.SURVIVAL) {
			permitted = sender.hasPermission("basics.gamemode.survival");
		} else if (gamemode == GameMode.SPECTATOR) {
			permitted = sender.hasPermission("basics.gamemode.spectator");
		} else if (gamemode == GameMode.ADVENTURE) {
			permitted = sender.hasPermission("basics.gamemode.adventure");
		}
		return permitted;
	}

	private void sendNoPermission(CommandSender sender) {
		BasicsUtils.sendTranslate(basics,sender,"command.fail.permission");
	}
	private void sendUsage(CommandSender sender) {
		BasicsUtils.sendTranslate(basics,sender,"command.gamemode.usage");
	}

}