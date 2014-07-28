package ironcrystal.minecraftrp.timer.mayor;

import ironcrystal.minecraftrp.event.town.MayorClaimLand;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MayorClaimChunkVisualTimer implements Runnable {
	
	private HashMap<Location, Material> hashmap;
	private Player p;
	
	public MayorClaimChunkVisualTimer(HashMap<Location, Material> hashmap, Player p) {
		this.hashmap = hashmap;
		this.p = p;
	}

	@Override
	public void run() {
		for (Entry<Location, Material> value: hashmap.entrySet()) {
			Location loc = value.getKey();
			loc.getBlock().setType(value.getValue());
		}
		MayorClaimLand.VisualGlassTasksForPlayer.remove(p.getUniqueId());
	}
}