package com.mileses.mcroyale;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class McRoyaleRoundObject {
	private McRoyale pl;
	private boolean active = false;
	private boolean walls;
	private boolean peace;
	private boolean tele;
	private int wallLength;
	private int peaceTime;
	private int teleTime;
	private Location location;
	String playerString;
	BukkitTask tpWarnEvent;
	BukkitTask tpEvent;
	BukkitTask peaceEvent;
	String[] playerList;
	boolean peaceTimeActive = false;
	private int peaceEventId;
	private int tpEventID;
	private int tpWarnEventId;
	HashMap<UUID, Boolean> activePlayers;
	

	// construct an McRoyaleRound object
	public McRoyaleRoundObject(McRoyale pl, boolean walls, int wallLength, boolean peace, int peaceTime, boolean tele,
			int teleTime, Location location) {
		this.pl = pl;
		this.walls = walls;
		this.wallLength = wallLength;
		this.peace = peace;
		this.peaceTime = peaceTime;
		this.tele = tele;
		this.teleTime = teleTime;
		this.location = location;
		activePlayers = new HashMap<UUID, Boolean>();
	}

	// start round
	public void startRound() {
		/* should I register the listeners here and deregister on round end? Then I
		*  don't need to check active. */
		McRoyale.roundActive = true;
		
		//register opted-in players to round list
		for (Entry<UUID, Boolean> entry : McRoyale.playerOpts.entrySet()) {
			if (entry.getValue() && Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(entry.getKey())))
				activePlayers.put(entry.getKey(),true);
		}
		
		// do walls
		if (walls) {
			if (wallLength == 0) {
				wallLength = calcWallLength();
			}
			generateWalls();
		}

		// reset players TODO preserve inventory, xp, hp, hunger, location etc
		resetPlayers();

		// do peaceTime
		if (peace) {
			startPeace();
		}

		// do teleTime
		if (tele) {
			startTeleTimer();
		}
		String playerString = "";
		// move players to world and construct player string for spread command.
		for (UUID uuid : activePlayers.keySet()) {
			Player player = Bukkit.getPlayer(uuid);
			if (Bukkit.getOnlinePlayers().contains(player)) {
				playerString = playerString + player.getName();
				World world = player.getWorld();
				if (world != location.getWorld()) {
					player.teleport(location.getWorld().getSpawnLocation());
				}
			}
		}
		
		//spread players
		int distance = calculateSpreadDistance();
		McRoyale.sendDebug("running spreadplayers..");
		String commandString = "spreadplayers " + Integer.toString(location.getBlockX()) + " "
				+ Integer.toString(location.getBlockZ()) + " " + Integer.toString(distance) + " "
				+ Integer.toString((wallLength / 2) - 1) + " false" + playerString;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
		
		
	}

	/*
	 * end round: cancel all TP events, run peacetime event. restore PVP, restore
	 * inventory if that gets implemented. Send players back to main world if that
	 * is implemented.
	 */
	public void endRound(UUID winner) {
		if (tele && tpWarnEvent != null) {
			//cancel tp warning event
			Bukkit.getScheduler().cancelTask(tpWarnEventId);
		}
		if (tele && tpEvent != null) {
			//cancel tp event
			Bukkit.getScheduler().cancelTask(tpEventID);
		}
		if (peace && peaceEvent != null) {
			//end peace time
			peaceTimeActive = false;
			//cancel peace event
			Bukkit.getScheduler().cancelTask(peaceEventId);
		}
		//congratulate winner
		Bukkit.getPlayer(winner).sendMessage("VICTORY ROYALE!");
		Bukkit.broadcastMessage(ChatColor.GOLD + Bukkit.getPlayer(winner).getName() + ChatColor.WHITE + " is the winner!");
		McRoyale.changeWins(Bukkit.getPlayer(winner), 1);
		McRoyale.roundActive = false;
		//Location newLocation;
	}

	public int calcWallLength() {
		int newWallLength;
		int playercount = playerList.length;
		newWallLength = (int) Math.sqrt(10000 * playercount);
		return newWallLength;
	}

	public void generateWalls() {
		// walls logic using location
	}

	public void startPeace() {
		// create peace runnable for end of peace timer
		peaceEvent = new McRoyalePeaceRunnable().runTaskLater(pl, peaceTime);
		//get ID for event for canceling on round end.
		peaceEventId = peaceEvent.getTaskId();
	}

	public void startTeleTimer() {
		//create warning event ~30 seconds (600 ticks) before end of normal timer.
		tpWarnEvent = new McRoyaleTPWarningRunnable(this, pl, teleTime).runTaskLater(pl, teleTime - 600);
		//get ID for event for canceling on round end.
		tpWarnEventId = tpWarnEvent.getTaskId();
	}

	public void resetPlayers() {
		for (UUID uuid : activePlayers.keySet()) {
			if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) {
			//TODO save player info to restore at round end.
			Player player = Bukkit.getPlayer(uuid);
			// reset player to full, healed, clear and survival.
			player.setGameMode(GameMode.SURVIVAL);
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
			player.setSaturation(5);
			player.setExp(0);
			player.setExhaustion(0);
			clearInventory(player);
			playerString = playerString.concat(" " + player.getName());
			}
		}
	}

	public static void clearInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
	}
	public int calculateSpreadDistance() {
		int distance = wallLength / (activePlayers.size() + 1);
		if (distance >= wallLength / 2) {
			distance = wallLength / 2 + 1;
		}
		return distance;
	}
}
