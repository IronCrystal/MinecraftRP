package ironcrystal.minecraftrp.town;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.MinecraftRP;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class Town {
	
	public static void addWorldGuard(String name) {
		int radius = Town.getTownRadius(name);
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin instanceof WorldGuardPlugin) {
			WorldGuardPlugin wgp = (WorldGuardPlugin) plugin;
			RegionManager regionManager = wgp.getRegionManager(Town.getWorld(name));
			List<Integer> centerChunkLoc = Town.getCentralChunk(name);
			Chunk centerChunk = Town.getWorld(name).getChunkAt(centerChunkLoc.get(0), centerChunkLoc.get(1));
			/**
			 * Get Corner Block relative to chunk
			 */
			Block topChunkLeft = centerChunk.getBlock(15, 0, 0);
			Block bottomChunkRight = centerChunk.getBlock(0, 256, 15);

			//Bukkit.broadcastMessage("TopChunkLeft: x: " + topChunkLeft.getX() + " y: " + topChunkLeft.getY() + " z: " + topChunkLeft.getZ());
			//Bukkit.broadcastMessage("BottomChunkRight: x: " + bottomChunkRight.getX() + " y: " + bottomChunkRight.getY() + " z: " + bottomChunkRight.getZ());

			/**
			 * Get Corner Blocks for all 3 chunks relative to previously gained corner blocks
			 */
			Block topRegionLeft = Town.getWorld(name).getBlockAt(topChunkLeft.getX() + 16 * radius, 0, topChunkLeft.getZ() - 16 * radius);
			Block bottomRegionRight = Town.getWorld(name).getBlockAt(bottomChunkRight.getX() - 16 * radius, 255, bottomChunkRight.getZ() + 16 * radius);

			//Bukkit.broadcastMessage("TopRegionLeft: x: " + topRegionLeft.getX() + " y: " + topRegionLeft.getY() + " z: " + topRegionLeft.getZ());
			//Bukkit.broadcastMessage("BottomRegionRight: x: " + bottomRegionRight.getX() + " y: " + bottomRegionRight.getY() + " z: " + bottomRegionRight.getZ());
			
			/**
			 * Get locations of corner blocks
			 */
			Location loc1 = topRegionLeft.getLocation();
			Location loc2 = bottomRegionRight.getLocation();

			/**
			 * Get BlockVectors of locations (required for WorldGuard)
			 */
			BlockVector topLeftBlock = new BlockVector(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ());
			BlockVector bottomRightBlock = new BlockVector(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());

			/**
			 * Create Region
			 */
			ProtectedCuboidRegion region = new ProtectedCuboidRegion(name, topLeftBlock, bottomRightBlock);
			DefaultDomain defaultDomain = new DefaultDomain();
			defaultDomain.addPlayer(Bukkit.getPlayer(Town.getOwner(name)).getName());
			//defaultDomain.addPlayer("IronCrystal");
			region.setOwners(defaultDomain);
			regionManager.addRegion(region);
		}
	}
	
	public static void createTownFile(String name) {
		String path = "plugins/MinecraftRP/towns/" + name + ".yml";
		File file = new File(path);
		if (!file.exists()) {
			FileConfiguration config = new YamlConfiguration();
			//config.set("Type", "Village");
			Files.saveFile(file, config);
		}
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(Files.Towns, config);
		List<String> list = config.getStringList("Towns");
		list.add(name);
		config.set("Towns", list);
		Files.saveFile(Files.Towns, config);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] File for town " + name + " created succesfully.");
	}
	
	public static void expandTown(String name) {
		FileConfiguration config = new YamlConfiguration();
		int radius = getTownRadius(name);
		setTownRadius(name, radius + 1);
		Town.removeWorldGuard(name);
		Town.addWorldGuard(name);
		Files.loadFile(Files.Config, config);
		double baseCost = config.getDouble("Cost of Expanding a Village Per Chunk");
		int numberOfChunks = (radius + 1) * 8;
		double cost = baseCost * numberOfChunks;
		MinecraftRP.econ.bankWithdraw(Bukkit.getPlayer(Town.getOwner(name)).getName(), cost);
	}
	
	public static List<Integer> getCentralChunk(String name) {
		File file = Files.getTownFile(name);
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(file, config);
		List<Integer> chunkLoc = config.getIntegerList("Central Chunk");
		return chunkLoc;
	}
	
	public static UUID getOwner(String name) {
		File file = Files.getTownFile(name);
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(file, fileConfig);
		UUID uuid = UUID.fromString(fileConfig.getString("Mayor"));
		return uuid;
	}
	
	public static List<String> getTownList() {
		File file = Files.Towns;
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(file, config);
		List<String> list = config.getStringList("Towns");
		return list;
	}
	
	public static String getTownName(UUID uuid) {
		File pFile = Files.getPlayerFile(uuid);
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(pFile, fileConfig);
		return fileConfig.getString("Town");
	}
	
	public static int getTownRadius(String name) {
		File file = Files.getTownFile(name);
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(file, fileConfig);
		int radius = fileConfig.getInt("Radius");
		return radius;
	}
	
	public static World getWorld(String name) {
		File file = Files.getTownFile(name);
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(file, fileConfig);
		String worldName = fileConfig.getString("World");
		World world = Bukkit.getWorld(worldName);
		return world;
	}
	
	public static void removeWorldGuard(String name) {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin instanceof WorldGuardPlugin) {
			WorldGuardPlugin wgp = (WorldGuardPlugin) plugin;
			RegionManager regionManager = wgp.getRegionManager(Town.getWorld(name));
			regionManager.removeRegion(name);
		}
	}
	
	public static void setTownRadius(String name, int radius) {
		File file = Files.getTownFile(name);
		FileConfiguration fileConfig = new YamlConfiguration();
		Files.loadFile(file, fileConfig);
		fileConfig.set("Radius", radius);
		Files.saveFile(file, fileConfig);
	}
}
