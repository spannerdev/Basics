package com.spanner.basics.lang;

import com.spanner.basics.Basics;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import org.apache.commons.lang3.LocaleUtils;

import java.io.*;
import java.util.*;

public class Translator {

	Map<Locale,Properties> languages = new HashMap<>();
	static final Locale internalDefaultLocale = Locale.UK;

	Locale defaultLocale = internalDefaultLocale;

	boolean enabled;

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
		if (!enabled) {
			String l = (String)languages.get(defaultLocale).get(string);
			return l.substring(1,l.length()-1);
		}
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
			reader.close();
			stream.close();
			return true;
		} catch (FileNotFoundException e) {
			main.getLogger().warn("Lang file not found: "+locale.toString()+".lang");
		} catch (IOException e) {
			main.getLogger().error("IOException while loading language");
			e.printStackTrace();
		}
		return false;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public void setDefaultLocale(String localeString) {
		Locale l = LocaleUtils.toLocale(localeString);
		if (l==null) {
			l = internalDefaultLocale;
		}
		setDefaultLocale(l);
	}
	public void setDefaultLocale(Locale locale) {
		this.defaultLocale = locale;
	}

}
