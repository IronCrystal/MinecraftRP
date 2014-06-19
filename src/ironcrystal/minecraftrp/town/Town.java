package ironcrystal.minecraftrp.town;

import ironcrystal.minecraftrp.Files;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Town {
	
	public static void createTownFile(String name) {
		String path = "plugins/MinecraftRP/towns/" + name + ".yml";
		File file = new File(path);
		if (!file.exists()) {
			FileConfiguration config = new YamlConfiguration();
			config.set("Type", "Village");
			Files.saveFile(file, config);
		}
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(Files.Towns, config);
		List<String> list = config.getStringList("Towns");
		list.add(name);
		config.set("Towns", list);
		Files.saveFile(Files.Towns, config);
		Bukkit.getLogger().info(ChatColor.GREEN + "[MinecraftRP] File for town " + name + " created succesfully.");
	}
	
	public static Integer[] getCentralChunk(String name) {
		File file = Files.getTownFile(name);
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(file, config);
		Integer[] chunkLoc = (Integer[]) config.get("Central Chunk");
		return chunkLoc;
	}
}
