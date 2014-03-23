package com.gmail.mcraftworldmc.thepurge;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.mcraftworldmc.thepurge.Listener.ChestListener;
import com.gmail.mcraftworldmc.thepurge.Listener.PowerUpsListener;
import com.gmail.mcraftworldmc.thepurge.Listener.PurgeGameListener;
import com.gmail.mcraftworldmc.thepurge.Listener.PurgeMainListener;
import com.gmail.mcraftworldmc.thepurge.commands.PurgeCommand;
import com.gmail.mcraftworldmc.thepurge.commands.VoteCommand;
import com.gmail.mcraftworldmc.thepurge.config.ItemsConfig;
import com.gmail.mcraftworldmc.thepurge.config.Maps;
import com.gmail.mcraftworldmc.thepurge.mechanics.Board;
import com.gmail.mcraftworldmc.thepurge.mechanics.PurgeGame;
import com.gmail.mcraftworldmc.thepurge.mechanics.VoteMechanic;
import com.gmail.mcraftworldmc.thepurge.timers.GameTimer;
import com.gmail.mcraftworldmc.thepurge.timers.PreGameTimer;
import com.gmail.mcraftworldmc.thepurge.utilities.Book;

/**
 * Main main class file
 * 
 * @author MineOCraftMC
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class Main extends JavaPlugin{
	public Maps maps = new Maps(this);
	public Book book = new Book(this);
	public ItemsConfig itemConfig = new ItemsConfig(this);
	public PreGameTimer preTimer = new PreGameTimer(this);
	public GameTimer timer = new GameTimer(this);
	public PurgeGame game = new PurgeGame(this);
	public Board board = new Board(this);
	public VoteMechanic voteMech = new VoteMechanic(this);
	public String prefix;
	public HashMap<String, Integer> inChestSetup = new HashMap<>();
	public HashMap<String, ArrayList<String>> chestLocation = new HashMap<>();
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
