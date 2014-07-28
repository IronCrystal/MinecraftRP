package ironcrystal.minecraftrp.timer.mayor;

import ironcrystal.minecraftrp.commands.MayorCommands;
import ironcrystal.minecraftrp.town.TownManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EndElectionsTimer implements Runnable {

	private int IDToEnd;

	public int getID() {
		return IDToEnd;
	}

	public void setID(int id) {
		this.IDToEnd = id;
	}

	@Override
	public void run() {
		Bukkit.getScheduler().cancelTask(IDToEnd);
		MayorCommands.voting = false;
		//Tally Up Scores
		int topScore = 0;
		int secondScore = 0;
		int thirdScore = 0;
		List<UUID> winners = new ArrayList<UUID>();
		List<UUID> second = new ArrayList<UUID>();
		List<UUID> third = new ArrayList<UUID>();
		for (UUID uuids : MayorCommands.PlayersRunningForMayor) {
			int votes = MayorCommands.VotesForPlayers.get(uuids);
			if (votes >= topScore) {
				thirdScore = secondScore;
				secondScore = topScore;
				topScore = votes;
			}
			else if (votes >= secondScore) {
				thirdScore = secondScore;
				secondScore = votes;
			}
			else if (votes >= thirdScore) {
				thirdScore = votes;
			}
		}
		for (UUID uuids : MayorCommands.PlayersRunningForMayor) {
			if (MayorCommands.VotesForPlayers.get(uuids) == topScore && topScore != 0) {
				winners.add(uuids);
			}
			else if (MayorCommands.VotesForPlayers.get(uuids) == secondScore && secondScore != 0) {
				second.add(uuids);
			}
			else if (MayorCommands.VotesForPlayers.get(uuids) == thirdScore && thirdScore != 0) {
				third.add(uuids);
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {

			if (winners.size() > 1) {
				p.sendMessage(ChatColor.RED + "[MinecraftRP] There was a tie for the top vote, so no mayor elected!");
				p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Here are the results!");
				for (UUID uuid: winners) {
					p.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + Bukkit.getPlayer(uuid).getName() + " - " + MayorCommands.VotesForPlayers.get(uuid) + " votes");
				}
				for (UUID uuid: second) {
					p.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + Bukkit.getPlayer(uuid).getName() + " - " + MayorCommands.VotesForPlayers.get(uuid) + " votes");
				}
				for (UUID uuid: third) {
					p.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + Bukkit.getPlayer(uuid).getName() + " - " + MayorCommands.VotesForPlayers.get(uuid) + " votes");
				}
			}else{
				Player pl = Bukkit.getPlayer(winners.get(0));
				p.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + pl.getName() + " has won the election!");
				p.sendMessage("");
				for (UUID uuid: winners) {
					p.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + Bukkit.getPlayer(uuid).getName() + " - " + MayorCommands.VotesForPlayers.get(uuid) + " votes");
				}
				for (UUID uuid: second) {
					p.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + Bukkit.getPlayer(uuid).getName() + " - " + MayorCommands.VotesForPlayers.get(uuid) + " votes");
				}
				for (UUID uuid: third) {
					p.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + Bukkit.getPlayer(uuid).getName() + " - " + MayorCommands.VotesForPlayers.get(uuid) + " votes");
				}
				p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Congratulations to " + pl.getName() + " for being elected mayor of " + MayorCommands.townBeingVotedForMayor.getName());
				/**
				 * TODO
				 */
				TownManager.setTownMayor(winners.get(0), MayorCommands.townBeingVotedForMayor.getName());
			}
		}
		MayorCommands.townBeingVotedForMayor = null;
		MayorCommands.Voters.clear();
		MayorCommands.VotesForPlayers.clear();
		MayorCommands.PlayersRunningForMayor.clear();
	}
}
