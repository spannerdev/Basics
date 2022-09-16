package com.spanner.basics.command;

import com.spanner.basics.Basics;
import com.spanner.basics.util.BasicsUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

import java.util.List;

public class TeleportCommand extends Command {

	Basics basics;
	public TeleportCommand(Basics basics) {
		super("teleport","tp");
		this.basics = basics;

		setDefaultExecutor((sender, context) -> {
			if (sender.hasPermission("basics.teleport")) {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
					basics.getTranslator().translate("command.teleport.usage",sender)
				));
			} else {
				sendNoPermission(sender);
			}
		});

		var playerArg = ArgumentType.Entity("player");
		var targetArg = ArgumentType.Entity("target");

		addSyntax((sender,context) -> {
			if (sender.hasPermission("basics.teleport.others")) {
				if (sender instanceof Player) {
					List<Entity> entities = context.get(playerArg).find(sender);
					List<Entity> targets = context.get(targetArg).find(sender);
					if (entities != null && entities.size() > 0) {
						if (targets != null && targets.size() > 0) {
							if (targets.size() == 1) {
								Entity target = targets.get(0);
								Component targetDisplayName = BasicsUtils.getName(target);
								for (Entity e : entities) {
									e.teleport(target.getPosition());
									Component entityDisplayName = BasicsUtils.getName(e);
									sender.sendMessage(MiniMessage.miniMessage().deserialize(
											basics.getTranslator().translate("command.teleport.other", sender)
											, Placeholder.component("player", entityDisplayName)
											, Placeholder.component("target", targetDisplayName)
									));
								}
							} else {
								sender.sendMessage(MiniMessage.miniMessage().deserialize(
									basics.getTranslator().translate("command.fail.constraint.limit",sender)
								));
							}
						} else {
							sendNotFound(sender, context.getRaw("target"));
						}
					} else {
						sendNotFound(sender,context.getRaw("player"));
					}
				} else {
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
						basics.getTranslator().translate("command.fail.constraint.player",sender)
					));
				}
			} else {
				sendNoPermission(sender);
			}
		},playerArg,targetArg);

		addSyntax((sender,context) -> {
			if (sender.hasPermission("basics.teleport.self")) {
				if (sender instanceof Player) {
					Player p = (Player)sender;
					List<Entity> otherEntities = context.get(playerArg).find(sender);
					if (otherEntities != null && otherEntities.size() > 0) {
						if (otherEntities.size() == 1) {
							Entity other = otherEntities.get(0);
							p.teleport(other.getPosition());
							Component otherDisplayName = BasicsUtils.getName(other);
							sender.sendMessage(MiniMessage.miniMessage().deserialize(
								basics.getTranslator().translate("command.teleport.self", sender)
								,Placeholder.component("player", otherDisplayName)
							));
						} else {
							sender.sendMessage(MiniMessage.miniMessage().deserialize(
								basics.getTranslator().translate("command.fail.constraint.limit",sender)
							));
						}
					} else {
						sendNotFound(sender,context.getRaw("player"));
					}
				} else {
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
						basics.getTranslator().translate("command.fail.constraint.player",sender)
					));
				}
			} else {
				sendNoPermission(sender);
			}
		},playerArg);

	}

	private void sendNoPermission(CommandSender sender) {
		sender.sendMessage(MiniMessage.miniMessage().deserialize(
				basics.getTranslator().translate("command.fail.permission",sender)
		));
	}

	private void sendNotFound(CommandSender sender, String target) {
		sender.sendMessage(MiniMessage.miniMessage().deserialize(
				basics.getTranslator().translate("command.fail.notfound.player",sender)
				,Placeholder.unparsed("target",target)
		));
	}

}
