package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;

public class McRoyaleTPRunnable extends BukkitRunnable {
	public McRoyale pl;
	public int tpTimer;
	BukkitTask tpWarningRunnable;
	public McRoyaleTPRunnable(McRoyale pl, int tpTimer) {
		this.pl = pl;
		this.tpTimer = tpTimer;
	}
	

	@Override
	public void run() {
		if (!McRoyale.roundActive) this.cancel();
		
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "Teleporting all players to top of map!");
		for (Player p: Bukkit.getOnlinePlayers()) {
			if (pl.playerList.containsKey(p.getName()) && pl.playerList.get(p.getName())){
				Location location = p.getLocation();
				World world = p.getWorld();
				location.setY(world.getHighestBlockYAt(location));
				Location underLocation = new Location(location.getWorld(), location.getX(),location.getY(),location.getZ());
				if (underLocation.subtract(0, 1, 0).getBlock().isLiquid()) {
					underLocation.getBlock().setType(Material.COBBLESTONE);
				}
				
				p.teleport(location);
			}
		}
		
		tpWarningRunnable = new McRoyaleTPWarningRunnable(pl, tpTimer).runTaskLater(pl, ((tpTimer*1200) - 600));
		
	}

}
