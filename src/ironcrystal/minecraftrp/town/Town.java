package ironcrystal.minecraftrp.town;

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

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.player.Mayor;

public class Town {
	
	private Mayor mayor;
	private File file;
	private String name;
	private int radius;
	private World world;
	private List<Integer> chunkLoc;
	private List<String> residents;
	
	private FileConfiguration fileConfig;
	
	/**public Town(Mayor mayor) {
		this.mayor = mayor;
		File pFile = Files.getPlayerFile(mayor.getUUID());
		FileConfiguration pConfig = new YamlConfiguration();
		Files.loadFile(pFile, pConfig);
		this.name = pConfig.getString("Town");
		this.file = Files.getTownFile(name);
		this.fileConfig = new YamlConfiguration();
		if (!this.file.exists()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] WARNING: A town object was created but the file has not been created!");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MinecraftRP] WARNING: Something is wrong!");
		}
		Files.loadFile(file, fileConfig);
		this.radius = fileConfig.getInt("Radius");
		this.world = Bukkit.getWorld(fileConfig.getString("World"));
		this.chunkLoc = fileConfig.getIntegerList("Central Chunk");
		this.residents = fileConfig.getStringList("Residents");
	}
	**/
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
	
	public void addResident(String name) {
		residents.add(name);
		fileConfig.set("Residents", residents);
		Files.saveFile(file, fileConfig);
	}
	
	public List<Integer> getCentralChunkLoc() {
		return chunkLoc;
	}
	
	public void addWorldGuardRegion() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin instanceof WorldGuardPlugin) {
			WorldGuardPlugin wgp = (WorldGuardPlugin) plugin;
			RegionManager regionManager = wgp.getRegionManager(world);
			Chunk centerChunk = world.getChunkAt(chunkLoc.get(0), chunkLoc.get(1));
			/**
			 * Get Corner Block relative to chunk
			 */
			Block topChunkLeft = centerChunk.getBlock(15, 0, 0);
			Block bottomChunkRight = centerChunk.getBlock(0, 256, 15);

			/**
			 * Get Corner Blocks for all 3 chunks relative to previously gained corner blocks
			 */
			Block topRegionLeft = world.getBlockAt(topChunkLeft.getX() + 16 * radius, 0, topChunkLeft.getZ() - 16 * radius);
			Block bottomRegionRight = world.getBlockAt(bottomChunkRight.getX() - 16 * radius, 255, bottomChunkRight.getZ() + 16 * radius);
			
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
			defaultDomain.addPlayer(mayor.getName());
			region.setOwners(defaultDomain);
			regionManager.addRegion(region);
		}
	
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
