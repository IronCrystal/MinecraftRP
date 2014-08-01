package ironcrystal.minecraftrp.event.contract;

import ironcrystal.minecraftrp.contract.Contract;
import ironcrystal.minecraftrp.contract.ContractManager;
import ironcrystal.minecraftrp.occupations.Occupations;
import ironcrystal.minecraftrp.player.OccupationalPlayer;
import ironcrystal.minecraftrp.player.Shopkeeper;
import ironcrystal.minecraftrp.player.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceChest implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPlaceChest(BlockPlaceEvent event) {
		if (!event.isCancelled()) {
			Player player = event.getPlayer();
			Block block = event.getBlockPlaced();

			if (block.getType() == Material.CHEST) {
				if (ContractManager.playersPlacingChests.contains(player.getUniqueId())) {
					ContractManager.playersPlacingChests.remove(player.getUniqueId());
					OccupationalPlayer occPlayer = new OccupationalPlayer(player.getUniqueId());
					if (occPlayer.getOccupation() == Occupations.SHOPKEEPER) {
						Shopkeeper shop = new Shopkeeper(occPlayer.getUUID());
						Contract contract = ContractManager.getUnStartedContract(shop.getUUID());
						contract.setShopChest(block);
						boolean isComplete = ContractManager.checkForBothChests(contract);
						if (isComplete) {
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] You have succesfully started your contract!");
							Bukkit.getPlayer(contract.getSupplier().getUUID()).sendMessage(ChatColor.GREEN + "[MinecraftRP] You contract has started!");
						}else{
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] You succesfully placed your chest, waiting for partner.");
						}
						placeSignOnChest(block, player);
					}
					else if (occPlayer.getOccupation() == Occupations.SUPPLIER) {
						Supplier supply = new Supplier(occPlayer.getUUID());
						Contract contract = ContractManager.getUnStartedContract(supply.getUUID());
						contract.setSupplyChest(block);
						boolean isComplete = ContractManager.checkForBothChests(contract);
						if (isComplete) {
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] You have succesfully started your contract!");
							Bukkit.getPlayer(contract.getSupplier().getUUID()).sendMessage(ChatColor.GREEN + "[MinecraftRP] You contract has started!");
						}else{
							player.sendMessage(ChatColor.GREEN + "[MinecraftRP] You succesfully placed your chest, waiting for partner.");
						}
						placeSignOnChest(block, player);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void placeSignOnChest(Block block, Player p) {
		String data = block.getState().getData().toString();
		BlockFace face;
		byte signDirection = 0;
		if (data.contains("EAST")) {
			face = BlockFace.EAST;
			signDirection = 0x2;
		}
		else if (data.contains("WEST")) {
			face = BlockFace.WEST;
			signDirection = 0x3;
		}
		else if (data.contains("NORTH")) {
			face = BlockFace.NORTH;
			signDirection = 0x4;
		}
		else if (data.contains("SOUTH")) {
			face = BlockFace.SOUTH;
			signDirection = 0x5;
		}else{
			face = null;
		}
		if (face != null) {
			Block relativeBlock = block.getRelative(face);
			relativeBlock.setType(Material.WALL_SIGN);
			BlockState state = relativeBlock.getState();
			state.setRawData(signDirection);
		}
	}
}
