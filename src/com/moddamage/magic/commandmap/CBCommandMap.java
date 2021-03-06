package com.moddamage.magic.commandmap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import com.moddamage.magic.MagicStuff;

public class CBCommandMap implements IMagicCommandMap
{
	final Method CraftServer_getCommandMap;
	final Field SimpleCommandMap_knownCommands;
	
	public CBCommandMap()
	{
		Server server = Bukkit.getServer(); // org.bukkit.craftbukkit.CraftServer

		CraftServer_getCommandMap = MagicStuff.safeGetMethod(server.getClass(), "getCommandMap");

		SimpleCommandMap_knownCommands = MagicStuff.safeGetField(
				MagicStuff.safeInvoke(server, CraftServer_getCommandMap).getClass().getSuperclass(),
				"knownCommands");
	}

	@Override
	public SimpleCommandMap getCommandMap()
	{
		Server server = Bukkit.getServer();
		return (SimpleCommandMap) MagicStuff.safeInvoke(server, CraftServer_getCommandMap);
	}

	// As per CraftCommandMap.java.
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Command> getKnownCommandsRawMap() {
		SimpleCommandMap map = getCommandMap();
		return (Map<String, Command>) MagicStuff.safeGet(map, SimpleCommandMap_knownCommands);
	}
}
