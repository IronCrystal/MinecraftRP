package ironcrystal.minecraftrp.event.contract;

import ironcrystal.minecraftrp.contract.Contract;
import ironcrystal.minecraftrp.contract.ContractManager;
import ironcrystal.minecraftrp.contract.ContractState;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ChestBreak implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void chestBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		for (Contract contract : ContractManager.inProgressContractList) {
			if (contract.getState() == ContractState.INPROGRESS) {
				Block shop = contract.getShopChest();
				Block supply = contract.getSupplyChest();
				if (shop != null && supply != null) {
					if ((shop.getLocation().getBlockX() == block.getLocation().getBlockX() 
							&& shop.getLocation().getBlockY() == block.getLocation().getBlockY()
							&& shop.getLocation().getBlockZ() == block.getLocation().getBlockZ())
						||
							(supply.getLocation().getBlockX() == block.getLocation().getBlockX() 
							&& supply.getLocation().getBlockY() == block.getLocation().getBlockY()
							&& supply.getLocation().getBlockZ() == block.getLocation().getBlockZ())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "[MinecraftRP] You may not break this chest!");
					}
				}
			}
		}
	}
}
