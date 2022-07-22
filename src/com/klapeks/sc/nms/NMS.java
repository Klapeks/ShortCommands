package com.klapeks.sc.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;

public class NMS {
	
	static CommandMap commandMap;
	static Field knownCommands;
	public static void registerCommand(String prefix, BukkitCommand cmd) {
		if (cmd==null) return;
		if (commandMap == null) {
			try {
				Method getCommandMap = Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap");
				commandMap = (CommandMap) getCommandMap.invoke(Bukkit.getServer());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		try {
			commandMap.register(prefix, cmd);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public static void unregisterCommand(BukkitCommand cmd) {
		if (cmd==null) return;
		if (knownCommands==null) {
			try {
				knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
				knownCommands.setAccessible(true);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		try {
			Map<String, Command> map = (Map<String, Command>) knownCommands.get(commandMap);
			map.remove(cmd.getName());
	        for (String alias : cmd.getAliases()){
	           if(map.containsKey(alias) && map.get(alias).getName().equals(cmd.getName())) {
	        	   map.remove(alias);
	            }
	        }
		} catch (Throwable e) {
			e.printStackTrace();
		}
		cmd.unregister((CommandMap) commandMap);
	}
}
