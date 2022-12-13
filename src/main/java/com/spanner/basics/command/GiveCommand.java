package com.spanner.basics.command;

import com.spanner.basics.Basics;
import com.spanner.basics.util.BasicsUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

public class GiveCommand extends Command {

	Basics basics;
	public GiveCommand(Basics basics) {
		super("give","i");
		this.basics = basics;

		setDefaultExecutor((sender,context) -> {
			if (sender.hasPermission("basics.give")) {
				BasicsUtils.sendTranslate(basics,sender,"command.give.usage");
			} else {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.permission");
			}
		});

		var itemStackArg = ArgumentType.ItemStack("item");
		var quantityArg = ArgumentType.Integer("quantity");
		var targetArg = ArgumentType.Entity("target").onlyPlayers(true);

		addSyntax((sender, context) -> {
			if (!(sender instanceof Player p)) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.constraint.player");
				return;
			}
			if (!sender.hasPermission("basics.give.self")) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.permission");
				return;
			}

			ItemStack b = context.get(itemStackArg);

			give(p, b);
			giveSelfMessage(p, b);
		},itemStackArg);

		addSyntax((sender, context) -> {
			if (!(sender instanceof Player p)) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.constraint.player");
				return;
			}
			if (!sender.hasPermission("basics.give.self")) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.permission");
				return;
			}

			int quantity = context.get(quantityArg);
			ItemStack b = context.get(itemStackArg).withAmount(quantity);

			give(p, b);
			giveSelfMessage(p, b);
		},itemStackArg,quantityArg);

		addSyntax((sender,context)->{
			if (!sender.hasPermission("basics.give.others")) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.permission");
				return;
			}

			ItemStack b = context.get(itemStackArg);
			int quantity = context.get(quantityArg);
			EntityFinder e = context.get(targetArg);
			List<Entity> targets = e.find(sender);

			if (targets.size() == 0) {
				BasicsUtils.sendTranslate(basics,sender,"command.fail.notfound.player",
					Placeholder.unparsed("target", context.getRaw("target")));
			}

			for (Entity entity : targets) {
				Player target = (Player) entity;
				give(target, b.withAmount(quantity));
				Component targetDisplayName = BasicsUtils.getName(target);
				
				BasicsUtils.sendTranslate(basics,sender,"command.give.other",
						Placeholder.unparsed("quantity", ""+quantity),
						Placeholder.unparsed("item", b.material().toString()),
						Placeholder.component("target", targetDisplayName)
				);
			}
		},itemStackArg,quantityArg,targetArg);

	}

	private void giveSelfMessage(Player sender, ItemStack b) {
		BasicsUtils.sendTranslate(basics,sender,"command.give.self",
			Placeholder.unparsed("quantity", "" + b.amount()),
			Placeholder.unparsed("item", b.material().toString())
		);
	}

	private void give(Player p, ItemStack stack) {
		p.getInventory().addItemStack(stack);
	}

}
