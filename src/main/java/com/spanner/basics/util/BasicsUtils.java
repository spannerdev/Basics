package com.spanner.basics.util;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

public class BasicsUtils {
	public static Component getName(Entity entity) {
		Component name;
		if (entity instanceof Player) {
			Player p = (Player)entity;
			name = p.getDisplayName();
			if (name == null) name = Component.text(p.getUsername());
		} else {
			name = entity.getCustomName();
			if (name == null) name = Component.text(entity.getEntityType().name());
			// TOOD: Use TranslatableComponent to translate the entity name.
			// Perhaps rewrite the translator system to use Adventure's translators?
		}
		return name;
	}
}
