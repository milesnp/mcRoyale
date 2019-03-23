package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class McRoyalePeaceListener implements Listener{
	McRoyale pl;
	public McRoyalePeaceListener(McRoyale plugin) {
		pl = plugin;
	}
	
 @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
    	Bukkit.broadcastMessage(e.getEntity().getName() + " was damaged by " + e.getDamager().getName());
      if (McRoyale.peaceTime && e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
        e.setCancelled(true);
        e.getEntity().sendMessage("Peacetime is active.");
        e.getDamager().sendMessage("Peacetime is active.");
      }
    }
}
