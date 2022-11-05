package me.sturm.rcbc;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public enum Lang {
	
	ERROR_ARGS("&cПодкоманды с количеством аргументов &6%args% &cне найдено"),
	NO_RESULT("&cНичего не произошло"),
	PLUGIN_RELOAD("&aПлагин перезагружен"),
	NO_PERMS("&cНет прав");
	
	private String text;
	
	Lang(String text) {this.text = text;}
	
	public String text() {return text;}
	public String coloredText() {return color(text);}
	
	public boolean sendMessage(CommandSender sen) {
		if (!text.equals("null")) sen.sendMessage(color(text));
		return true;
	}
	
	public boolean sendMessage(CommandSender sen, String from, String to) {
		if (!text.equals("null")) sen.sendMessage(color(text.replaceAll(from, to)));
		return true;
	}
	
	public boolean sendMessage(CommandSender sen, String from1, String to1, String from2, String to2) {
		if (!text.equals("null")) sen.sendMessage(color(text.replaceAll(from1, to1).replaceAll(from2, to2)));
		return true;
	}
	
	public boolean sendMessage(CommandSender sen, String[] from, String[] to) {
		if (from.length != to.length || text.equals("null")) return true;
		String ret = text;
		for (int i = 0; i < from.length; i++)
			ret = ret.replaceAll(from[i], to[i]);
		sen.sendMessage(color(ret));
		return true;
	}
	
	public static String color(String from) {
		from = from.replaceAll("/n", System.lineSeparator());
		return ChatColor.translateAlternateColorCodes('&', from);
	}
	
	public static YamlConfiguration setMessages(YamlConfiguration c) {
		for (Lang sl : Lang.values()) {
			String path = sl.toString().replaceAll("_", "-").toLowerCase();
			String s = c.getString(path);
			if (s != null) sl.text = s;
			else c.set(path, sl.text);
		}
		return c;
	}
	
	public static void load(File lang) {
		YamlConfiguration l = YamlConfiguration.loadConfiguration(lang);
		l = setMessages(l);
		try {l.save(lang);} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	public static void init(Plugin pl) {
		File f = new File(pl.getDataFolder() + File.separator + "lang.yml");
		f.getParentFile().mkdir();
		if (!f.exists())
			try {f.createNewFile();} 
			catch (IOException e) {e.printStackTrace();}
		load(f);
	}


}
