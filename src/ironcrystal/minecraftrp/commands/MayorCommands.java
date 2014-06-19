package ironcrystal.minecraftrp.commands;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.event.MayorClaimLand;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.town.Town;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
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

public class MayorCommands {
	
	public static HashMap<UUID, Integer[]> MayorsConfirmingClaims = new HashMap<UUID, Integer[]>();
	public static HashMap<UUID, String> MayorTownNames = new HashMap<UUID, String>();
	
	public static void claimLand(Player p, OccupationalPlayer player, String name) {
		if (player.getOccupation() == Occupations.MAYOR) {
			MayorTownNames.put(player.getUUID(), name);
			MayorClaimLand.MayorsClaimingChunks.add(player.getUUID());
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Right Click the Center Chunk for the Town");
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] This command is for Mayors only.");
		}
	}
	
	public static void confirmClaim(Player p) {
		if (MayorCommands.MayorsConfirmingClaims.containsKey(p.getUniqueId())) {
			String name = MayorTownNames.get(p.getUniqueId());
			
			/**
			 * Save Player File Info
			 */
			File pFile = Files.getPlayerFile(p.getUniqueId());
			FileConfiguration config = new YamlConfiguration();
			Files.loadFile(pFile, config);
			config.set("Town", name);
			Files.saveFile(pFile, config);
			
			/**
			 * Save Town File Info
			 */
			Town.createTownFile(name);
			Files.loadFile(Files.getTownFile(name), config);
			config.set("Central Chunk", MayorsConfirmingClaims.get(p.getUniqueId()));
			config.set("Radius", 1);
			Files.saveFile(Files.getTownFile(name), config);
			
			/**
			 * Create World Guard Region
			 */
			Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
			if (plugin instanceof WorldGuardPlugin) {
				WorldGuardPlugin wgp = (WorldGuardPlugin) plugin;
				RegionManager regionManager = wgp.getRegionManager(p.getWorld());
				List<Integer> centerChunkLoc = Town.getCentralChunk(name);
				Chunk centerChunk = p.getWorld().getChunkAt(centerChunkLoc.get(0), centerChunkLoc.get(1));
				/**
				 * Get Corner Block relative to chunk
				 */
				Block topChunkLeft = centerChunk.getBlock(15, 0, 0);
				Block bottomChunkRight = centerChunk.getBlock(0, 256, 15);
				
				Bukkit.broadcastMessage("TopChunkLeft: x: " + topChunkLeft.getX() + " y: " + topChunkLeft.getY() + " z: " + topChunkLeft.getZ());
				Bukkit.broadcastMessage("BottomChunkRight: x: " + bottomChunkRight.getX() + " y: " + bottomChunkRight.getY() + " z: " + bottomChunkRight.getZ());
				
				/**
				 * Get Corner Blocks for all 3 chunks relative to previously gained corner blocks
				 */
				Block topRegionLeft = p.getWorld().getBlockAt(topChunkLeft.getX() + 16, 0, topChunkLeft.getZ() - 16);
				Block bottomRegionRight = p.getWorld().getBlockAt(bottomChunkRight.getX() - 16, 255, bottomChunkRight.getZ() + 16);
				
				Bukkit.broadcastMessage("TopRegionLeft: x: " + topRegionLeft.getX() + " y: " + topRegionLeft.getY() + " z: " + topRegionLeft.getZ());
				Bukkit.broadcastMessage("BottomRegionRight: x: " + bottomRegionRight.getX() + " y: " + bottomRegionRight.getY() + " z: " + bottomRegionRight.getZ());
				
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
				defaultDomain.addPlayer(p.getName());
				region.setOwners(defaultDomain);
				regionManager.addRegion(region);
			}
			
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Center Chunk Claimed");
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Your town has a 1 chunk radius, for a total of 3x3 chunks.");
			MayorClaimLand.MayorsClaimingChunks.remove(p.getUniqueId());
			MayorsConfirmingClaims.remove(p.getUniqueId());
			MayorTownNames.remove(p.getUniqueId());
			MayorClaimLand.MayorsClaimingChunks.remove(p.getUniqueId());
			int confirmTaskID = MayorClaimLand.ConfirmClaimTaskForPlayer.get(p.getUniqueId());
			Bukkit.getScheduler().cancelTask(confirmTaskID);
			//Bukkit.getScheduler().cancelTask(MayorClaimLand.TasksForPlayer.get(p.getUniqueId()));
			//Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("MinecraftRP"), new MayorClaimChunkVisualTimer(p));
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] There is no claim to confirm!  Type /rp claim <name> to claim a town.");
		}
	}
	
	public static String getTown(OccupationalPlayer player) {
		File file = Files.getPlayerFile(player.getUUID());
		FileConfiguration config = new YamlConfiguration();
		Files.loadFile(file, config);
		return config.getString("Town");		
	}
}
