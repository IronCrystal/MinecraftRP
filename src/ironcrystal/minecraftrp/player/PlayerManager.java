package ironcrystal.minecraftrp.player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
	
	public static List<UUID> getAllPlayers() {
		File folder = new File("plugins/MinecraftRP/player");
		File[] files = folder.listFiles();
		List<UUID> players = new ArrayList<UUID>();
		for (File file: files) {
			players.add(UUID.fromString(file.getName()));
		}
		return players;
	}

}
