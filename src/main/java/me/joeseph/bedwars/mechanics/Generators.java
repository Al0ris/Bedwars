package me.joeseph.bedwars.mechanics;

import me.joeseph.bedwars.Bedwars;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Generators {

	private static Generators instance;
	private static Bedwars bedwars = Bedwars.getPlugin(Bedwars.class);

	private Map<UUID, GeneratorType> playerGenerator;
	private Map<Location, GeneratorType> generatorLocation;

	private ItemStack iron = new ItemStack(Material.IRON_INGOT);
	private ItemStack gold = new ItemStack(Material.GOLD_INGOT);
	private ItemStack diamond = new ItemStack(Material.DIAMOND);
	private ItemStack emerald = new ItemStack(Material.EMERALD);
	private Location loc;
	private GeneratorType genType;

	private static Random rand = new Random();
	AtomicInteger output = new AtomicInteger();


	public Generators() {
		this.instance = this;
		playerGenerator = new HashMap<>();
		generatorLocation = new HashMap<>();
	}

	@EventHandler
	public void setGeneratorBlock() {
		Events.subscribe(BlockBreakEvent.class)
				.filter(e -> !playerGenerator.containsKey(e.getPlayer().getUniqueId()))
				.filter(e -> !generatorLocation.containsKey(e.getBlock().getLocation()))
				.filter(e -> e.getPlayer().isOp())
				.filter(e -> e.getPlayer().getGameMode() == GameMode.CREATIVE)
				.handler(e -> {
					generatorLocation.put(e.getBlock().getLocation(), playerGenerator.get(e.getPlayer().getUniqueId()));
					playerGenerator.remove(e.getPlayer().getUniqueId());
					e.setCancelled(true);
					System.out.println(generatorLocation);
				}
				);

	}

	public void generatorCommand() {
		Commands.create()
				.assertPlayer()
				.assertUsage("<GeneratorType>")
				.handler(c -> {
					String generatorType = c.args().get(0).toUpperCase();
					GeneratorType type = GeneratorType.DEFAULT1;
					switch (generatorType) {
						case "DIAMOND":
							type = GeneratorType.DIAMOND;
							break;
						case "EMERALD":
							type = GeneratorType.EMERALD;
							break;
						case "DEFAULT1":
							type = GeneratorType.DEFAULT1;
							break;
						case "DEFAULT2":
							type = GeneratorType.DEFAULT2;
							break;
						case "DEFAULT3":
							type = GeneratorType.DEFAULT3;
							break;
					}
					playerGenerator.put(c.sender().getUniqueId(), type);
					System.out.println(playerGenerator);
				})
				.register("generator", "gen");
	}

	public void generatorMechanics() {
		if (generatorLocation.size() != 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					generatorLocation.forEach((loc, genType) -> {
						output.set(rand.nextInt(100));
						if (genType == GeneratorType.DEFAULT1) {
							if (output.get() <= 20) {
								loc.getWorld().dropItemNaturally(loc, gold);
							} else {
								loc.getWorld().dropItem(loc, iron);
							}
						}
					});
				}
			}.runTaskTimerAsynchronously(bedwars, 0, 60);
		}
	}

}
