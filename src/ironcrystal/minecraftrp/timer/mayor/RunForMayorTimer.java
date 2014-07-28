package ironcrystal.minecraftrp.timer.mayor;

import ironcrystal.minecraftrp.commands.MayorCommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class RunForMayorTimer implements Runnable {

	@Override
	public void run() {
		Chunk chunk = MayorCommands.townBeingVotedForMayor.getWorld().getChunkAt(MayorCommands.townBeingVotedForMayor.getCentralChunkLoc().get(0),
				MayorCommands.townBeingVotedForMayor.getCentralChunkLoc().get(1));
		for (Player p: Bukkit.getOnlinePlayers()) {
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + MayorCommands.townBeingVotedForMayor.getName() + " is looking for a new mayor!");
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Coordinates: x: " + chunk.getBlock(8, 0, 8).getLocation().getBlockX()
										  + " x: " + chunk.getBlock(8, 0, 8).getLocation().getBlockZ());
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Type " + ChatColor.RED + "/rp town run" + ChatColor.GREEN + " to run for mayor!");
		}
	}
}
