package com.mileses.mcroyale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
	public static Objective killStat;
	public static Scoreboard killBoard;
	public static Objective deathStat;
	public static Scoreboard deathBoard;
	public static Objective winStat;
	public static Scoreboard winBoard;
	public static boolean peaceTime;
	public static boolean roundActive;
	public HashMap<String, Boolean> playerList;
	public static EbeanServer database;
	public static BukkitTask statRunnable;

	@Override
	public void onEnable() {
		// TODO initializaiton logic
		instance = this;
		logger = getLogger();
		ScoreboardSetup();
		setupDatabase();
		McRoyaleDeathListener mrdl = new McRoyaleDeathListener(this);
		McRoyalePeaceListener mrpl = new McRoyalePeaceListener(this);
		getServer().getPluginManager().registerEvents(mrdl, this);
		getServer().getPluginManager().registerEvents(mrpl, this);
		for (Player x : Bukkit.getOnlinePlayers())
			playerList.put(x.getName(), true);
		playerList = new HashMap<String, Boolean>();
		roundActive = false;
	}

	@Override
	public void onDisable() {
		instance = null;
		// TODO disable logic
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
					} else
						sender.sendMessage("/royale help wall or /royale help round");
					return true;
				}

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

				// check for round command and for args
				if (args[0].equalsIgnoreCase("round") && args.length >= 3) {
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
							playerList.put(player.getName(), true);
							SetupPlayerScore(player);

						}
						
					}
					// start round
					statRunnable = new McRoyaleStatRunnable(scoreManager).runTaskTimer(this, 0, 800);
					McRoyaleRound.startRound(location, length, playerList, (Player) sender, peaceTimeArg);
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

	public static Logger getLogr() {
		return logger;
	}

	public static ScoreboardManager getScoreManager() {
		return scoreManager;
	}

	public void ScoreboardSetup() {
		// set scoreboard, retrieve stats from database
		scoreManager = Bukkit.getScoreboardManager();
		killBoard = scoreManager.getNewScoreboard();
		winBoard = scoreManager.getNewScoreboard();
		deathBoard = scoreManager.getNewScoreboard();
		killStat = killBoard.registerNewObjective("kills", "dummy");
		killStat.setDisplayName("Kills");
		killStat.setDisplaySlot(DisplaySlot.SIDEBAR);
		winStat = killBoard.registerNewObjective("wins", "dummy");
		winStat.setDisplayName("Wins");
		winStat.setDisplaySlot(DisplaySlot.SIDEBAR);
		deathStat = killBoard.registerNewObjective("deaths", "dummy");
		deathStat.setDisplayName("Deaths");
		deathStat.setDisplaySlot(DisplaySlot.SIDEBAR);
		Objective royaleHPk = killBoard.registerNewObjective("health", "health");
		Objective royaleHPw = winBoard.registerNewObjective("health", "health");
		Objective royaleHPd = deathBoard.registerNewObjective("health", "health");
		royaleHPk.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		royaleHPw.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		royaleHPd.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}

	public void SetupPlayerScore(Player player) {
		
		logger.info("Scoreboard switcher started");
		Score pkills = killStat.getScore(player.getName());
		int newscore = getStat(player, "kills");
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
}
