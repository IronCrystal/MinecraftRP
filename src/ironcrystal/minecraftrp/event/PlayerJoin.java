package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.Files;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

	@EventHandler
	public void playerLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		File file = null;
		try {
			file = new File("plugins/MinecraftRP/player/" + player.getUniqueId().toString() + ".yml");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		FileConfiguration config = new YamlConfiguration();
		if (!file.exists()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] New Player Joined! Creating player file...");
			config.set("Occupation", "citizen");
			Files.saveFile(file, config);
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] " + player.getName() + " Player File Created!");
		}
		Files.loadFile(file, config);
		config.set("Last Known Name", player.getName());
		if (config.get("Contracts") == null) {
			config.set("Contracts", new ArrayList<Integer>());
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Setting contracts to a blank array");
		}
		Files.saveFile(file, config);
	}
}