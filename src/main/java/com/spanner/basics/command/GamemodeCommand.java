package com.spanner.basics.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.Locale;

public class GamemodeCommand extends Command {

	public GamemodeCommand() {
		super("gamemode","gm");

		setDefaultExecutor((sender, context) -> {
			sender.addPermission(new Permission("basics.gamemode"));
			sender.addPermission(new Permission("basics.gamemode.creative"));
			if (sender.hasPermission("basics.gamemode")) {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
				"<red>Usage: <gray>/gamemode <gamemode> [target]</gray></red>"
				));
			} else {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
				"<red>You do not have the permissions to use this command</red>"
				));
			}
		});

		var gamemodeArg = ArgumentType.Enum("gamemode", GameMode.class);
		var targetArg = ArgumentType.String("target");

		addSyntax((sender, context) -> {
			final GameMode gamemode = context.get(gamemodeArg);

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
			if (permitted) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					player.setGameMode(gamemode);
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
							"<gold>Set your gamemode to <red><gamemode></red></gold>"
							, Placeholder.unparsed("gamemode", gamemode.toString())
					));
				} else {
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
							"<red>You must be a player to use this command</red>"
					));
				}
			} else {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
				"<red>You do not have the permissions to use this command</red>"
				));
			}
		},gamemodeArg);

		addSyntax((sender,context)->{
			if (sender.hasPermission("basics.gamemode.others")) {
				final GameMode gamemode = context.get(gamemodeArg);
				final Player target = MinecraftServer.getConnectionManager().getPlayer(context.get(targetArg));

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
				if (permitted) {
					if (target != null) {
						target.setGameMode(gamemode);
						Component targetDisplayName = target.getDisplayName();
						if (targetDisplayName == null) targetDisplayName = Component.text(target.getUsername());
						sender.sendMessage(MiniMessage.miniMessage().deserialize(
								"<gold>Set the gamemode of <red><target></red> to <red><gamemode></red></gold>"
								, Placeholder.component("target", targetDisplayName)
								, Placeholder.unparsed("gamemode", gamemode.toString())
						));
					} else {
						sender.sendMessage(MiniMessage.miniMessage().deserialize(
								"<red>Could not find player <target></red>"
								, Placeholder.unparsed("target", context.getRaw("target"))));
					}
				} else {
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
					"<red>You do not have the permissions to use this command</red>"
					));
				}
			} else {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
				"<red>You do not have the permissions to use this command</red>"
				));
			}

		},gamemodeArg,targetArg);

	}

}