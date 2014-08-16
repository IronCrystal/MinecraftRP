package ironcrystal.minecraftrp.event;

import ironcrystal.minecraftrp.MinecraftRP;
import ironcrystal.minecraftrp.event.contract.ChestBreak;
import ironcrystal.minecraftrp.event.contract.CreateContract;
import ironcrystal.minecraftrp.event.contract.PlaceChest;
import ironcrystal.minecraftrp.event.contract.SupplierSendItems;
import ironcrystal.minecraftrp.event.town.MayorClaimLand;
import ironcrystal.minecraftrp.event.town.PlayerJoinTown;
import ironcrystal.minecraftrp.event.town.PlayerLeaveTown;
import ironcrystal.minecraftrp.event.town.RegionCreation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Listeners {
	private static PlayerJoin playerJoin;
	private static CreateOccupationSign createSign;
	private static ChangeOccupation changeOccup;
	private static MayorClaimLand claimLand;
	private static RegionCreation regionCreation;
	private static PlayerJoinTown playerJoinTown;
	private static PlayerLeaveTown playerLeaveTown;
	private static PlayerExitServer playerExitServer;
	private static CreateContract createContract;
	private static PlaceChest placeChest;
	private static ChestBreak chestBreak;
	private static SupplierSendItems supplierSendItems;
	
	private static void initializeListeners(MinecraftRP main) {
		playerJoin = new PlayerJoin();
		createSign = new CreateOccupationSign();
		changeOccup = new ChangeOccupation();
		claimLand = new MayorClaimLand(main);
		regionCreation = new RegionCreation();
		playerJoinTown = new PlayerJoinTown();
		playerLeaveTown = new PlayerLeaveTown();
		playerExitServer = new PlayerExitServer();
		createContract = new CreateContract();
		placeChest = new PlaceChest();
		chestBreak = new ChestBreak();
		supplierSendItems = new SupplierSendItems();
	}
	
	public static void registerEvents(MinecraftRP main) {
		Listeners.initializeListeners(main);
		Bukkit.getServer().getPluginManager().registerEvents(playerJoin, main);
		Bukkit.getServer().getPluginManager().registerEvents(createSign, main);
		Bukkit.getServer().getPluginManager().registerEvents(changeOccup, main);
		Bukkit.getServer().getPluginManager().registerEvents(claimLand, main);
		Bukkit.getServer().getPluginManager().registerEvents(regionCreation, main);
		Bukkit.getServer().getPluginManager().registerEvents(playerJoinTown, main);
		Bukkit.getServer().getPluginManager().registerEvents(playerLeaveTown, main);
		Bukkit.getServer().getPluginManager().registerEvents(playerExitServer, main);
		Bukkit.getServer().getPluginManager().registerEvents(createContract, main);
		Bukkit.getServer().getPluginManager().registerEvents(placeChest, main);
		Bukkit.getServer().getPluginManager().registerEvents(chestBreak, main);
		Bukkit.getServer().getPluginManager().registerEvents(supplierSendItems, main);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MinecraftRP] Listeners Registered");
	}

}
