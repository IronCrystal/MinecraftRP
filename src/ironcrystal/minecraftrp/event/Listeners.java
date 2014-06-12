package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.MinecraftRP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Listeners {
	private static PlayerJoin playerJoin;
	
	private static void initializeListeners() {
		playerJoin = new PlayerJoin();
	}
	
	public static void registerEvents(MinecraftRP main) {
		Listeners.initializeListeners();
		Bukkit.getServer().getPluginManager().registerEvents(playerJoin, main);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Listeners Registered");
	}

}
