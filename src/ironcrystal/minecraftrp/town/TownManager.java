package ironcrystal.minecraftrp.town;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.player.Mayor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TownManager {
	public static List<Town> townList;
	private static File file;
	
	private static FileConfiguration fileConfig;
	
	public static void initializeTownList() {
		TownManager.townList = new ArrayList<Town>();
		TownManager.file = Files.Towns;
		TownManager.fileConfig = new YamlConfiguration();
		Files.loadFile(file, fileConfig);
		List<String> townNames = fileConfig.getStringList("Towns");
		for (String name : townNames) {
			Town town = new Town(name);
			townList.add(town);
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] " + TownManager.townList.size() + " towns loaded.");
	}
	
	public static Town createNewTown(Mayor mayor, String name, int radius, World world, List<Integer> centerChunkLoc) {
		File townFile = createTownFile(name);
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(townFile, fileConfig);
		fileConfig.set("Mayor", mayor.getUUID().toString());
		fileConfig.set("Radius", radius);
		fileConfig.set("Central Chunk", centerChunkLoc);
		fileConfig.set("Residents", new ArrayList<String>());
		fileConfig.set("World", world.getName());
		Files.saveFile(townFile, fileConfig);
		Town town = new Town(name);
		TownManager.addTown(town);
		return town;
	}
	
	public static Town getTown(String name) {
		for (Town town : TownManager.townList) {
			if (town.getName().equalsIgnoreCase(name)) {
				return town;
			}
		}
		return null;
	}
	
	public static Town getTown(Mayor mayor) {
		for (Town town : TownManager.townList) {
			if (town.getMayor().getUUID().equals(mayor.getUUID())) {
				return town;
			}
		}
		return null;
	}
	
	private static void addTown(Town town) {
		List<String> names = new ArrayList<String>();
		TownManager.townList.add(town);
		for (Town towns : townList) {
			names.add(towns.getName());
		}
		TownManager.fileConfig.set("Towns", names);
		Files.saveFile(TownManager.file, fileConfig);
	}
	
	private static File createTownFile(String name) {
		String path = "plugins/MinecraftRP/towns/" + name + ".yml";
		File file = new File(path);
		if (!file.exists()) {
			Files.saveFile(file, new YamlConfiguration());
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] File for town " + name + " created succesfully.");
		return file;
	}
}
