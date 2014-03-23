package com.gmail.mcraftworldmc.thepurge.timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.mcraftworldmc.thepurge.Main;

public class GameTimer extends BukkitRunnable{
	private Main plugin;
	public GameTimer(Main instance){
		plugin = instance;
	}
	private int timeInSeconds;
	private int taskID;
	@SuppressWarnings("unused")
	private int extraTime;
	private boolean extraTimeActive = false;
	public void start(){
		stop();
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
		resetTime();
		if(timeInSeconds > 600){
			extraTime = timeInSeconds - 600;
			extraTimeActive = true;
		}
	}
	public void run(){
		if(timeInSeconds > 0){
			timeInSeconds--;
			int minutes = timeInSeconds / 60;
			int seconds = timeInSeconds % 60;
			String str = String.format("%d:%02d", minutes, seconds);
			if(plugin.getConfig().getString("purge.devTool").equalsIgnoreCase("true")){
				plugin.getLogger().info(timeInSeconds + "");
			}
			for(Player p : Bukkit.getOnlinePlayers()){
				try{
					plugin.board.setGameBoard(p, str, plugin.game.getSyndicateCount(), plugin.game.getCivillianCount(), plugin.game.getSyndicateList(), plugin.game.getCivillianList(), plugin.game.getSpectatorList(), plugin.game.getTarget());
				}catch(NullPointerException ex){
					if(plugin.getConfig().getBoolean("purge.devTool") == true){
						plugin.getLogger().info("Suppressed NullPointerException: " + ex.getMessage());
					}
				}
			}
		}
		if(extraTimeActive){
			if(timeInSeconds > (60 * 5) && plugin.game.world.getTime() >= 18000L){
				plugin.game.world.setTime(18000L);
			}
		}
		if(timeInSeconds % 60 == 0 && timeInSeconds > 0){
			broadcastTime(true);
			return;
		}
		if(timeInSeconds % 15 == 0 && timeInSeconds > 0){
			broadcastTime(false);
		}
		if(timeInSeconds <= 30 && timeInSeconds > 20){
			broadcastTime(true);
		}
		if(timeInSeconds <= 10 && timeInSeconds > 0){
			broadcastTime(true);
		}
		if(timeInSeconds == 0){
			this.plugin.game.endGame();
		}
	}
	private void broadcastTime(boolean b){
		if(!b == true){
			return;
		}
		if(timeInSeconds % 60 == 0){
			Bukkit.broadcastMessage(plugin.prefix + timeInSeconds / 60 + " minutes remain for the annual Purge!");
			return;
		}
		if(timeInSeconds % 15 == 0){
			Bukkit.broadcastMessage(plugin.prefix + timeInSeconds + " seconds remain for the annual Purge!");
			return;
		}
		if(timeInSeconds <= 10 && timeInSeconds > 00){
			Bukkit.broadcastMessage(plugin.prefix + timeInSeconds + " seconds remain until the annual Purge ends.");
		}
	}
	private void resetTime(){
		timeInSeconds = plugin.getConfig().getInt("purge.timeInGame");
	}
	public void setTime(int i){
		this.timeInSeconds = i;
	}
	public void stop(){
		Bukkit.getScheduler().cancelTask(this.taskID);
	}
}
