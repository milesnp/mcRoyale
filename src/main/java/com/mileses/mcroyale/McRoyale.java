package com.mileses.mcroyale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.avaje.ebean.EbeanServer;

public final class McRoyale extends JavaPlugin {
	private static McRoyale instance;
	private static Logger logger;
	private static ScoreboardManager scoreManager;
	static boolean debugActive = false;
	public static Scoreboard board;
	public static Objective killStat;
	public static Objective deathStat;
	public static Objective winStat;
	public static Objective royaleHP;
	public static Objective oreStat;
	public static boolean peaceTimeActive;
	public static boolean roundActive;
	public static HashMap<UUID, Boolean> playerOpts;
	public static EbeanServer database;
	public static BukkitTask statRunnable;
	public static BukkitTask tpWarningRunnable;
	public int tpTimer = 5;
	String tpTimerString = " minutes.";
	public static World royaleWorld;
	//setup parameters with defaults:
	boolean defwalls = true;
	boolean defpeace = false;
	boolean deftele = false;
	int defwallLength = 0;
	//now in seconds
	int defpeaceTime = 60; //one minute
	int defteleTime = 300; // 5 minutes
	McRoyaleRoundObject currentRound;
	
	
	
	@Override
	public void onEnable() {
		instance = this;
		logger = getLogger();
		ScoreboardSetup();
		setupDatabase();
		McRoyaleRespawnListener mrrl = new McRoyaleRespawnListener(this);
		McRoyaleOreListener mrol = new McRoyaleOreListener(this);
		McRoyaleDeathListener mrdl = new McRoyaleDeathListener(this);
		McRoyalePeaceListener mrpl = new McRoyalePeaceListener(this);
		getServer().getPluginManager().registerEvents(mrrl, this);
		getServer().getPluginManager().registerEvents(mrol, this);
		getServer().getPluginManager().registerEvents(mrdl, this);
		getServer().getPluginManager().registerEvents(mrpl, this);
		playerOpts = new HashMap<UUID, Boolean>();
		for (Player x : Bukkit.getOnlinePlayers())
			playerOpts.put(x.getUniqueId(), true);
		roundActive = false;
	}

