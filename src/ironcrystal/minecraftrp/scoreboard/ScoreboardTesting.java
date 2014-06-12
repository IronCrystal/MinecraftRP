package ironcrystal.minecraftrp.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardTesting {
	
	public static void showScoreBoard(Player p) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		 
		Objective objective = board.registerNewObjective("Occupation", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "Occupational Info");
		
		Score timeLeftOnContract = objective.getScore("Finish in (day)");
		Score income = objective.getScore("Income");
		
		timeLeftOnContract.setScore(2);
		income.setScore(10);
		
		p.setScoreboard(board);
	}
}
