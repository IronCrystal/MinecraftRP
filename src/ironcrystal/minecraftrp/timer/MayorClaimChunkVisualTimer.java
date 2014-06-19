package ironcrystal.minecraftrp.timer;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;

public class MayorClaimChunkVisualTimer implements Runnable {
	
	private HashMap<Location, Material> hashmap;
	
	public MayorClaimChunkVisualTimer(HashMap<Location, Material> hashmap) {
		this.hashmap = hashmap;
	}

	@Override
	public void run() {
		for (Entry<Location, Material> value: hashmap.entrySet()) {
			Location loc = value.getKey();
			loc.getBlock().setType(value.getValue());
		}
	}
}