package com.spanner.basics.command;

import com.spanner.basics.Basics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.entity.EntityFinder;

public class GiveCommand extends Command {

	Basics basics;
	public GiveCommand(Basics basics) {
		super("give","i");
		this.basics = basics;

		setDefaultExecutor((sender,context) -> {
			if (sender.hasPermission("basics.give")) {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
						basics.getTranslator().translate("command.give.usage",sender)
				));
			} else {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
						basics.getTranslator().translate("command.fail.permission",sender)
				));
			}
		});

		var itemStackArg = ArgumentType.ItemStack("item");
		var quantityArg = ArgumentType.Integer("quantity");
		var targetArg = ArgumentType.Entity("target");

		addSyntax((sender, context) -> {
			ItemStack b = context.get(itemStackArg);
			if (sender instanceof Player) {
				if (sender.hasPermission("basics.give.self")) {
					give((Player) sender, b);
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
						basics.getTranslator().translate("command.give.self", sender)
						, Placeholder.unparsed("quantity", "1")
						, Placeholder.unparsed("item", b.material().toString())
					));
				} else {
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
						basics.getTranslator().translate("command.fail.permission",sender)
					));
				}
			} else {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
					basics.getTranslator().translate("command.fail.constraint.player",sender)
				));
			}
		},itemStackArg);

		addSyntax((sender, context) -> {
			ItemStack b = context.get(itemStackArg);
			int quantity = context.get(quantityArg);
			if (sender instanceof Player) {
				if (sender.hasPermission("basics.give.self")) {
					give((Player) sender, b.withAmount(quantity));
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
							basics.getTranslator().translate("command.give.self", sender)
							, Placeholder.unparsed("quantity", "" + quantity)
							, Placeholder.unparsed("item", b.material().toString())
					));
				} else {
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
						basics.getTranslator().translate("command.fail.permission",sender)
					));
				}
			} else {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
					basics.getTranslator().translate("command.fail.constraint.player",sender)
				));
			}
		},itemStackArg,quantityArg);

		addSyntax((sender,context)->{
			ItemStack b = context.get(itemStackArg);
			int quantity = context.get(quantityArg);
			EntityFinder e = context.get(targetArg);
			Player target = e.findFirstPlayer(sender);
			if (target != null) {
				if (sender.hasPermission("basics.give.others")) {
					give(target, b.withAmount(quantity));
					Component targetDisplayName = target.getDisplayName();
					if (targetDisplayName == null) targetDisplayName = Component.text(target.getUsername());
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
							basics.getTranslator().translate("command.give.other", sender)
							, Placeholder.unparsed("quantity", "" + quantity)
							, Placeholder.unparsed("item", b.material().toString())
							, Placeholder.component("target", targetDisplayName)
					));
				} else {
					sender.sendMessage(MiniMessage.miniMessage().deserialize(
						basics.getTranslator().translate("command.fail.permission",sender)
					));
				}
			} else {
				sender.sendMessage(MiniMessage.miniMessage().deserialize(
					basics.getTranslator().translate("command.fail.notfound.player",sender)
					, Placeholder.unparsed("target", context.getRaw("target"))));
			}
		},itemStackArg,quantityArg,targetArg);

	}

	private void give(Player p, ItemStack stack) {
		p.getInventory().addItemStack(stack);
	}

}
