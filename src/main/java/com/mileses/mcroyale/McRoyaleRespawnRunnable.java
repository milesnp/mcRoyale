package com.mileses.mcroyale;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;



public class McRoyaleRespawnRunnable extends BukkitRunnable{
	McRoyale pl;
	public static List<String> item_lore = new ArrayList<>();
	Player player;
	public McRoyaleRespawnRunnable(Player player, McRoyale pl) {
		this.pl = pl;
		this.player = player;
	}

	@Override
	public void run() {
		String commandString = "gamemode 3 " + player.getName();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
		//player.setGameMode(GameMode.SPECTATOR);
		if (Bukkit.getPluginManager().isPluginEnabled("CompassRadar")) {
			player.setItemInHand(getTracker());
		}
	}
		public static ItemStack getTracker() {
			ItemStack tracker = new ItemStack(Material.COMPASS, 1);
			ItemMeta tracker_meta = tracker.getItemMeta();
			
			tracker_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bPlayer Radar"));
			item_lore.add(ChatColor.translateAlternateColorCodes('&',"&eThis radar will be pointed to"));
			item_lore.add(ChatColor.translateAlternateColorCodes('&',"&enearest player in your world."));
			tracker_meta.setLore(item_lore);
			tracker.setItemMeta(tracker_meta);

			return tracker;
		}
		
	

}
