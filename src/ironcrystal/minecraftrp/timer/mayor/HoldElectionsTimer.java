package ironcrystal.minecraftrp.timer.mayor;

import ironcrystal.minecraftrp.commands.MayorCommands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HoldElectionsTimer implements Runnable {
	private int ID;
	private int RunForMayorTaskID;
	
	public HoldElectionsTimer(int taskId) {
		this.RunForMayorTaskID = taskId;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}

	@Override
	public void run() {
		Bukkit.getScheduler().cancelTask(RunForMayorTaskID);
		MayorCommands.voting = true;
		MayorCommands.askingForMayors = false;
		if (MayorCommands.PlayersRunningForMayor.size() == 0) {
			for (Player p: Bukkit.getOnlinePlayers()) {
				p.sendMessage(ChatColor.RED + "[MinecraftRP] No one ran for mayor!");
			}
			MayorCommands.voting = false;
			Bukkit.getScheduler().cancelTask(ID);
		}
		for (Player p: Bukkit.getOnlinePlayers()) {
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Vote for a new mayor for " + MayorCommands.townBeingVotedForMayor.getName());
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Vote with " + ChatColor.RED + "/rp town vote <player>");
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Players running for mayor:");
			for (UUID uuid : MayorCommands.PlayersRunningForMayor) {
				Player mayor = Bukkit.getPlayer(uuid);
				p.sendMessage(ChatColor.GREEN + mayor.getName());
			}
		}
	}
}