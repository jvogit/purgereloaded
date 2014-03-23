package com.gmail.mcraftworldmc.thepurge.timers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.mcraftworldmc.thepurge.Main;

public class PreGameTimer extends BukkitRunnable{
	private int taskID;
	private int lobbyTimeInSeconds;
	private Main plugin;
	public PreGameTimer(Main instance){
		plugin = instance;
	}
	public void start(){
		stop();
		this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0l, 20l);
		resetTime();
	}
	public void setTime(int i){
		this.lobbyTimeInSeconds = i;
	}
	public void run(){
		if(lobbyTimeInSeconds > 0){
			lobbyTimeInSeconds--;
			if(plugin.getConfig().getString("purge.devTool").equalsIgnoreCase("true")){
				plugin.getLogger().info(lobbyTimeInSeconds + "");
			}
			for(Player p : Bukkit.getOnlinePlayers()){
				int minutes = lobbyTimeInSeconds / 60;
				int seconds = lobbyTimeInSeconds % 60;
				String str = String.format("%d:%02d", minutes, seconds);
				plugin.board.setPreGameBoard(p, str, plugin.getConfig().getInt("purge.playerMin"), Bukkit.getOnlinePlayers().length);
				p.setLevel(lobbyTimeInSeconds);
			}
		}
		if(lobbyTimeInSeconds % 60 == 0 && lobbyTimeInSeconds > 0){
			broadcastTime(true);
			return;
		}
		if(lobbyTimeInSeconds % 15 == 0 && lobbyTimeInSeconds > 0){
			broadcastTime(false);
			Bukkit.broadcastMessage(plugin.prefix + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/vote " + ChatColor.WHITE + "to choose your favorite map!");
			for(String s : plugin.maps.getMapsConfig().getStringList("purgeWorlds")){
				String[] split = s.split(",");
				Bukkit.broadcastMessage(ChatColor.YELLOW + "" + Integer.parseInt(split[2]) + ". " + ChatColor.WHITE + split[1] + ChatColor.GRAY + " [" + ChatColor.GREEN + plugin.voteMech.getVotes(split[0]) + " Votes" + ChatColor.GRAY + "]");
			}
		}
		if(lobbyTimeInSeconds > 0 && lobbyTimeInSeconds <= 5){
			broadcastTime(true);
			for(Player p : Bukkit.getOnlinePlayers()){
				p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.A));;
			}
		}
		if(lobbyTimeInSeconds == 0){
			if(Bukkit.getOnlinePlayers().length >= plugin.getConfig().getInt("purge.playerMin")){
				this.plugin.game.siren();
				this.plugin.game.startGame(plugin.voteMech.getHighestVote());
				stop();
			}else{
				Bukkit.broadcastMessage(plugin.prefix + "Not enough players to start the game!");
				this.resetTime();
			}
		}
	}
	private void broadcastTime(boolean b){
		if(!b == true){
			return;
		}
		if(lobbyTimeInSeconds % 60 == 0 && lobbyTimeInSeconds > 0){
			Bukkit.broadcastMessage(plugin.prefix + lobbyTimeInSeconds / 60 + " minutes until the lobby ends.");
			return;
		}
		if(lobbyTimeInSeconds % 15 == 0 && lobbyTimeInSeconds > 0){
			Bukkit.broadcastMessage(plugin.prefix + lobbyTimeInSeconds + " seconds until the lobby ends.");
			return;
		}
		if(lobbyTimeInSeconds <= 5 && lobbyTimeInSeconds > 0){
			Bukkit.broadcastMessage(plugin.prefix + lobbyTimeInSeconds + " seconds until the lobby ends.");
			return;
		}
	}
	private void resetTime(){
		lobbyTimeInSeconds = plugin.getConfig().getInt("purge.timeInLobby");
	}
	public void stop(){
		Bukkit.getScheduler().cancelTask(this.taskID);
	}
}
