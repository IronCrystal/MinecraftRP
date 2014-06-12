package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.Files;

import java.io.File;

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
		char firstLetter = player.getName().charAt(0);
		File file = null;
		try {
			file = new File("plugins/MinecraftRP/player/" + firstLetter + "/" + player.getName() + ".yml");
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		if (!file.exists()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] New Player Joined! Creating player file...");
			FileConfiguration config = new YamlConfiguration();
			config.set("Occupation", "Generic Occupation");
			Files.saveFile(file, config);
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] " + player.getName() + " Player File Created!");
		}
	}
}