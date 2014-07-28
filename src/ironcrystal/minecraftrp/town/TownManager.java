package ironcrystal.minecraftrp.town;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.Mayor;
import ironcrystal.minecraftrp.player.OccupationalPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

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

	public static void deleteTown(Town town) {
		String townName = town.getName();
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin instanceof WorldGuardPlugin) {
			WorldGuardPlugin wgp = (WorldGuardPlugin) plugin;
			RegionManager regionManager = wgp.getRegionManager(town.getWorld());
			regionManager.removeRegion(townName);
		}
		List<String> names = new ArrayList<String>();
		TownManager.townList.remove(town);
		for (Town towns : townList) {
			names.add(towns.getName());
		}
		TownManager.fileConfig.set("Towns", names);
		Files.saveFile(TownManager.file, fileConfig);

		String path = "plugins/MinecraftRP/towns/" + town.getName() + ".yml";
		File file = new File(path);
		if (!file.exists()) {
			file.delete();
		}

		File pFile = Files.getPlayerFile(town.getMayor().getUUID());
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(pFile, fileConfig);
		fileConfig.set("Town", null);
		Files.saveFile(pFile, fileConfig);
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

	/**
	 * Returns the town that a player is living in
	 * @param resident of town
	 */
	public static Town getTown(UUID resident) {
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(Files.getPlayerFile(resident), fileConfig);
		String townName = fileConfig.getString("Resident of");
		if (townName != null) {
			if (!townName.equalsIgnoreCase("")) {
				Town town = getTown(townName);
				return town;
			}
		}
		return null;
	}

	public static void setTownMayor(UUID uuid, String town) {
		Town t = TownManager.getTown(town);
		Mayor oldMayor = t.getMayor();
		Mayor newMayor = new Mayor(uuid);
		t.setMayor(newMayor);
		/**
		 * Old Mayor Files
		 */
		FileConfiguration p2FileConfig = new YamlConfiguration();
		File p2File = Files.getPlayerFile(oldMayor.getUUID());
		Files.loadFile(p2File, p2FileConfig);
		p2FileConfig.set("Town", null);
		Files.saveFile(p2File, p2FileConfig);

		/**
		 * Town File
		 */
		FileConfiguration fileConfig = new YamlConfiguration();
		File tFile = Files.getTownFile(town);
		Files.loadFile(tFile, fileConfig);
		fileConfig.set("Mayor", uuid.toString());
		Files.saveFile(tFile, fileConfig);

		/**
		 * New Mayor File
		 */
		FileConfiguration pFileConfig = new YamlConfiguration();
		File pFile = Files.getPlayerFile(uuid);
		Files.loadFile(pFile, pFileConfig);
		OccupationalPlayer occP = new OccupationalPlayer(uuid);
		occP.setOccupation(Occupations.MAYOR);
		pFileConfig.set("Town", town);
		Files.saveFile(pFile, pFileConfig);
	}

	public static String getTownInfo(Town town) {
		int sideLength = 1 + 2 * town.getRadius();
		String info = "";
		info = info + "Name: " + town.getName();
		info = info + "\n";
		info = info + "Mayor: " + town.getMayor().getName();
		info = info + "\n";
		info = info + "Size: " + sideLength + "x" + sideLength;
		info = info + "\n";
		info = info + "Residents: " + town.getResidents().size();
		info = info + "\n";
		info = info + town.getResidents().toString();
		return info;
	}
}
