package ironcrystal.minecraftrp.commands;

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
				if (sender.hasPermission("rp.admin")) {
					//player is an admin
				}else{
					//player is typical
					if (args.length == 0) {
						//Send player info on occupation
						outputOccupationInfo(p);
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
		OccupationalPlayer player = new OccupationalPlayer(p.getName());
		p.sendMessage(ChatColor.BLUE + "[MinecraftRP] Occupation: " + player.getOccupation().toString().toLowerCase());
	}
}