	@Override
	public void onDisable() {
		instance = null;
		logger = null;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("royale")) {
			// check for arguments (subcommands)
			if (args.length > 0) {
				// check for help
				if (args[0].equalsIgnoreCase("help")) {
					if (args.length >= 2) {
						if (args[1].equalsIgnoreCase("wall")) {
							sender.sendMessage("/royale wall <wall length> OR /royale wall <length> <x> <z>");
							return true;
						}
						if (args[1].equalsIgnoreCase("round")) {
							sender.sendMessage("/royale round <wall length> <peacetime in ticks or false> [x z]");
							return true;
						}
						if (args[1].equalsIgnoreCase("newround")) {
							sender.sendMessage("/royale newround length peacetime tptime genWalls? x z world");
						}
					} else
						sender.sendMessage("/royale help wall or /royale help round");
					return true;
				}
				//check for debug command
				if (args[0].equalsIgnoreCase("debug")) {
					debugActive = !debugActive;
					sender.sendMessage("Debug is now " + Boolean.toString(debugActive));
				}
				//end debug command
				
				//check for get world command
				if (args[0].equalsIgnoreCase("world")) {
					royaleWorld = getRoyaleWorld();
					if (sender instanceof Player)
						((Player) sender).teleport(royaleWorld.getSpawnLocation());
				}
				//end get world command
				
				//check for set world command
				if (args[0].equalsIgnoreCase("setworld")) {
					if (sender instanceof Player) {
						royaleWorld = ((Player) sender).getWorld();
						return true;
					}
					else 
						{
						sender.sendMessage("Player only command");
						return false;
					}
				}
				//end set world command
				
				//check for tp timer command
				if (args[0].equalsIgnoreCase("tptimer")) {
					if (args.length > 1) {
						if (isInt(args[1]) && Integer.parseInt(args[1]) > 0) {
							if (args[1] == "1") tpTimerString = " minute.";
							else tpTimerString = " minutes.";
							sender.sendMessage("Teleport timer set to " + args[1] + tpTimerString);
							tpTimer = Integer.parseInt(args[1]);
							return true;
						}
					}
				}
				//end tp timer command
				
				// check for wall command
				if (args[0].equalsIgnoreCase("wall")) {
					// check length if sender is Player
					if ((sender instanceof Player) && (args.length == 2)) {
						// init length at 3 for debug and to cheat around a compile error.
						int iLength;
						// check for Integer
						// automatically calculate length based on number of players. uses 10,000 blocks
						// area per player.
						if (args[1].equalsIgnoreCase("auto")) {
							int playercount = Bukkit.getOnlinePlayers().size();
							iLength = (int) Math.sqrt(10000 * playercount);

						} else if (!isInt(args[1])) {
							sender.sendMessage("Argument should be a whole number.");
							return false;
						} else
							iLength = Integer.parseInt(args[1]);
						if (iLength <= 2) {
							sender.sendMessage("Length must be positive and larger than 2.");
							return false;
						}
						generateWalls(((Player) sender).getLocation(), iLength);
						return true;
					} else {
						// check if coords are given
						if (args.length == 4) {
							// check args for integership
							// default to 3 to show something is wrong
							int iLength = 3;
							int ix;
							int iz;
							if (isInt(args[1])) {
								iLength = Integer.parseInt(args[1]);
								if (iLength <= 2) {
									sender.sendMessage("Length must be positive and larger than 2.");
									return false;
								}
							}
							// automatically calculate length based on number of players. uses 10,000 blocks
							// area per player.
							else if (args[1].equalsIgnoreCase("auto")) {
								int playercount = Bukkit.getOnlinePlayers().size();
								iLength = (int) Math.sqrt(10000 * playercount);
							} else {
								sender.sendMessage("Length must be a number or \"auto\"");
								return false;
							}
							if (isInt(args[2]) && isInt(args[3])) {
								ix = Integer.parseInt(args[2]);
								iz = Integer.parseInt(args[3]);
							} else {
								sender.sendMessage("Co-ords must be whole numbers.");
								return false;
							}
							World world = Bukkit.getServer().getWorld("World");
							Location location = new Location(world, ix, 0, iz);
							generateWalls(location, iLength);
							return true;
						} else if (sender instanceof Player) {
							sender.sendMessage("Wrong number of arguments. Should be 2 or 4, not: "
									+ Integer.toString(args.length));
							return false;
						} else {
							sender.sendMessage("Must use 4 arguments in console.");
							return false;
						}
					}
				}
				// end wall command

				// check for manual start command
				if (args[0].equalsIgnoreCase("start")) {
					roundActive = true;
					Bukkit.broadcastMessage("Starting round with no automation. Good luck!");
				}
				// end manual start command
				//check for new round command
				if (args[0].equalsIgnoreCase("newround")) {
					int argsLength = args.length;
					Location location;
					//setup parameters with defaults:
					World world;
					boolean walls = defwalls;
					boolean peace = defpeace;
					boolean tele = deftele;
					int x= 0;
					int z = 0;
					int wallLength =defwallLength;
					//now in seconds
					int peaceTime = defpeaceTime; //one minute
					int teleTime = defteleTime; // 5 minutes
					// /royale 0 		1	   2		 3		4 		 5 6 7
					// /royale newround length peacetime tptime genWalls? x z world
					
					// get length if specified, 0 if auto.
					if (argsLength >= 2) {
						if (args[1].equalsIgnoreCase("auto") )
							wallLength = 0;
						else if (args[1].equalsIgnoreCase("false"))
							walls = false;
						else if (isInt(args[1]) && Integer.parseInt(args[1]) > 0) {
							wallLength = Integer.parseInt(args[1]);
						}
						else {
							sender.sendMessage("Wall length should be \"false\", \"auto\", or positive.");
							return false;
						}
					}
					//get peacetime
					if (argsLength >= 3) {
						if (args[2].equalsIgnoreCase("false"))
							peace = false;
						else if (isInt(args[2]) && Integer.parseInt(args[2]) > 0)
								peaceTime = Integer.parseInt(args[2]);
						else {
							sender.sendMessage("Peace time should be \"false\" or positive.");
							return false;
						}
					}
					//get tptime
					if (argsLength >= 4) {
						if (args[3].equalsIgnoreCase("false"))
							tele = false;
						else if (isInt(args[3]) && Integer.parseInt(args[3]) > 0) 
							teleTime = Integer.parseInt(args[3]);
						else {
							sender.sendMessage("Teleport time should be \"false\" or positive.");
							return false;
						}
					}
					//get genWalls? default true.
					if (argsLength >= 5) {
						if (args[4].equalsIgnoreCase("false")) {
							walls = false;
						}
						else if (args[4].equalsIgnoreCase("true"))
							walls = true;
						else {
							sender.sendMessage("Generate Walls? should be true or false.");
						}
					}
					
					//get x z
					if (argsLength == 6) {
						sender.sendMessage("x and z must both be present.");
						return false;
					}
					else location = ((Player) sender).getLocation();
					if (argsLength >= 7) {
						if (isInt(args[5]) && isInt(args[6])) {
							x = Integer.parseInt(args[5]);
							z = Integer.parseInt(args[6]);
						}
						else {
							sender.sendMessage("x and z must be integers.");
							return false;
						}
						world = ((Player) sender).getWorld();
					}
					else location = ((Player) sender).getLocation();
					//get world
					if (argsLength >= 8) {
						world = Bukkit.getWorld(args[7]);
						if(world == null){
							sender.sendMessage("The world \"" + args[7] +"\" does not exist.");
							if (args[7].equalsIgnoreCase("royale")) {
								sender.sendMessage("Royale world has not been generated. Generating now.");
								sender.sendMessage("Please re-run your round start command after a few seconds.");
								getRoyaleWorld();
							}
							return false;
						}
						location = new Location(world, x, 0, z);
					}
					//populate list
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (!playerOpts.containsKey(player.getUniqueId())) {
							playerOpts.put(player.getUniqueId(), true);
						}
					}
					
					//create round
					currentRound = new McRoyaleRoundObject(getInst(), walls, wallLength, peace, peaceTime, tele, teleTime, location);
					currentRound.startRound();
					
					return true;					
				}
				
				// check for round command and for args
				if ((args[0].equalsIgnoreCase("round") ||args[0].equalsIgnoreCase("tpround"))&& args.length >= 3) {
					Location location;
					// check that optional x z args are missing
					if ((args.length < 5)) {
						if (sender instanceof Player) {
							location = ((Player) sender).getLocation();
						} else {
							sender.sendMessage("console round start requires coords.");
							return false;
						}
					}
					// check that x and z are integers and set location accordingly
					else if (isInt(args[3]) && isInt(args[4])) {
						int ix = Integer.parseInt(args[3]);
						int iz = Integer.parseInt(args[4]);
						location = new Location(((Player) sender).getWorld(), ix, 0, iz);
					} else {
						sender.sendMessage("x and z should be numbers.");
						return false;
					}

					// check that peacetime is either "false" or a number
					int length;
					int peaceTimeArg;
					// set peacetime
					if (isInt(args[2]) && Integer.parseInt(args[2]) >= 0) {
						peaceTimeArg = Integer.parseInt(args[2]);
					} else if (args[2].equalsIgnoreCase("false"))
						peaceTimeArg = 0;
					else {
						sender.sendMessage("Peace time must either be \"false\" or positive number of whole minutes.");
						return false;
					}
					// check length is positive int and set
					if (isInt(args[1]) && (Integer.parseInt(args[1]) >= 0)) {
						if (Integer.parseInt(args[1]) >= 5 || Integer.parseInt(args[1]) == 0) {
							length = Integer.parseInt(args[1]);
						} else {
							sender.sendMessage("Length must be 0, greater than 5, \"auto\" or \"false\"");
							return false;
						}
					} else if (args[1].equalsIgnoreCase("auto")) {
						int playercount = Bukkit.getOnlinePlayers().size();
						length = (int) Math.sqrt(10000 * playercount);
					} else if (args[1].equalsIgnoreCase("false")) {
						length = 0;
					} else
						return false;
					// list all players
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player instanceof Player) {
							playerOpts.put(player.getUniqueId(), true);
							SetupPlayerScore(player);
						}
					}
					// start round
					logger.info("Scoreboard switcher started");
					statRunnable = new McRoyaleStatRunnable().runTaskTimer(this, 400, 400);
					McRoyaleRound.startRound(location, length, playerOpts, (Player) sender, peaceTimeArg);
					if (args[0].equalsIgnoreCase("tpround")) {
						Bukkit.broadcastMessage("Players will be teleported to the surface every " + ChatColor.RED + Integer.toString(tpTimer) + tpTimerString);
						//tpWarningRunnable = new McRoyaleTPWarningRunnable(this, tpTimer).runTaskLater(this, ((tpTimer*1200) - 600));
					}
					return true;

				}
				// end round command

			}
		}

		return false;

	}

	public boolean isInt(String s) {
		return s.matches("-?\\d+");
	}

	public static void generateWalls(Location loc, int length) {
		Bukkit.broadcastMessage("Building walls of length " + Integer.toString(length));
		// get corner relative to player position
		// uses getBlockN() instead of getN() for int
		int absLength = Math.abs(length);
		int x1 = loc.getBlockX() - (absLength / 2);
		int y1 = 0;
		int z1 = loc.getBlockZ() - (absLength / 2);

		// create opposite corner adding length
		int x2 = x1 + absLength;
		int y2 = 150;
		int z2 = z1 + absLength;

		World world = loc.getWorld();

		// loop z
		logger.info("wall loop started");
		for (int yPoint = y1; yPoint <= y2; yPoint++) {
			// loop x

			for (int xPoint = x1; xPoint <= x2; xPoint++) {
				int zPoint = z1;
				Block currentBlock = world.getBlockAt(xPoint, yPoint, zPoint);
				currentBlock.setType(Material.BEDROCK);
				zPoint = z2;
				currentBlock = world.getBlockAt(xPoint, yPoint, zPoint);
				currentBlock.setType(Material.BEDROCK);
			}

			// loop y

			for (int zPoint = z1 + 1; zPoint < z2; zPoint++) {
				int xPoint = x1;
				Block currentBlock = world.getBlockAt(xPoint, yPoint, zPoint);
				currentBlock.setType(Material.BEDROCK);
				xPoint = x2;
				currentBlock = world.getBlockAt(xPoint, yPoint, zPoint);
				currentBlock.setType(Material.BEDROCK);

			}

		}
		logger.info("wall loop complete");
	}

	public static McRoyale getInst() {
		return instance;
	}

	public static void sendDebug(String msg) {
		if (debugActive)
			logger.info(msg);
	}

	public static ScoreboardManager getScoreManager() {
		return scoreManager;
	}

	public void ScoreboardSetup() {
		// set scoreboard, retrieve stats from database
		scoreManager = Bukkit.getScoreboardManager();
		board = scoreManager.getNewScoreboard();
		killStat = board.registerNewObjective("kills", "dummy");
		killStat.setDisplayName(ChatColor.GREEN + "Kills");
		winStat = board.registerNewObjective("wins", "dummy");
		winStat.setDisplayName(ChatColor.GOLD + "Wins");
		winStat.setDisplaySlot(DisplaySlot.SIDEBAR);
		deathStat = board.registerNewObjective("deaths", "dummy");
		deathStat.setDisplayName(ChatColor.DARK_RED + "Deaths");
		royaleHP = board.registerNewObjective("health", "health");
		royaleHP.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		oreStat = board.registerNewObjective("ore", "dummy");
		oreStat.setDisplayName(ChatColor.RED + "Ores Mined");
	}

	public void SetupPlayerScore(Player player) {
		player.setScoreboard(board);
		int newscore;

		Score pkills = killStat.getScore(player.getName());
		newscore = getStat(player, "kills");
		logger.info("saving kills.." + Integer.toString(newscore));
		pkills.setScore(newscore);

		Score pwins = winStat.getScore(player.getName());
		newscore = getStat(player, "wins");
		logger.info("saving wins.." + Integer.toString(newscore));
		pwins.setScore(newscore);

		Score pdeaths = deathStat.getScore(player.getName());
		newscore = getStat(player, "deaths");
		logger.info("saving deaths.." + Integer.toString(newscore));
		pdeaths.setScore(newscore);
		
		oreStat.getScore(player.getName()).setScore(0);

	}

	public static void changeKills(Player player, int change) {
		Score pkills = killStat.getScore(player.getName());
		int currentscore = pkills.getScore();
		int newscore = currentscore + change;
		pkills.setScore(newscore);
		saveStat(player.getName(), "kills", newscore);
	}

	public static void changeWins(Player player, int change) {
		Score pwins = winStat.getScore(player.getName());
		int currentscore = pwins.getScore();
		int newscore = currentscore + change;
		pwins.setScore(newscore);
		saveStat(player.getName(), "wins", newscore);
	}

	public static void changeDeaths(Player player, int change) {
		Score pdeaths = deathStat.getScore(player.getName());
		int currentscore = pdeaths.getScore();
		int newscore = currentscore + change;
		pdeaths.setScore(newscore);
		saveStat(player.getName(), "deaths", newscore);
	}

	private void setupDatabase() {
		try {
			getDatabase().find(McRoyaleStat.class).findRowCount();
		} catch (PersistenceException ex) {
			installDDL();
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(McRoyaleStat.class);
		return list;
	}

	public static void saveStat(String playerName, String stat, int value) {
		McRoyaleStat mcRoyaleStat = instance.getDatabase().find(McRoyaleStat.class).where().ieq("stat", stat)
				.ieq("playerName", playerName).findUnique();
		logger.info("saving a stat " + stat);
		if (mcRoyaleStat == null) {
			mcRoyaleStat = new McRoyaleStat();
			mcRoyaleStat.setPlayerName(playerName);
			mcRoyaleStat.setStat(stat);

		}
		mcRoyaleStat.setValue(value);

		instance.getDatabase().save(mcRoyaleStat);

	}

	public int getStat(Player player, String stat) {
		McRoyaleStat mcRoyaleStat = instance.getDatabase().find(McRoyaleStat.class).where().ieq("stat", stat)
				.ieq("playerName", player.getName()).findUnique();
		if (mcRoyaleStat == null)
			return 0;
		else
			return mcRoyaleStat.getValue();
	}

	public static Location setPlayerDown(Player p) {
		int oldX = p.getLocation().getBlockX();
		int oldZ = p.getLocation().getBlockZ();
		int newY = p.getWorld().getHighestBlockAt(oldX, oldZ).getY();
		Location newLocation = new Location(p.getWorld(), oldX, newY, oldZ);
		p.teleport(newLocation);
		return newLocation;
	}
	public static World getRoyaleWorld() {
		World world = Bukkit.getWorld("royale");
		if(world == null){
		WorldCreator creator = new WorldCreator("royale");
		creator.environment(World.Environment.NORMAL);
		creator.generateStructures(true);
		world = creator.createWorld();
		}
		return world;
	}
}
