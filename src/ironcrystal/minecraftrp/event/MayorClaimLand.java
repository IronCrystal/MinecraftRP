package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.commands.MayorCommands;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.timer.mayor.MayorClaimChunkVisualTimer;
import ironcrystal.minecraftrp.timer.mayor.MayorConfirmChunkTimer;
import ironcrystal.minecraftrp.town.Town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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


	private MinecraftRP main;

	public MayorClaimLand(MinecraftRP plugin) {
		main = plugin;
	}

	@EventHandler
	public void mayorClaimLand(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		OccupationalPlayer player = new OccupationalPlayer(p.getUniqueId());

		if (MayorsClaimingChunks.contains(p.getUniqueId())) {
			if (player.getOccupation() == Occupations.MAYOR) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Block block = event.getClickedBlock();
					Chunk chunk = block.getChunk();
					boolean distanceIsGood = true;

					/**
					 * Check for distance from other chunks
					 */
					double distancesquared = Double.MAX_VALUE;
					List<String> townList = Town.getTownList();
					Bukkit.broadcastMessage("Townlist size: " + townList.size());
					Iterator<String> iterator = townList.iterator();

					if (townList != null) {
						Bukkit.broadcastMessage("Townlist is not null!");
						while (iterator.hasNext()) {
							Bukkit.broadcastMessage("Iterator has next!");
							String town = iterator.next();
							List<Integer> centerChunkLoc = Town.getCentralChunk(town);
							Chunk centerChunk = p.getWorld().getChunkAt(centerChunkLoc.get(0), centerChunkLoc.get(1));
							
							double x1 = chunk.getX();
							double z1 = chunk.getZ();
							double x2 = centerChunk.getX();
							double z2 = centerChunk.getZ();
							Bukkit.broadcastMessage("x1: " + x1);
							Bukkit.broadcastMessage("z1: " + z1);
							Bukkit.broadcastMessage("x2: " + x2);
							Bukkit.broadcastMessage("z2: " + z2);
							distancesquared = (x1-x2) * (x1-x2) + (z1-z2) * (z1-z2);
							Bukkit.broadcastMessage("Distance is: " + distancesquared);

							if (distancesquared < 225) {
								Bukkit.broadcastMessage("Distance is too close!  Breaking");
								distanceIsGood = false;
								break;
							}
						}
					}

					/**
					 * TODO
					 * if distance is good
					 */
					if (distanceIsGood) {
						HashMap<Location, Material> BlocksChanged = new HashMap<Location, Material>();
						Integer[] chunkLoc = {chunk.getX(), chunk.getZ()};
						MayorCommands.MayorsConfirmingClaims.put(player.getUUID(), chunkLoc);
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
						BukkitTask task = Bukkit.getScheduler().runTaskLater(main, new MayorClaimChunkVisualTimer(BlocksChanged, p), 500L);
						BukkitTask confirmTask = Bukkit.getScheduler().runTaskLater(main, new MayorConfirmChunkTimer(p), 600L);
						VisualGlassTasksForPlayer.put(p.getUniqueId(), task.getTaskId());
						ConfirmClaimTaskForPlayer.put(p.getUniqueId(), confirmTask.getTaskId());
						p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Please confirm selection with /rp confirm");
					}else{
						p.sendMessage(ChatColor.RED + "[MinecraftRP] Too close to another town!  Try going a bit farther");
					}
				}
			}
		}
	}
}