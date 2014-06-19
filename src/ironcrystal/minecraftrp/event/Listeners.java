package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.MinecraftRP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Listeners {
	private static PlayerJoin playerJoin;
	private static CreateOccupationSign createSign;
	private static ChangeOccupation changeOccup;
	private static MayorClaimLand claimLand;
	
	private static void initializeListeners(MinecraftRP main) {
		playerJoin = new PlayerJoin();
		createSign = new CreateOccupationSign();
		changeOccup = new ChangeOccupation();
		claimLand = new MayorClaimLand(main);
	}
	
	public static void registerEvents(MinecraftRP main) {
		Listeners.initializeListeners(main);
		Bukkit.getServer().getPluginManager().registerEvents(playerJoin, main);
		Bukkit.getServer().getPluginManager().registerEvents(createSign, main);
		Bukkit.getServer().getPluginManager().registerEvents(changeOccup, main);
		Bukkit.getServer().getPluginManager().registerEvents(claimLand, main);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Listeners Registered");
	}

}
