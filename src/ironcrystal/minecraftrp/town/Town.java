package ironcrystal.minecraftrp.town;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.player.Mayor;

import java.io.File;
import java.util.ArrayList;
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class Town {
	
	private Mayor mayor;
	private File file;
	private String name;
	private int radius;
	private World world;
	private List<Integer> chunkLoc;
	private List<String> residents;
	
	private FileConfiguration fileConfig;
	
	protected Town(String name) {
		this.name = name;
		this.file = Files.getTownFile(name);
		if (!this.file.exists()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] WARNING: A town object was created but the file has not been created!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] WARNING: Something is wrong!");
		}
		this.fileConfig = new YamlConfiguration();
		Files.loadFile(file, fileConfig);
		UUID uuid = UUID.fromString(fileConfig.getString("Mayor"));
		this.mayor = new Mayor(uuid);
		this.radius = fileConfig.getInt("Radius");
		this.world = Bukkit.getWorld(fileConfig.getString("World"));
		this.chunkLoc = fileConfig.getIntegerList("Central Chunk");
		this.residents = fileConfig.getStringList("Residents");
	}
	
	public void addResident(UUID uuid) {
		residents.add(uuid.toString());
		fileConfig.set("Residents", residents);
		Files.saveFile(file, fileConfig);
	}
	
	public List<Integer> getCentralChunkLoc() {
		return chunkLoc;
	}
	
	/**
	 * Returns the location of corners like in a grid system.
	 * Z is like Y on 2D grid, and loc1-4 are their respective quadrants.
	 * @return List of locations of corners. index 0 and 2 are opposite corners
	 */
	public List<Location> getTownCorners() {
		List<Location> list = new ArrayList<Location>();
		Chunk centerChunk = world.getChunkAt(chunkLoc.get(0), chunkLoc.get(1));
		/**
		 * Get Corner Block relative to chunk
		 */			
		Block chunkMax = centerChunk.getBlock(15, 0, 15);
		Block chunkMin = centerChunk.getBlock(0, 0, 0);

		/**
		 * Get Corner Blocks for all 3 chunks relative to previously gained corner blocks
		 */
		Block regionMax = world.getBlockAt(chunkMax.getX() + 16 * radius, 255, chunkMax.getZ() + 16 * radius);
		Block regionMin = world.getBlockAt(chunkMin.getX() - 16 * radius, 0, chunkMin.getZ() - 16 * radius);
		
		/**
		 * Get locations of corner blocks
		 */
		Location loc1 = regionMax.getLocation();
		Location loc2 = new Location(world, regionMin.getX(), 0, regionMax.getZ());
		Location loc3 = regionMin.getLocation();
		Location loc4 = new Location(world, regionMax.getX(), 255, regionMin.getZ());
		list.add(loc1);
		list.add(loc2);
		list.add(loc3);
		list.add(loc4);
		return list;
	}
	
	public Town addWorldGuardRegion() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin instanceof WorldGuardPlugin) {
			WorldGuardPlugin wgp = (WorldGuardPlugin) plugin;
			RegionManager regionManager = wgp.getRegionManager(world);
			
			Location loc1 = getTownCorners().get(0);
			Location loc2 = getTownCorners().get(2);

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
			defaultDomain.addPlayer(mayor.getName());
			region.setOwners(defaultDomain);
			/**
			 * Really hacky way of doing this
			 */
			Player player = Bukkit.getPlayer(mayor.getUUID());
			Bukkit.getServer().dispatchCommand(player, "region flag " + name + " farewell &GYou are now leaving " + name);
			Bukkit.getServer().dispatchCommand(player, "region flag " + name + " greeting &GYou are now entering " + name);
			regionManager.addRegion(region);
			Bukkit.getServer().dispatchCommand(player, "region flag " + name + " farewell &GYou are now leaving " + name);
			Bukkit.getServer().dispatchCommand(player, "region flag " + name + " greeting &GYou are now entering " + name);
		}
		return this;
	}
	
	public void expandTown() {
		setRadius(radius + 1);
		removeWorldGuard();
		addWorldGuardRegion();
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(Files.Config, config);
		double baseCost = config.getDouble("Cost of Expanding a Village Per Chunk");
		int numberOfChunks = getRadius() * 8;
		double cost = baseCost * numberOfChunks;
		MinecraftRP.econ.bankWithdraw(mayor.getName(), cost);
	}
	
	public File getFile() {
		return file;
	}
	
	public Mayor getMayor() {
		return mayor;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public List<String> getResidents() {
		return residents;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void removeResident(String name) {
		residents.remove(name);
		fileConfig.set("Residents", residents);
		Files.saveFile(file, fileConfig);
	}
	
	public void removeWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin instanceof WorldGuardPlugin) {
			WorldGuardPlugin wgp = (WorldGuardPlugin) plugin;
			RegionManager regionManager = wgp.getRegionManager(world);
			regionManager.removeRegion(name);
		}
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public void setMayor(Mayor mayor) {
		this.mayor = mayor;
	}

	public void setRadius(int radius) {
		this.radius = radius;
		fileConfig.set("Radius", getRadius());
		Files.saveFile(file, fileConfig);
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
