package com.moddamage.properties;

import org.bukkit.OfflinePlayer;

import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.property.Properties;

public class OfflinePlayerProps
{
	public static void register()
	{
		Properties.register("name", OfflinePlayer.class, "getName");
		Properties.register("isOnline", OfflinePlayer.class, "isOnline");
		Properties.register("isBanned", OfflinePlayer.class, "isBanned");
		Properties.register("isWhitelisted", OfflinePlayer.class, "isWhitelisted", "setWhitelisted");
		Properties.register("hasPlayedBefore", OfflinePlayer.class, "hasPlayedBefore");
		Properties.register("firstPlayed", OfflinePlayer.class, "getFirstPlayed");
		Properties.register("lastPlayed", OfflinePlayer.class, "getLastPlayed");

		// Inherited from ServerOperator.
		Properties.register("isOp", OfflinePlayer.class, "isOp", "setOp");
		
        DataProvider.registerTransformer(OfflinePlayer.class, "getPlayer");
	}
}
