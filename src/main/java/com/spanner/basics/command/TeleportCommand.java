package com.spanner.basics.command;

import com.spanner.basics.Basics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

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
					Player p = context.get(playerArg).findFirstPlayer(sender);
					Player target = context.get(targetArg).findFirstPlayer(sender);
					if (p != null) {
						if (target != null) {
							p.teleport(target.getPosition());
							Component playerDisplayName = p.getDisplayName();
							Component targetDisplayName = target.getDisplayName();
							if (playerDisplayName == null) playerDisplayName = Component.text(p.getUsername());
							if (targetDisplayName == null) targetDisplayName = Component.text(target.getUsername());
							sender.sendMessage(MiniMessage.miniMessage().deserialize(
								basics.getTranslator().translate("command.teleport.other", sender)
								,Placeholder.component("player",playerDisplayName)
								,Placeholder.component("target",targetDisplayName)
							));
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
					Player other = context.get(playerArg).findFirstPlayer(sender);
					if (other != null) {
						p.teleport(other.getPosition());
						Component otherDisplayName = other.getDisplayName();
						if (otherDisplayName == null) otherDisplayName = Component.text(other.getUsername());
						sender.sendMessage(MiniMessage.miniMessage().deserialize(
							basics.getTranslator().translate("command.teleport.self", sender)
							,Placeholder.component("player",otherDisplayName)
						));
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
