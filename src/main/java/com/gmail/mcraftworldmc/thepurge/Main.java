package com.gmail.mcraftworldmc.thepurge;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main main class file
 * 
 * @author MineOCraftMC
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class Main extends JavaPlugin{
	Maps maps = new Maps(this);
	Book book = new Book(this);
	ItemsConfig itemConfig = new ItemsConfig(this);
	PreGameTimer preTimer = new PreGameTimer(this);
	GameTimer timer = new GameTimer(this);
	PurgeGame game = new PurgeGame(this);
	Board board = new Board(this);
	VoteMechanic voteMech = new VoteMechanic(this);
	String prefix;
	HashMap<String, Integer> inChestSetup = new HashMap<>();
	HashMap<String, ArrayList<String>> chestLocation = new HashMap<>();
	public void onEnable(){
		saveDefaultConfig();
		book.saveDefaultBookConfig();
		maps.saveDefaultMapsConfig();
		maps.saveDefaultMapsInfoConfig();
		itemConfig.saveDefaultItemsConfig();
		getLogger().info("Registering events...");
		Bukkit.getPluginManager().registerEvents(new PurgeMainListener(this), this);
		/* Bukkit.getPluginManager().registerEvents(new ChestSetup(this), this); */
		Bukkit.getPluginManager().registerEvents(new PurgeGameListener(this), this);
		Bukkit.getPluginManager().registerEvents(new ChestListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PowerUpsListener(this), this);
		getCommand("purge").setExecutor(new PurgeCommand(this));
		getCommand("vote").setExecutor(new VoteCommand(this));
	    if(getConfig().getString("purge.setupmode").equalsIgnoreCase("true")){
			getLogger().info("Set up mode has been set to true! The game will not be automated for set up!");
		}else{
			if(Bukkit.getOnlinePlayers().length >= 1){
				this.preTimer.start();
			}
		}
		this.prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("purge.prefix"));
	}
}
