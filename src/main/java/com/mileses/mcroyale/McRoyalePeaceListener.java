package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class McRoyalePeaceListener implements Listener {
	McRoyale pl;

	public McRoyalePeaceListener(McRoyale plugin) {
		pl = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (McRoyale.roundActive) {
			if (McRoyale.peaceTime && e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
				Bukkit.broadcastMessage(
						e.getDamager().getName() + " sure looks like BooBoo the Fool! Peacetime is still active.");
				e.setCancelled(true);
				e.getEntity().sendMessage("Peacetime is active.");
				e.getDamager().sendMessage("Peacetime is active.");
			}
		}
	}
}
