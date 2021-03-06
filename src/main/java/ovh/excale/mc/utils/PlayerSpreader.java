package ovh.excale.mc.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import ovh.excale.mc.UHC;

import java.util.Random;

public class PlayerSpreader {

	private final Random random;
	private final World world;
	private final int size;

	public PlayerSpreader(World world, int size) {
		this.world = world;
		this.size = size;
		random = new Random(System.currentTimeMillis());
	}

	public void spread(Player... players) {
		Block block;
		Location location;

		do {
			int x = random.nextInt(size - 2), z = random.nextInt(size - 2), y;
			x -= size / 2;
			z -= size / 2;
			y = world.getHighestBlockYAt(x, z);

			block = world.getBlockAt(x, y, z);
			location = block.getLocation();
			location.setY(location.getY() + 1);

		} while(block.isLiquid());

		Location finalLocation = location;

		if(Bukkit.isPrimaryThread())
			for(Player player : players)
				player.teleport(finalLocation);
		else
			Bukkit.getScheduler()
					.callSyncMethod(UHC.plugin(), () -> {

						for(Player player : players)
							player.teleport(finalLocation);
						return Void.TYPE;

					});
	}

}
