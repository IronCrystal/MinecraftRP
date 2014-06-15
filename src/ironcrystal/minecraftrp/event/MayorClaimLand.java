package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.commands.MayorCommands;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MayorClaimLand implements Listener {
	
	public static List<UUID> MayorsClaimingChunks = new ArrayList<UUID>();
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void mayorClaimLand(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		OccupationalPlayer player = new OccupationalPlayer(p.getUniqueId());
		
		if (player.getOccupation() == Occupations.MAYOR) {
			if (MayorsClaimingChunks.contains(p.getUniqueId())) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Block block = event.getClickedBlock();
					Chunk chunk = block.getChunk();
					
					/**
					 * Check for distance from other chunks
					 */
					//int distance;
					
					/**
					 * TODO
					 */
					if (true) {
						
					}
					Integer[] chunkLoc = {chunk.getX(), chunk.getZ()};
					File townFile = Files.getTownFile(MayorCommands.getTown(player));
					FileConfiguration config = new YamlConfiguration();
					Files.loadFile(townFile, config);
					List<Integer[]> chunkList = (List<Integer[]>) config.getList("Chunks");
					List<Integer[]> chunkListNew = new ArrayList<Integer[]>();
					int length;
					if (chunkList != null) {
						chunkList.add(chunkLoc);
						length = chunkList.size();
						config.set("Chunks", chunkList);
					}else{
						chunkListNew.add(chunkLoc);
						length = chunkListNew.size();
						config.set("Chunks", chunkListNew);
					}
					
					Files.saveFile(townFile, config);
					p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Chunk Claimed");
					
					if (length == 4) {
						MayorsClaimingChunks.remove(p.getUniqueId());
						p.sendMessage(ChatColor.GREEN + "[MinecraftRP] All chunks claimed!");
					}					
				}
			}
		}
	}
}