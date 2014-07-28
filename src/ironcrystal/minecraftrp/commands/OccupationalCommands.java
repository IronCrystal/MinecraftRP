package ironcrystal.minecraftrp.commands;

import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.Mayor;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.town.Town;
import ironcrystal.minecraftrp.town.TownManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OccupationalCommands implements CommandExecutor {

	private MinecraftRP plugin;
	public OccupationalCommands(MinecraftRP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rp")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				OccupationalPlayer occPlayer = new OccupationalPlayer(player.getUniqueId());
				if (args.length == 0) {
					occPlayer.sendOccupationalInfo();
				}else{
					if (args[0].equalsIgnoreCase("town")) {
						if (args.length == 1) {
							occPlayer.sendTownInfo();
						}else{
							if (args[1].equalsIgnoreCase("create")) {
								if (args.length == 3) {
									if (occPlayer.getOccupation() == Occupations.MAYOR) {
										Mayor mayor = new Mayor(occPlayer.getUUID());
										MayorCommands.claimLand(player, mayor, args[2]);
									}else{
										player.sendMessage(ChatColor.RED + "[MinecraftRP] You must be a mayor!");
									}
								}else{
									player.sendMessage(ChatColor.RED + "[MinecraftRP] Usage: /rp town create <name>");
								}
							}
							else if (args[1].equalsIgnoreCase("expand")) {
								if (occPlayer.getOccupation() == Occupations.MAYOR) {
									Mayor mayor = new Mayor(occPlayer.getUUID());
									MayorCommands.expandTown(player, mayor);
								}else{
									sender.sendMessage(ChatColor.RED + "[MinecraftRP] This command is for Mayors only.");
								}
							}
							else if (args[1].equalsIgnoreCase("list")) {
								player.sendMessage(ChatColor.GREEN + "[MinecraftRP] Who has the time to code this...");
							}
							else if (args[1].equalsIgnoreCase("leave")) {
								if (occPlayer.getOccupation() == Occupations.MAYOR) {
									if (TownManager.getTown(new Mayor(occPlayer.getUUID())) != null) {
										//Player is owner of a town
										//Check if there are any residents.
										Town town = TownManager.getTown(new Mayor(occPlayer.getUUID()));
										if (town.getResidents().size() == 0) {
											//No residents
											TownManager.deleteTown(town);
											player.sendMessage(ChatColor.RED + "[MinecraftRP] Your town has been deleted!");
										}else{
											//Town has residents!  Player must transfer ownership
											player.sendMessage(ChatColor.RED   + "[MinecraftRP] You are currently the owner of a town with residents!");
											player.sendMessage(ChatColor.GREEN + "[MinecraftRP] If you wish to transfer owner ship to a player,");
											player.sendMessage(ChatColor.GREEN + "[MinecraftRP] use the command " + ChatColor.RED + "/rp town setowner <player>");
											player.sendMessage(ChatColor.GREEN + "[MinecraftRP] If you wish to start a global vote for a new mayor,");
											player.sendMessage(ChatColor.GREEN + "[MinecraftRP] use the command " + ChatColor.RED + "/rp town startvote");

										}
									}else{
										//Player is not the owner of a town
										//check if he is the resident of one.
										if (TownManager.getTown(occPlayer.getUUID()) != null) {
											//Player is in a town as a resident
											player.sendMessage(ChatColor.RED + "[MinecraftRP] You leave a town by leaving your town plots!");
										}
									}
								}
								else if (TownManager.getTown(occPlayer.getUUID()) == null) {
									player.sendMessage(ChatColor.RED + "[MinecraftRP] You aren't in a town!");
								}
								else if (TownManager.getTown(occPlayer.getUUID()) != null) {
									player.sendMessage(ChatColor.RED + "[MinecraftRP] You leave a town by leaving your town plots!");
								}
							}
							else if (args[1].equalsIgnoreCase("setowner")) {
								if (args.length == 3) {
									if (occPlayer.getOccupation() == Occupations.MAYOR) {
										Mayor mayor = new Mayor(occPlayer.getUUID());
										MayorCommands.setOwner(mayor, args[2]);
									}else{
										player.sendMessage(ChatColor.RED + "[MinecraftRP] This command is for Mayors only.");
									}
								}else{
									player.sendMessage(ChatColor.RED + "[MinecraftRP] Usage: /rp town setowner <player>");
								}
							}
							else if (args[1].equalsIgnoreCase("startvote")) {
								if (occPlayer.getOccupation() == Occupations.MAYOR) {
									Mayor mayor = new Mayor(occPlayer.getUUID());
									//Start the vote!
									MayorCommands.startVote(mayor, plugin);
								}
							}
							else if (args[1].equalsIgnoreCase("help")) {
								sendTownHelp(player);
							}
							else if (args[1].equalsIgnoreCase("vote")) {
								if (args.length == 3) {
									MayorCommands.vote(occPlayer, args[2]);
								}
							}
							else if (args[1].equalsIgnoreCase("run")) {
								MayorCommands.run(occPlayer);
							}else{
								sendTownHelp(player);
							}
						}
					}
					else if (args[0].equalsIgnoreCase("contract")) {
						if (args.length == 1) {
							ContractCommands.sendContract(player);
						}else{
							if (args[1].equalsIgnoreCase("accept")) {
								ContractCommands.acceptContract(occPlayer);
							}
							else if (args[1].equalsIgnoreCase("decline")) {
								ContractCommands.declineContract(occPlayer);
							}
							else if (args[1].equalsIgnoreCase("send")) {
								if (args.length == 3) {
									ContractCommands.sendContractToPlayer(occPlayer, args[2]);
								}else{
									player.sendMessage(ChatColor.RED + "[MinecraftRP] Error: Syntax: /rp contract send <player>");
								}
							}
						}
					}
					else if (args[0].equalsIgnoreCase("confirm")) {
						if (occPlayer.getOccupation() == Occupations.MAYOR) {
							MayorCommands.confirmClaim(player);
						}
					}
					else if (args[0].equalsIgnoreCase("accept")) {
						MayorCommands.acceptOwner(occPlayer);
					}
					else if (args[0].equalsIgnoreCase("deny")) {
						MayorCommands.denyOwner(occPlayer);
					}
				}
			}
		}
		return false;
	}

	private void sendTownHelp(Player player) {
		player.sendMessage(ChatColor.GREEN + "----------Town Help----------");
		player.sendMessage("");
		player.sendMessage(ChatColor.GREEN + "/rp town               | displays info on your town");
		player.sendMessage(ChatColor.GREEN + "/rp town help        | shows this help page");
		player.sendMessage(ChatColor.GREEN + "/rp town create     | creates a new town");
		player.sendMessage(ChatColor.GREEN + "/rp town expand    | expands the size of your town");
		player.sendMessage(ChatColor.GREEN + "/rp town list          | lists the top towns");
		player.sendMessage(ChatColor.GREEN + "/rp town setowner | change the mayor of your town");
		player.sendMessage(ChatColor.GREEN + "/rp town startvote | starts a public election for a new mayor of your town");
	}
}