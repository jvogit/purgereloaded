package com.gmail.mcraftworldmc.thepurge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class PurgeGame {
	private Main plugin;
	private GameState gameState = GameState.LOBBY;
	World world;
	private List<String> syndicate = new ArrayList<>();
	private List<String> civillians = new ArrayList<>();
	private Player target;
	private List<String> spectators = new ArrayList<>();
	private HashMap<String, Integer> votes = new HashMap<>();
	public PurgeGame(Main instance){
		plugin = instance;
	}
	public void updateGameState(GameState state){
		this.gameState = state;
	}
	public GameState getGameState(){
		return this.gameState;
	}
	public void setMap(World w){
		this.world = w;
	}
	public void startGame(World w){
		this.world = w;
		Bukkit.broadcastMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', "&4&lThis is the emergency broadcasting system, annoucing the commencement of the annual Purge. At the siren, all emergency services will be suspended for a 12-hour period. Your government thanks you for your participation."));
		this.divideIntoTeams();
		this.teleportPlayers();
		this.gameState = GameState.GAME;
		world.setFullTime(12000L);
		plugin.timer.start();
	}
	public int getSyndicateCount(){
		return this.syndicate.size();
	}
	public List<String> getSyndicateList(){
		return this.syndicate;
	}
	public Player getTarget(){
		return target;
	}
	public int getCivillianCount(){
		return this.civillians.size();
	}
	public List<String> getCivillianList(){
		return this.civillians;
	}
	public List<String> getSpectatorList(){
		return this.spectators;
	}
	public void endGame(){
		String winningTeamName;
		if(syndicate.size() == 0){
			Bukkit.broadcastMessage(plugin.prefix + ChatColor.GREEN + "The targets & civilians have won!");
			winningTeamName = "Target";
		}else if(target == null){
			Bukkit.broadcastMessage(plugin.prefix + ChatColor.RED + "The syndicates have won!");
			winningTeamName = "Syndicate";
		}else{
			Bukkit.broadcastMessage(plugin.prefix + ChatColor.GREEN + "The targets & civilians have won!");
			winningTeamName = "Target";
		}
		plugin.timer.setTime(11);
		for(Player p : Bukkit.getOnlinePlayers()){
			this.clearOfLists(p);
			this.spectators.add(p.getName());
			p.getInventory().clear();
			plugin.board.setAfterGameBoard(winningTeamName, p);
			p.setAllowFlight(true);
			p.setFlying(true);
			p.teleport(p.getLocation().add(0, 2, 0));
			p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 20, 20);
		}
		plugin.timer.stop();
		this.gameState = GameState.AFTER_GAME;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				for(Player p : Bukkit.getOnlinePlayers())
					p.playSound(p.getLocation(), Sound.FIREWORK_BLAST2, 20, 20);
			}
		}, 0L, 40L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			int s = 20;
			public void run(){
				if(s > 0){
					s--;
				}
				if(s <= 10 && s > 0){
					Bukkit.broadcastMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', "&c&lThe server will restart in: " + s));
				}
				if(s == 0){
					for(Player p : Bukkit.getOnlinePlayers()){
						p.kickPlayer(ChatColor.RED + "Thanks for participating in the annual Purge!");
					}
					Bukkit.shutdown();
				}
			}
		}, 0l, 20l);
		this.gameState = GameState.RESTARTING;
	}
	public void vote(int amount, String worldName){
		votes.put(worldName, votes.get(worldName) + amount);
	}
	public void siren(){
		for(Player p : Bukkit.getOnlinePlayers()){
			p.playSound(p.getLocation(), Sound.DONKEY_ANGRY, 20L, 20L);
		}
	}
	public void clearOfLists(Player p){
		if(this.syndicate.contains(p.getName())){
			this.syndicate.remove(p.getName());
		}
		if(this.civillians.contains(p.getName())){
			this.civillians.remove(p.getName());
		}else if(this.syndicate.contains(p.getName())){
			this.syndicate.remove(p.getName());
		}
		if(target == null) return;
		if(target.getName().equalsIgnoreCase(p.getName())){
			target = null;
		}
	}
	public void setNewTarget(Player p){
		if(target != null){
			return;
		}
		this.clearOfLists(p);
		this.target = p;
	}
	private void divideIntoTeams(){
		Player[] players = Bukkit.getOnlinePlayers();
		Collections.shuffle(Arrays.asList(players));
		if(players.length > 5 && players.length < 10){
			syndicate.add(players[1].getName());
			players[1].sendMessage(plugin.prefix + ChatColor.RED + "You have been put into the syndicate team! You are hunting for: " + ChatColor.DARK_RED + players[2].getName());
			syndicate.add(players[0].getName());
			players[0].sendMessage(plugin.prefix + ChatColor.RED + "You have been put into the syndicate team! You are hunting for: " + ChatColor.DARK_RED + players[2].getName());
			target = players[2];
			players[2].sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', "&c&lYou have been chosen as the target! Look for shelter immediately!"));
			for(Player p : players){
				if(!syndicate.contains(p.getName()) && !target.getName().equalsIgnoreCase(p.getName())){
					civillians.add(p.getName());
					p.sendMessage(plugin.prefix + "You are a civillian! Protect the target: " + ChatColor.GREEN + players[2].getName() + ". You may assist or kill other civillians.");
				}
			}
			return;
		}else if(players.length >= 10){
			for(int i = 0; i < 6; i++){
				this.putIntoSyndicate(players[i]);
			}
			target = players[7];
			players[7].sendMessage(plugin.prefix + ChatColor.translateAlternateColorCodes('&', "&c&lYou have been chosen as the target! Look for shelter immediately!"));
			for(Player p : players){
				if(!syndicate.contains(p.getName()) && !target.getName().equalsIgnoreCase(p.getName())){
					civillians.add(p.getName());
					p.sendMessage(plugin.prefix + "You are a civillian! Protect the target: " + ChatColor.GREEN + players[5].getName() + ". You may assist or kill other civillians.");
				}
			}
			return;
		}else if(players.length == 1){
			target = players[0];
			players[0].sendMessage(plugin.prefix + ChatColor.RED + "You have been put into the target team!");
		}else if(players.length == 3){
			this.putIntoSyndicate(players[0]);
			this.putIntoCivillian(players[1]);
			target = players[2];
			players[2].sendMessage(plugin.prefix + ChatColor.RED + "You are the target!");
		}else if(players.length == 2){
			this.putIntoSyndicate(players[0]);
			target = players[1];
			players[1].sendMessage(plugin.prefix + ChatColor.RED + "You are the target!");
		}else if(players.length == 4){
			this.putIntoSyndicate(players[0]);
			target = players[1];
			players[1].sendMessage(plugin.prefix + ChatColor.RED + "You are a target!");
			for(Player p : players){
				if(!syndicate.contains(p.getName()) && !target.getName().equalsIgnoreCase(p.getName()))
					this.putIntoCivillian(p);
			}
		}else if(players.length == 5){
			this.putIntoSyndicate(players[0]);
			target = players[1];
			players[1].sendMessage(plugin.prefix + ChatColor.AQUA + "You are a target!");
			for(Player p : players){
				if(!syndicate.contains(p.getName()) && !target.getName().equalsIgnoreCase(p.getName()))
					this.putIntoCivillian(p);
			}
		}
	}
	private void teleportPlayers(){
		for(String s : syndicate){
			Player p = Bukkit.getPlayerExact(s);
			String[] split = plugin.maps.getMapsInfoConfig().getString(world.getName() + ".syndicatespawnpoint").split(",");
			p.teleport(new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
			PlayerInventory pi = p.getInventory();
			ItemStack lc = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta lm = (LeatherArmorMeta) lc.getItemMeta();
			lm.setColor(Color.RED);
			lc.setItemMeta(lm);
			pi.setChestplate(lc);
			for(String itemName : plugin.itemConfig.getItemsConfig().getStringList("syndicateItems.items")){
				String[] split2 = itemName.split(",");
				pi.addItem(new ItemStack(Material.getMaterial(split2[0].toUpperCase()), Integer.parseInt(split2[1])));
			}
			for(ItemStack i : pi.getContents()){
				if(i == null){
					continue;
				}
				if(i.getType().equals(Material.EGG)){
					ItemMeta meta = i.getItemMeta();
					meta.setDisplayName(ChatColor.BOLD + "Grenade");
					i.setItemMeta(meta);
				}else if(i.getType().equals(Material.SNOW_BALL)){
					ItemMeta meta = i.getItemMeta();
					meta.setDisplayName(ChatColor.ITALIC + "Flashbang");
					i.setItemMeta(meta);
				}
			}
			plugin.book.giveSyndicateBook(p);
		}
		ItemStack tc = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta tm = (LeatherArmorMeta) tc.getItemMeta();
		tm.setColor(Color.AQUA);
		tc.setItemMeta(tm);
		plugin.book.giveTargetBook(target);
		target.getInventory().setChestplate(tc);
		String[] splitTarget = plugin.maps.getMapsInfoConfig().getString(world.getName() + ".targetspawn").split(",");
		target.teleport(new Location(world, Double.parseDouble(splitTarget[0]), Double.parseDouble(splitTarget[1]), Double.parseDouble(splitTarget[2])));
		int counter = 0;
		for(String s : civillians){
			ItemStack lc = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta lm = (LeatherArmorMeta) lc.getItemMeta();
			lm.setColor(Color.GREEN);
			lc.setItemMeta(lm);
			plugin.book.giveCivilianBook(Bukkit.getPlayerExact(s));
			Bukkit.getPlayerExact(s).getInventory().setChestplate(lc);
			String[] split = plugin.maps.getMapsInfoConfig().getStringList(world.getName() + ".housespawn").get(counter).split(",");
			Bukkit.getPlayerExact(s).teleport(new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
			counter++;
		}
	}
	private void putIntoSyndicate(Player p){
		syndicate.add(p.getName());
		p.sendMessage(plugin.prefix + ChatColor.RED + "You have been put into the syndicate team!");
	}
	private void putIntoCivillian(Player p){
		civillians.add(p.getName());
		p.sendMessage(plugin.prefix + ChatColor.GREEN + "You have been put into the civilians team! Protect the target!");
	}
}
