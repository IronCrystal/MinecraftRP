package ironcrystal.minecraftrp.timer.mayor;

import ironcrystal.minecraftrp.commands.MayorCommands;
import ironcrystal.minecraftrp.event.town.MayorClaimLand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MayorConfirmChunkTimer implements Runnable {
	private Player p;

	public MayorConfirmChunkTimer(Player p) {
		this.p = p;
	}
	@Override
	public void run() {
		p.sendMessage(ChatColor.RED + "[MinecraftRP] You didn't confirm the town!  Try the command again.");
		p.sendMessage(ChatColor.RED + "[MinecraftRP] After you select the chunk type /rp confirm");
		MayorCommands.MayorsConfirmingClaims.remove(p.getUniqueId());
		MayorCommands.MayorTownNames.remove(p.getUniqueId());
		MayorClaimLand.MayorsClaimingChunks.remove(p.getUniqueId());
		MayorClaimLand.ConfirmClaimTaskForPlayer.remove(p.getUniqueId());
	}
}
