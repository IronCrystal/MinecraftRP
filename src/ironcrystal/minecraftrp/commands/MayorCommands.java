package ironcrystal.minecraftrp.commands;

import ironcrystal.minecraftrp.Files;
import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.event.town.MayorClaimLand;
import ironcrystal.minecraftrp.player.Mayor;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.timer.mayor.EndElectionsTimer;
import ironcrystal.minecraftrp.timer.mayor.HoldElectionsTimer;
import ironcrystal.minecraftrp.timer.mayor.RunForMayorTimer;
import ironcrystal.minecraftrp.town.Town;
import ironcrystal.minecraftrp.town.TownManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MayorCommands {

	public static HashMap<UUID, List<Integer>> MayorsConfirmingClaims = new HashMap<UUID, List<Integer>>();
	public static HashMap<UUID, String> MayorTownNames = new HashMap<UUID, String>();
	public static HashMap<UUID, String> PlayersAcceptingNewOwnership = new HashMap<UUID, String>();
	public static Town townBeingVotedForMayor = null;
	public static List<UUID> PlayersRunningForMayor = new ArrayList<UUID>();
	public static HashMap<UUID, Integer> VotesForPlayers = new HashMap<UUID, Integer>();
	public static List<UUID> Voters = new ArrayList<UUID>();
	public static boolean askingForMayors = false;
	public static boolean voting = false;

	public static void claimLand(Player p, Mayor mayor, String name) {
		if (TownManager.getTown(mayor) == null) {
			OfflinePlayer offP = Bukkit.getOfflinePlayer(p.getUniqueId());
			FileConfiguration fileConfig = new YamlConfiguration();
			Files.loadFile(Files.Config, fileConfig);
			double cost = fileConfig.getDouble("Cost of Starting a Village");
			if (MinecraftRP.econ.getBalance(offP) >= cost) {
				MayorTownNames.put(mayor.getUUID(), name);
				MayorClaimLand.MayorsClaimingChunks.add(mayor.getUUID());
				p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Right Click the Center Chunk for the Town");
			}else{
				p.sendMessage(ChatColor.RED + "[MinecraftRP] You don't have enough money!");
				p.sendMessage(ChatColor.RED + "[MinecraftRP] It costs " + cost + " dollars to start a village.");
			}
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] You already own a town!");
		}
	}

	public static void confirmClaim(Player p) {
		if (MayorCommands.MayorsConfirmingClaims.containsKey(p.getUniqueId())) {
			String name = MayorTownNames.get(p.getUniqueId());

			/**
			 * Save Player File Info
			 */
			File pFile = Files.getPlayerFile(p.getUniqueId());
			FileConfiguration config = new YamlConfiguration();
			Files.loadFile(pFile, config);
			config.set("Town", name);
			Files.saveFile(pFile, config);

			/**
			 * Save Town File Info
			 */
			Town town = TownManager.createNewTown(new Mayor(p.getUniqueId()), name, 1, p.getWorld(), MayorsConfirmingClaims.get(p.getUniqueId()));

			/**
			 * Create World Guard Region
			 */
			town.addWorldGuardRegion();

			/**
			 * Remove Money
			 */
			Files.loadFile(Files.Config, config);
			double cost = config.getDouble("Cost of Starting a Village");
			MinecraftRP.econ.bankWithdraw(p.getName(), cost);

			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Center Chunk Claimed");
			p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Your town has a 1 chunk radius, for a total of 3x3 chunks.");
			p.sendMessage(ChatColor.BLUE + "[MinecraftRP] " + cost + " has been removed from your account.");
			MayorClaimLand.MayorsClaimingChunks.remove(p.getUniqueId());
			MayorsConfirmingClaims.remove(p.getUniqueId());
			MayorTownNames.remove(p.getUniqueId());
			MayorClaimLand.MayorsClaimingChunks.remove(p.getUniqueId());
			int confirmTaskID = MayorClaimLand.ConfirmClaimTaskForPlayer.get(p.getUniqueId());
			Bukkit.getScheduler().cancelTask(confirmTaskID);
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] There is no claim to confirm!  Type /rp claim <name> to claim a town.");
		}
	}

	public static void expandTown(Player p, Mayor mayor) {
		OfflinePlayer offP = Bukkit.getOfflinePlayer(p.getUniqueId());
		Town town = TownManager.getTown(mayor);
		if (town != null) {
			int radius = town.getRadius() + 1;
			FileConfiguration config = new YamlConfiguration();
			/**
			 * Check if player has the money
			 */
			Files.loadFile(Files.Config, config);
			double baseCost = config.getDouble("Cost of Expanding a Village Per Chunk");
			int numberOfChunks = radius * 8;
			double cost = baseCost * numberOfChunks;

			if (MinecraftRP.econ.getBalance(offP) >= cost) {
				town.expandTown();
				p.sendMessage(ChatColor.GREEN + "[MinecraftRP] You have expanded your town!");
				radius = town.getRadius();
				int sideLength = 1 + 2 * radius;
				p.sendMessage(ChatColor.GREEN + "[MinecraftRP] Your town has a " + radius +" chunk radius, for a total of " + sideLength + "x" + sideLength + " chunks.");
				p.sendMessage(ChatColor.BLUE + "[MinecraftRP] " + cost + " has been removed from your account.");
			}else{
				p.sendMessage(ChatColor.RED + "[MinecraftRP] You don't have enough money!");
				p.sendMessage(ChatColor.RED + "[MinecraftRP] It costs " + cost + " dollars to expand your village.");
			}
		}else{
			p.sendMessage(ChatColor.RED + "[MinecraftRP] You don't own a town!");
		}
	}

	public static void acceptOwner(OccupationalPlayer occP) {
		if (PlayersAcceptingNewOwnership.containsKey(occP.getUUID())) {
			Town town = TownManager.getTown(PlayersAcceptingNewOwnership.get(occP.getUUID()));
			Mayor oldMayor = town.getMayor();
			Player oldPlayer = Bukkit.getPlayer(oldMayor.getUUID());
			TownManager.setTownMayor(occP.getUUID(), PlayersAcceptingNewOwnership.get(occP.getUUID()));
			Player player = Bukkit.getPlayer(occP.getUUID());
			player.sendMessage(ChatColor.GREEN + "[MinecraftRP] You are now the owner of " + PlayersAcceptingNewOwnership.get(occP.getUUID()));
			oldPlayer.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + player.getName() + " is now the owner of " + PlayersAcceptingNewOwnership.get(occP.getUUID()));
			PlayersAcceptingNewOwnership.remove(occP.getUUID());
		}else{
			Bukkit.getPlayer(occP.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You have nothing to accept!");
		}
	}

	public static void denyOwner(OccupationalPlayer occP) {
		if (PlayersAcceptingNewOwnership.containsKey(occP.getUUID())) {
			Town town = TownManager.getTown(PlayersAcceptingNewOwnership.get(occP.getUUID()));
			Mayor oldMayor = town.getMayor();
			Player oldPlayer = Bukkit.getPlayer(oldMayor.getUUID());
			Player player = Bukkit.getPlayer(occP.getUUID());
			player.sendMessage(ChatColor.GREEN + "[MinecraftRP] You denied ownership of " + PlayersAcceptingNewOwnership.get(occP.getUUID()));
			oldPlayer.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + player.getName() + " denied ownership of " + PlayersAcceptingNewOwnership.get(occP.getUUID()));
			PlayersAcceptingNewOwnership.remove(occP.getUUID());
		}else{
			Bukkit.getPlayer(occP.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You have nothing to deny!");
		}
	}

	@SuppressWarnings("deprecation")
	public static void setOwner(Mayor mayor, String recipient) {
		Player oldMayor = Bukkit.getPlayer(mayor.getUUID());
		if (TownManager.getTown(mayor) != null) {
			OfflinePlayer offP = Bukkit.getOfflinePlayer(recipient);
			if (offP.isOnline()) {
				Player newmayor = Bukkit.getPlayer(recipient);
				newmayor.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + oldMayor.getName() + " wants to transfer ownership of "
						+ TownManager.getTown(mayor).getName() + " to you!");
				newmayor.sendMessage(ChatColor.GREEN + "[MinecraftRP] Type " + ChatColor.RED + "/rp accept " + ChatColor.GREEN + "or " + ChatColor.RED + "/rp deny");
				PlayersAcceptingNewOwnership.put(newmayor.getUniqueId(), TownManager.getTown(mayor).getName());
			}else{
				oldMayor.sendMessage(ChatColor.RED + "[MinecraftRP] The new owner must be online!");
			}
		}else{
			oldMayor.sendMessage(ChatColor.RED + "[MinecraftRP] You don't own a town!");
		}
	}

	/**
	 * TODO
	 * @param Mayor mayor, Plugin plugin (MinecraftRP instance)
	 */
	public static void startVote(Mayor mayor, MinecraftRP plugin) {
		if (TownManager.getTown(mayor) != null) {
			if (townBeingVotedForMayor == null) {
				townBeingVotedForMayor = TownManager.getTown(mayor);
				askingForMayors = true;
				voting = false;
				int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new RunForMayorTimer(), 0L, 600L);
				HoldElectionsTimer elections = new HoldElectionsTimer(task);
				elections.setID(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, elections, 2400L, 600L));
				EndElectionsTimer endElections = new EndElectionsTimer();
				endElections.setID(elections.getID());
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, endElections, 4800L);
			}else{
				Player p = Bukkit.getPlayer(mayor.getUUID());
				p.sendMessage(ChatColor.RED + "[MinecraftRP] There is already a vote going on!");
			}
		}else{
			Player p = Bukkit.getPlayer(mayor.getUUID());
			p.sendMessage(ChatColor.RED + "[MinecraftRP] You don't own a town!");
		}
	}

	@SuppressWarnings("deprecation")
	public static void vote(OccupationalPlayer player, String name) {
		if (voting) {
			if (!Voters.contains(player.getUUID())) {
				Player p = Bukkit.getPlayer(name);
				if (p != null) {
					if (VotesForPlayers.containsKey(p.getUniqueId())) {
						int votes = VotesForPlayers.get(p.getUniqueId()) + 1;
						VotesForPlayers.put(p.getUniqueId(), votes);
						Voters.add(player.getUUID());
						Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.GREEN + "[MinecraftRP] Thank you for voting!");
					}else{
						Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] That player isn't running for mayor!");
					}
				}else{
					Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] Can't find that player!  He may be offline!");
				}
			}else{
				Player p = Bukkit.getPlayer(player.getUUID());
				p.sendMessage(ChatColor.RED + "[MinecraftRP] You already voted!");
			}
		}else{
			Player p = Bukkit.getPlayer(player.getUUID());
			p.sendMessage(ChatColor.RED + "[MinecraftRP] There isn't a vote going on!");
		}
	}

	public static void run(OccupationalPlayer player) {
		Player pl = Bukkit.getPlayer(player.getUUID());
		if (askingForMayors) {
			if (!PlayersRunningForMayor.contains(player.getUUID())) {
				PlayersRunningForMayor.add(player.getUUID());
				Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.GREEN + "[MinecraftRP] You are now running for mayor of " + townBeingVotedForMayor.getName());
				for (Player p: Bukkit.getOnlinePlayers()) {
					p.sendMessage(ChatColor.GREEN + "[MinecraftRP] " + pl.getName() + " joined in the running for mayor of " + townBeingVotedForMayor.getName());
				}
				VotesForPlayers.put(player.getUUID(), 0);
			}else{
				Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] You are already running!");
			}
		}else{
			Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.RED + "[MinecraftRP] There isn't an election going on!");
		}
	}
}
