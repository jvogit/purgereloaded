package com.gmail.mcraftworldmc.thepurge.mechanics;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.gmail.mcraftworldmc.thepurge.Main;

/**
 * Board Class for setting scoreboards
 * 
 * @author MineOCraftMC
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class Board {
	private Main plugin;
	public Board(Main instance){
		plugin = instance;
	}
	/*
	 * Method to show the lobby scoreboard
	 * 
	 * @param p Player to be showed the scoreboard
	 * @param timeLeft formatted String with the time
	 * @param playersMin Minimum player integer
	 * @param playersCount The current amount of players integer
	 * 
	 */
	public void setPreGameBoard(Player p, String timeLeft, int playersMin, int playersCount){
		ScoreboardManager sm = plugin.getServer().getScoreboardManager();
		Scoreboard board = sm.getNewScoreboard();
		Objective preGame = board.registerNewObjective("Purge", "dummy");
		preGame.setDisplaySlot(DisplaySlot.SIDEBAR);
		preGame.setDisplayName(ChatColor.RED + "Purge Lobby " + ChatColor.GREEN +  "(" + timeLeft + ")");
		Score playerMin = preGame.getScore(Bukkit.getOfflinePlayer("Players (Min):"));
		playerMin.setScore(playersMin);
		Score players = preGame.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Players:"));
		players.setScore(playersCount);
		p.setScoreboard(board);
	}
	/*
	 * Method to show the game scoreboard
	 * 
	 * @param p Player to be showed the scoreboard
	 * @param timeLeft Formatted String of the time that is left
	 * @param syndicateCount Amount of players that are syndicate integer
	 * @param civillianCount Amount of players that are civillian integer
	 */
	public void setGameBoard(Player p, String timeLeft, int syndicateCount, int civillianCount, List<String> syndicateList, List<String> civillianList, List<String> spectator, Player target){
		ScoreboardManager sm = plugin.getServer().getScoreboardManager();
		Scoreboard board = sm.getNewScoreboard();
		Team syn = board.registerNewTeam("Syndicate");
		Team civ = board.registerNewTeam("Civilian");
		Team tar = board.registerNewTeam("Target");
		Team spe = board.registerNewTeam("Spectator");
		syn.setPrefix(ChatColor.RED + "");
		civ.setPrefix(ChatColor.GREEN + "");
		tar.setPrefix(ChatColor.AQUA + "");
		Objective game = board.registerNewObjective("Purge", "dummy");
		game.setDisplaySlot(DisplaySlot.SIDEBAR);
		game.setDisplayName(ChatColor.RED + "The Purge " + ChatColor.GREEN + "(" + timeLeft + ")");
		Score syndicate = game.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "Syndicate(s):"));
		syndicate.setScore(syndicateCount);
		Score civillian = game.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Civilian(s):"));
		civillian.setScore(civillianCount);
		Score spectators = game.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "Spectator(s)"));
		spectators.setScore(spectator.size());
		p.setScoreboard(board);
		if(syndicateList != null){
			for(String s : syndicateList){
				syn.addPlayer(Bukkit.getOfflinePlayer(s));
			}
		}
		if(civillianList != null){
			for(String s : civillianList){
				civ.addPlayer(Bukkit.getOfflinePlayer(s));
			}
		}
		if(spectator != null){
			for(String s  : spectator){
				spe.addPlayer(Bukkit.getOfflinePlayer(s));
			}
		}
		tar.addPlayer(Bukkit.getOfflinePlayer(target.getName()));
	}
	public void setAfterGameBoard(String winningTeamName, Player p){
		ScoreboardManager sm = plugin.getServer().getScoreboardManager();
		Scoreboard board = sm.getNewScoreboard();
		Objective afterGame = board.registerNewObjective("agame", "dummy");
		afterGame.setDisplayName(ChatColor.GREEN + "After Game");
		afterGame.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score c1 = afterGame.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Plugin by:"));
		c1.setScore(-1);
		Score c2 = afterGame.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + "MineOCraftMC"));
		c2.setScore(-2);
		Score c3 = afterGame.getScore(Bukkit.getOfflinePlayer(ChatColor.LIGHT_PURPLE + "For:"));
		c3.setScore(-3);
		Score c4 = afterGame.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "StylishZebra"));
		c4.setScore(-4);
		Score c5 = afterGame.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + "&"));
		c5.setScore(-5);
		Score c6 = afterGame.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "PurgeMC"));
		c6.setScore(-6);
		Score w1 = afterGame.getScore(Bukkit.getOfflinePlayer(ChatColor.translateAlternateColorCodes('&', "&b&lWINNER:")));
		w1.setScore(2);
		Score w2 = afterGame.getScore(Bukkit.getOfflinePlayer(ChatColor.translateAlternateColorCodes('&', "&c&l" + winningTeamName)));
		w2.setScore(1);
		p.setScoreboard(board);
	}
}
