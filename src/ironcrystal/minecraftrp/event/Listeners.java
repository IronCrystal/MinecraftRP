package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.MinecraftRP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Listeners {
	private static PlayerJoin playerJoin;
	private static CreateOccupationSign createSign;
	private static ChangeOccupation changeOccup;
	
	private static void initializeListeners() {
		playerJoin = new PlayerJoin();
		createSign = new CreateOccupationSign();
		changeOccup = new ChangeOccupation();
	}
	
	public static void registerEvents(MinecraftRP main) {
		Listeners.initializeListeners();
		Bukkit.getServer().getPluginManager().registerEvents(playerJoin, main);
		Bukkit.getServer().getPluginManager().registerEvents(createSign, main);
		Bukkit.getServer().getPluginManager().registerEvents(changeOccup, main);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Listeners Registered");
	}

}
