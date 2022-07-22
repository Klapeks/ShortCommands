package com.klapeks.sc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.klapeks.sc.nms.NMS;

public class ShortCommands extends JavaPlugin {
	
	@Override
	public void onEnable() {
		Bukkit.getLogger().info("§aTest Plugin of Prefix Adder was enabled");
		BukkitCommand bc = new BukkitCommand("shortcommands", "Short Commands", "/shortcommands [reload]", List.of("sc", "shortcmd")) {
			@Override
			public boolean execute(CommandSender sender, String label, String[] args) {
				load(true);
				sender.sendMessage("Plugin was reloaded");
				return true;
			}
		};
		NMS.registerCommand("shortcmd", bc);
		load(false);
	}
	
	static List<BukkitCommand> cmds = new ArrayList<>();
	
	static void log(String text, boolean forAdmin) {
		text = ChatColor.translateAlternateColorCodes('&', text.replace("§", "&"));
		Bukkit.getLogger().info(text);
		if (!forAdmin) return;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.isOp()) p.sendMessage(text);
		}
	}
	
	static void load(boolean reload) {
		cmds.forEach(cmd -> NMS.unregisterCommand(cmd));
		cmds.clear();
		
		if (reload) log("§9[ShortCommands] §eReloading...", true);
		else log("§9[ShortCommands] §aLoading plugin", false);
		
		File file = new File("plugins/ShortCommands/config.yml");
		Bukkit.getLogger().info(file.getAbsolutePath());
		Bukkit.getLogger().info(file.getPath());
		FileConfiguration fc;
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			log("§9[ShortCommands] §eGenerating default config...", true);
			fc = YamlConfiguration.loadConfiguration(file);
			fc.set("menu", "dm open menu");
			fc.set("testmsg", "say This is test message");
			try {
				fc.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else fc = YamlConfiguration.loadConfiguration(file);
		
		for (String scmd : fc.getKeys(false)) {
			try {
				command(scmd, fc.getString(scmd));	
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		if (reload) log("§9[ShortCommands] §aPlugin was reloaded", true);
		else log("§9[ShortCommands] §aPlugin was loaded", false);
	}
	
	static BukkitCommand command(String name, String toChange) {
		log("§9[ShortCommands] §eCreating short command §6"+name+"§r", false);
		BukkitCommand cmd = new BukkitCommand(name, "Short command for '"+toChange+"'", "/"+name, List.of()) {
			@Override
			public boolean execute(CommandSender sender, String label, String[] args) {
				Bukkit.dispatchCommand(sender, toChange);
				return true;
			}
		};
		cmds.add(cmd);
		NMS.registerCommand("shortcmd", cmd);
		return cmd;
	}
}
