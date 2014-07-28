package ironcrystal.minecraftrp.event.town;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.commands.MayorCommands;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.timer.mayor.MayorClaimChunkVisualTimer;
import ironcrystal.minecraftrp.timer.mayor.MayorConfirmChunkTimer;
import ironcrystal.minecraftrp.town.Town;
import ironcrystal.minecraftrp.town.TownManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

public class MayorClaimLand implements Listener {

	public static List<UUID> MayorsClaimingChunks = new ArrayList<UUID>();
	public static HashMap<UUID, Integer> VisualGlassTasksForPlayer = new HashMap<UUID, Integer>();
	public static HashMap<UUID, Integer> ConfirmClaimTaskForPlayer = new HashMap<UUID, Integer>();

	private File config = Files.Config;
	private FileConfiguration fileConfig = new YamlConfiguration();


	private MinecraftRP main;

	public MayorClaimLand(MinecraftRP plugin) {
		main = plugin;
		Files.loadFile(config, fileConfig);
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void mayorClaimLand(PlayerInteractEvent event) {
		boolean tooFar = false;
		boolean tooClose = false;
		Player p = event.getPlayer();
		OccupationalPlayer player = new OccupationalPlayer(p.getUniqueId());

		if (MayorsClaimingChunks.contains(p.getUniqueId())) {
			if (player.getOccupation() == Occupations.MAYOR) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Block block = event.getClickedBlock();
					World world = block.getWorld();
					Chunk chunk = block.getChunk();
					boolean distanceIsGood = true;

					/**
					 * Check for distance from other chunks
					 */
					double closest = Double.MAX_VALUE;
					for (Town town : TownManager.townList) {
						List<Integer> centerChunkLoc  = town.getCentralChunkLoc();
						Chunk centerChunk = p.getWorld().getChunkAt(centerChunkLoc.get(0), centerChunkLoc.get(1));

						double x1 = chunk.getX();
						double z1 = chunk.getZ();
						double x2 = centerChunk.getX();
						double z2 = centerChunk.getZ();
						double distancesquared = (x1-x2) * (x1-x2) + (z1-z2) * (z1-z2);
						if (distancesquared < closest) {
							closest = distancesquared;
						}

						if (distancesquared < fileConfig.getDouble("Chunk Distance Between Towns Squared")) {
							distanceIsGood = false;
							tooClose = true;
							break;
						}
					}

					if (closest > fileConfig.getDouble("Maximum Chunk Distance Between Towns Squared")) {
						tooFar = true;
					}

					if (distanceIsGood) {
						HashMap<Location, Material> BlocksChanged = new HashMap<Location, Material>();
						List<Integer> chunkLoc = new ArrayList<Integer>();
						chunkLoc.add(chunk.getX());
						chunkLoc.add(chunk.getZ());
						//Integer[] chunkLoc = {chunk.getX(), chunk.getZ()};
						MayorCommands.MayorsConfirmingClaims.put(player.getUUID(), chunkLoc);

						/**
						 * Get Corner Block relative to chunk
						 */			
						Block chunkMax = chunk.getBlock(15, 0, 15);
						Block chunkMin = chunk.getBlock(0, 0, 0);

						/**
						 * Get Corner Blocks for all 3 chunks relative to previously gained corner blocks
						 */
						Block regionMax = world.getBlockAt(chunkMax.getX() + 16, 255, chunkMax.getZ() + 16);
						Block regionMin = world.getBlockAt(chunkMin.getX() - 16, 0, chunkMin.getZ() - 16);

						/**
						 * Get locations of corner blocks
						 */
						Location loc1 = regionMax.getLocation();
						loc1.setY(p.getLocation().getBlockY());
						
						Location loc2 = new Location(world, regionMin.getX(), 0, regionMax.getZ());
						loc2.setY(p.getLocation().getBlockY());
						
						Location loc3 = regionMin.getLocation();
						loc3.setY(p.getLocation().getBlockY());
						
						Location loc4 = new Location(world, regionMax.getX(), 255, regionMin.getZ());
						loc4.setY(p.getLocation().getBlockY());
						
						Location currentLoc = new Location(loc1.getWorld(), loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ());
						while (currentLoc.getBlockX() != loc2.getBlockX() || currentLoc.getBlockZ() != loc2.getBlockZ()) {
							Block currentBlock = currentLoc.getBlock();
							while (currentBlock.getType() != Material.AIR){
								currentBlock = currentBlock.getRelative(BlockFace.UP);
								
							}
							BlocksChanged.put(currentBlock.getLocation(), currentBlock.getType());
							currentBlock.setType(Material.GLASS);
							currentLoc.setX(currentLoc.getBlockX() - 1);
						}
						

						currentLoc = new Location(loc2.getWorld(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
						while (currentLoc.getBlockX() != loc3.getBlockX() || currentLoc.getBlockZ() != loc3.getBlockZ()) {
							Block currentBlock = currentLoc.getBlock();
							while (currentBlock.getType() != Material.AIR){
								currentBlock = currentBlock.getRelative(BlockFace.UP);
								
							}
							BlocksChanged.put(currentBlock.getLocation(), currentBlock.getType());
							currentBlock.setType(Material.GLASS);
							currentLoc.setZ(currentLoc.getBlockZ() - 1);
						}
						
						currentLoc = new Location(loc3.getWorld(), loc3.getBlockX(), loc3.getBlockY(), loc3.getBlockZ());
						while (currentLoc.getBlockX() != loc4.getBlockX() || currentLoc.getBlockZ() != loc4.getBlockZ()) {
							Block currentBlock = currentLoc.getBlock();
							while (currentBlock.getType() != Material.AIR){
								currentBlock = currentBlock.getRelative(BlockFace.UP);
								
							}
							BlocksChanged.put(currentBlock.getLocation(), currentBlock.getType());
							currentBlock.setType(Material.GLASS);
							currentLoc.setX(currentLoc.getBlockX() + 1);
						}						
						
						currentLoc = new Location(loc4.getWorld(), loc4.getBlockX(), loc4.getBlockY(), loc4.getBlockZ());
						while (currentLoc.getBlockX() != loc1.getBlockX() || currentLoc.getBlockZ() != loc1.getBlockZ()) {
							Block currentBlock = currentLoc.getBlock();
							while (currentBlock.getType() != Material.AIR){
								currentBlock = currentBlock.getRelative(BlockFace.UP);
								
							}
							BlocksChanged.put(currentBlock.getLocation(), currentBlock.getType());
							currentBlock.setType(Material.GLASS);
							currentLoc.setZ(currentLoc.getBlockZ() + 1);
						}
						
						/**
						for (int z = 0; z < 16; z++) {
							for (int x = 0; x < 16; x++) {
								if (z == 0 || z == 15) {
									Block chunkBlock = chunk.getBlock(x, p.getLocation().getBlockY(), z);
									while (chunkBlock.getType() != Material.AIR) {
										chunkBlock = chunkBlock.getWorld().getBlockAt(chunkBlock.getX(), chunkBlock.getY() + 1, chunkBlock.getZ());
									}
									BlocksChanged.put(chunkBlock.getLocation(), chunkBlock.getType());
									chunkBlock.setType(Material.GLASS);
								}else{
									if (x != 0 && x != 15) {
										continue;
									}else{
										Block chunkBlock = chunk.getBlock(x, p.getLocation().getBlockY(), z);
										while (chunkBlock.getType() != Material.AIR) {
											chunkBlock = chunkBlock.getWorld().getBlockAt(chunkBlock.getX(), chunkBlock.getY() + 1, chunkBlock.getZ());
										}
										BlocksChanged.put(chunkBlock.getLocation(), chunkBlock.getType());
										chunkBlock.setType(Material.GLASS);
									}
								}
							}
						}
						**/
						BukkitTask task = Bukkit.getScheduler().runTaskLater(main, new MayorClaimChunkVisualTimer(BlocksChanged, p), 500L);
						BukkitTask confirmTask = Bukkit.getScheduler().runTaskLater(main, new MayorConfirmChunkTimer(p), 600L);
						VisualGlassTasksForPlayer.put(p.getUniqueId(), task.getTaskId());
						ConfirmClaimTaskForPlayer.put(p.getUniqueId(), confirmTask.getTaskId());
						p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Please confirm selection with /rp confirm");
					}else{
						if (tooClose) {
							p.sendMessage(ChatColor.RED + "[MinecraftRP] Too close to another town!  Try going a bit farther");
						}else{
							p.sendMessage(ChatColor.RED + "[MinecraftRP] Too far from the other towns!  Try going a bit closer to civilization");
						}
					}
				}
			}
		}
	}
}