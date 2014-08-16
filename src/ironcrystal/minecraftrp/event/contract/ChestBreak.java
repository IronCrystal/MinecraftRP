package ironcrystal.minecraftrp.event.contract;

import ironcrystal.minecraftrp.contract.ContractManager;

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
		if (ContractManager.blockIsActiveChest(block)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "[MinecraftRP] You may not break this chest!");
		}
	}
}
