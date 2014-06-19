package ironcrystal.minecraftrp.commands;

import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.scoreboard.ScoreboardTesting;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OccupationalCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rp")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				OccupationalPlayer player = new OccupationalPlayer(p.getUniqueId());
				//player is typical
				if (args.length == 0) {
					//Send player info on occupation
					outputOccupationInfo(p);
				}
				else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("confirm")) {
						if (player.getOccupation() == Occupations.MAYOR) {
							MayorCommands.confirmClaim(p);
						}
					}
				}
				else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("claim")) {
						MayorCommands.claimLand(p, player, args[1]);
					}
				}

			}
		}
		else if (cmd.getName().equalsIgnoreCase("score")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				ScoreboardTesting.showScoreBoard(p);
			}
		}
		return false;
	}

	private void outputOccupationInfo(Player p) {
		OccupationalPlayer player = new OccupationalPlayer(p.getUniqueId());
		p.sendMessage(ChatColor.BLUE + "[MinecraftRP] Occupation: " + player.getOccupation().toString());
	}
}