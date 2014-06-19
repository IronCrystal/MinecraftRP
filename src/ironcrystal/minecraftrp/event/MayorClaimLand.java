package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.commands.MayorCommands;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.timer.MayorClaimChunkVisualTimer;

import java.util.ArrayList;
import java.util.HashMap;
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
	public static HashMap<UUID, Integer> TasksForPlayer = new HashMap<UUID, Integer>();

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

					/**
					 * Check for distance from other chunks
					 */
					//int distance;

					/**
					 * TODO
					 * if distance is good
					 */
					if (true) {
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
						BukkitTask task = Bukkit.getScheduler().runTaskLater(main, new MayorClaimChunkVisualTimer(BlocksChanged), 600L);
						TasksForPlayer.put(p.getUniqueId(), task.getTaskId());
						p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Please confirm selection with /rp confirm");
					}
				}
			}
		}
	}
}