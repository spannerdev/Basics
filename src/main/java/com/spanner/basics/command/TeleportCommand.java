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
				BasicsUtils.sendTranslate(basics,sender,"command.teleport.usage");
			} else {
				sendNoPermission(sender);
			}
		});

		var playerArg = ArgumentType.Entity("entity");
		var targetArg = ArgumentType.Entity("target");

		addSyntax((sender,context) -> {
			if (!sender.hasPermission("basics.teleport.others")) {
				sendNoPermission(sender);
				return;
			}

			List<Entity> entities = context.get(playerArg).find(sender);
			List<Entity> targets = context.get(targetArg).find(sender);
			if (entities.size() == 0) {
				sendNotFound(sender,context.getRaw("entity"));
				return;
			}
			if (targets.size() == 0) {
				sendNotFound(sender,context.getRaw("target"));
				return;
			}
			if (targets.size() != 1) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.constraint.limit");
				return;
			}

			Entity target = targets.get(0);
			Component targetDisplayName = BasicsUtils.getName(target);
			for (Entity e : entities) {
				e.teleport(target.getPosition());
				Component entityDisplayName = BasicsUtils.getName(e);
				BasicsUtils.sendTranslate(basics,sender,"command.teleport.other",
					Placeholder.component("entity", entityDisplayName),
					Placeholder.component("target", targetDisplayName)
				);
			}
		},playerArg,targetArg);

		addSyntax((sender,context) -> {
			if (!sender.hasPermission("basics.teleport.self")) {
				sendNoPermission(sender);
				return;
			}
			if (!(sender instanceof Player p)) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.constraint.player");
				return;
			}

			List<Entity> otherEntities = context.get(playerArg).find(sender);
			if (otherEntities.size() == 0) {
				sendNotFound(sender,context.getRaw("entity"));
				return;
			}
			if (otherEntities.size() != 1) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.constraint.limit");
			}
			Entity other = otherEntities.get(0);
			p.teleport(other.getPosition());
			Component otherDisplayName = BasicsUtils.getName(other);
			BasicsUtils.sendTranslate(basics,sender,"command.teleport.self",
					Placeholder.component("entity", otherDisplayName));
		},playerArg);

	}

	private void sendNoPermission(CommandSender sender) {
		BasicsUtils.sendTranslate(basics,sender,"command.fail.permission");
	}

	private void sendNotFound(CommandSender sender, String target) {
		BasicsUtils.sendTranslate(basics,sender,"command.fail.notfound.entity",
				Placeholder.unparsed("target",target));
	}

}
