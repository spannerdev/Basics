package com.spanner.basics.lang;

import com.spanner.basics.Basics;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

import java.io.*;
import java.util.*;

public class Translator {

	Map<Locale,Properties> languages = new HashMap<>();
	Locale defaultLocale = Locale.UK;

	Basics main;
	public Translator(Basics main) {
		this.main = main;
	}

	public String translate(String string, CommandSender sender) {
		if (sender instanceof Player) {
			Player p = (Player)sender;
			return translate(string,p);
		} else {
			return translate(string,defaultLocale);
		}
	}
	public String translate(String string, Player player) {
		Locale locale = player.getLocale();
		if (locale == null) locale = defaultLocale;
		return translate(string,locale);
	}
	public String translate(String string, Locale locale) {
		Properties language = languages.get(locale);
		Object s = language.get(string);
		String l = String.valueOf(s);
		if (s == null) {
			l = (String)languages.get(defaultLocale).get(string);
		}
		return l.substring(1,l.length()-1);
	}

	public boolean loadLanguage(Locale locale) {
		try (InputStream stream = main.getPackagedResource("lang/"+locale.toString()+".lang")) {
			Properties p = new Properties();
			Reader reader = new InputStreamReader(stream,"UTF-8");
			p.load(reader);
			languages.put(locale,p);
			stream.close();
			return true;
		} catch (FileNotFoundException e) {
			main.getLogger().warn("Lang file not found: "+locale.toString()+".lang");
		} catch (IOException e) {
			main.getLogger().error("Error loading lang file");
			e.printStackTrace();
		}
		return false;
	}

}
