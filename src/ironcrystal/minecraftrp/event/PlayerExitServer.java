package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.commands.MayorCommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerExitServer implements Listener {
	
	@EventHandler
	public void playerExit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (MayorCommands.PlayersRunningForMayor.contains(p.getUniqueId())) {
			MayorCommands.PlayersRunningForMayor.remove(p.getUniqueId());
			for (Player player: Bukkit.getOnlinePlayers()) {
				player.sendMessage(ChatColor.RED + "[MinecraftRP] Removed " + p.getName() + " from the election for disconnecting!");
			}
		}
		if (MayorCommands.VotesForPlayers.containsKey(p.getUniqueId())) {
			MayorCommands.VotesForPlayers.remove(p.getUniqueId());
		}
	}

}
