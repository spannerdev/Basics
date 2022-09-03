package com.spanner.basics.command;

import com.spanner.basics.Basics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.HashMap;
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
			if (sender.hasPermission("basics.gamemode")) {
				if (sender instanceof Player) {
					String cmd = context.getCommandName().toLowerCase(Locale.ROOT);
					for (String alias : aliasMap.keySet()) {
						if (cmd.equals(alias)) {
							GameMode gamemode = aliasMap.get(cmd);
							if (senderHasPermissions(sender,gamemode)) {
								Player player = (Player) sender;
								player.setGameMode(gamemode);
								sender.sendMessage(MiniMessage.miniMessage().deserialize(
									basics.getTranslator().translate("command.gamemode.self",sender)
									,Placeholder.unparsed("gamemode", gamemode.toString())
								));
							} else {
								sendNoPermission(sender);
							}
						}
					}
				} else {
					sendUsage(sender);
				}
			} else {
				sendNoPermission(sender);
			}
		});

		var targetArg = ArgumentType.Entity("target");
		var gamemodeStringArg = ArgumentType.Word("gamemode").from("creative","survival","adventure","spectator");

		addSyntax((sender,context) -> {
			GameMode gamemode = null;
			try {
				gamemode = GameMode.valueOf(context.get(gamemodeStringArg).toUpperCase());
			} catch (IllegalArgumentException e) {
				sendUsage(sender);
				return;
			}
			gamemodeSelfCommand(sender,gamemode);
		},gamemodeStringArg);

		addSyntax((sender,context)->{
			if (sender.hasPermission("basics.gamemode.others")) {
				GameMode gamemode = null;
				try {
					gamemode = GameMode.valueOf(context.get(gamemodeStringArg).toUpperCase());
				} catch (IllegalArgumentException e) {
					sendUsage(sender);
					return;
				}
				EntityFinder t = context.get(targetArg);
				Player target = t.findFirstPlayer(sender);

				if (senderHasPermissions(sender,gamemode)) {
					if (target != null) {
						target.setGameMode(gamemode);
						Component targetDisplayName = target.getDisplayName();
						if (targetDisplayName == null) targetDisplayName = Component.text(target.getUsername());
						sender.sendMessage(MiniMessage.miniMessage().deserialize(
							basics.getTranslator().translate("command.gamemode.other",sender)
							, Placeholder.component("target", targetDisplayName)
							, Placeholder.unparsed("gamemode", gamemode.toString())
						));
					} else {
						sender.sendMessage(MiniMessage.miniMessage().deserialize(
							basics.getTranslator().translate("command.common.notfound",sender)
							, Placeholder.unparsed("target", context.getRaw("target"))));
					}
				} else {
					sendNoPermission(sender);
				}
			} else {
				sendNoPermission(sender);
			}

		},gamemodeStringArg,targetArg);

	}

	private void gamemodeSelfCommand(CommandSender sender, GameMode gamemode) {

		if (senderHasPermissions(sender,gamemode)) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				player.setGameMode(gamemode);
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
						basics.getTranslator().translate("command.gamemode.self",sender)
						, Placeholder.unparsed("gamemode", gamemode.toString())
				));
			} else {
				sendNoPermission(sender);
			}
		} else {
			sendNoPermission(sender);
		}
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
		sender.sendMessage(MiniMessage.miniMessage().deserialize(
			basics.getTranslator().translate("command.permission",sender)
		));
	}
	private void sendUsage(CommandSender sender) {
		sender.sendMessage(MiniMessage.miniMessage().deserialize(
			basics.getTranslator().translate("command.gamemode.usage", sender)
		));
	}

}