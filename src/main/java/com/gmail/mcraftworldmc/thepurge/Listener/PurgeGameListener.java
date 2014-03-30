package com.gmail.mcraftworldmc.thepurge.Listener;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import com.gmail.mcraftworldmc.thepurge.Main;
import com.gmail.mcraftworldmc.thepurge.utilities.FireworkEffects;
import com.gmail.mcraftworldmc.thepurge.utilities.GameState;

public class PurgeGameListener implements Listener{
	private Main plugin;
	public PurgeGameListener(Main instance){
		this.plugin = instance;
	}
	private FireworkEffects fEffect = new FireworkEffects();
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if(plugin.getConfig().getString("purge.setupmode").equalsIgnoreCase("true")){
			return;
		}
		if(e.getEntity() instanceof Player && plugin.game.getGameState().equals(GameState.LOBBY)){
			e.setCancelled(true);
			return;
		}else if(e.getEntity() instanceof Player && plugin.game.getGameState().equals(GameState.AFTER_GAME)){
			e.setCancelled(true);
			return;
		}
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			Player attacker = (Player) e.getDamager();
			Player damaged = (Player) e.getEntity();
			if(plugin.game.getSpectatorList().contains(attacker.getName())){
				e.setCancelled(true);
				return;
			}
			if(plugin.game.getCivillianList().contains(attacker.getName()) && plugin.game.getTarget().getName().equalsIgnoreCase(damaged.getName())){
				e.setCancelled(true);
				attacker.sendMessage(plugin.prefix + ChatColor.RED + "You cannot damage the target! Protect the target!");
				return;
			}else if(plugin.game.getSyndicateList().contains(attacker.getName()) && plugin.game.getSyndicateList().contains(damaged.getName())){
				e.setCancelled(true);
				return;
			}else if(plugin.game.getSpectatorList().contains(attacker.getName())){
				e.setCancelled(true);
				return;
			}else if(plugin.game.getTarget().equals(attacker) && plugin.game.getCivillianList().contains(damaged.getName())){
				e.setCancelled(true);
				return;
			}else if(plugin.game.getSpectatorList().contains(damaged.getName())){
				e.setCancelled(true);
				return;
			}
		}
	}
	@EventHandler
	public void onDeathLightning(PlayerDeathEvent e){
		e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
		e.getEntity().getWorld().setThunderDuration(5 * 20);
		Firework firework = e.getEntity().getWorld().spawn(e.getEntity().getLocation(), Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(fEffect.deathEffect());
		firework.setFireworkMeta(meta);
		if(plugin.game.getSyndicateList().contains(e.getEntity().getName()))
			e.setDeathMessage(ChatColor.RED + e.getEntity().getName() + ChatColor.WHITE + " has died");
		else if(plugin.game.getTarget().equals(e.getEntity()))
			e.setDeathMessage(ChatColor.AQUA + e.getEntity().getName() + ChatColor.WHITE + " has died");
		else if(plugin.game.getCivillianList().contains(e.getEntity().getName()))
			e.setDeathMessage(ChatColor.GREEN + e.getEntity().getName() + ChatColor.WHITE + " has died");
		else
			e.setDeathMessage(ChatColor.GRAY + e.getEntity().getName() + ChatColor.WHITE + " has died");
	}
	@EventHandler
	public void onDeathGame(PlayerDeathEvent event){
		Player p = event.getEntity();
		if(plugin.getConfig().getString("purge.setupmode").equalsIgnoreCase("true")){
			return;
		}
		if(!plugin.game.getGameState().equals(GameState.GAME)){
			return;
		}
		if(plugin.game.getTarget().getName().equalsIgnoreCase(p.getName())){
			plugin.game.clearOfLists(p);
			plugin.game.endGame();
			return;
		}
		if(plugin.game.getSyndicateList().contains(p.getName()) && plugin.game.getSyndicateCount() - 1 <= 0){
			plugin.game.endGame();
			p.setAllowFlight(true);
			p.setFlying(true);
			return;
		}
		plugin.game.clearOfLists(p);
		plugin.game.getSpectatorList().add(p.getUniqueId());
		for(Player pl : Bukkit.getOnlinePlayers()){
			pl.hidePlayer(p);
		}
	}
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if(!plugin.game.getSpectatorList().contains(e.getPlayer().getName()) && !plugin.game.getGameState().equals(GameState.AFTER_GAME)) return;
		e.getPlayer().setAllowFlight(true);
		Player[] p = Bukkit.getOnlinePlayers();
		e.getPlayer().teleport(p[1]);
		e.getPlayer().sendMessage(plugin.prefix + ChatColor.GRAY + "You have been teleported to the target, spectator!");
		e.getPlayer().setFlying(true);
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(!plugin.game.getGameState().equals(GameState.GAME)){
			return;
		}
		plugin.game.clearOfLists(e.getPlayer());
		plugin.game.getSpectatorList().add(e.getPlayer().getUniqueId());
		for(Player p : Bukkit.getOnlinePlayers()){
			p.hidePlayer(e.getPlayer());
		}
		e.getPlayer().sendMessage(plugin.prefix + ChatColor.GRAY + "You joined as a spectator!");
		e.getPlayer().setAllowFlight(true);
		e.getPlayer().teleport(plugin.game.getTarget().getLocation().add(0, 3, 0));
		e.getPlayer().setFlying(true);
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(plugin.game.getGameState().equals(GameState.GAME)){
			if(plugin.game.getSpectatorList().contains(e.getPlayer().getName())){
				plugin.game.getSpectatorList().remove(e.getPlayer().getName());
			}
			if(plugin.game.getTarget().getName().equalsIgnoreCase(e.getPlayer().getName())){
				Player[] players = Bukkit.getOnlinePlayers();
				Collections.shuffle(Arrays.asList(players));
				plugin.game.clearOfLists(players[0]);
				plugin.game.setNewTarget(e.getPlayer());
				players[0].sendMessage(plugin.prefix + ChatColor.GREEN + "You have been selected as the new target!");
			}else if(plugin.game.getSyndicateList().contains(e.getPlayer().getName()) && plugin.game.getSyndicateList().size() - 1 <= 0){
				plugin.game.endGame();
			}else{
				plugin.game.clearOfLists(e.getPlayer());
			}
		}
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(!this.plugin.game.getGameState().equals(GameState.GAME)) return;
		if(!this.plugin.game.getSpectatorList().contains(p.getName())) return;
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			Player[] players = Bukkit.getOnlinePlayers();
			Collections.shuffle(Arrays.asList(players));
			Random rGen = new Random();
			int i = rGen.nextInt(players.length);
			while(this.plugin.game.getSpectatorList().contains(players[i].getName())){
				i = rGen.nextInt(players.length);
			}
			p.teleport(players[i].getLocation());
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e){
		/* if(!plugin.game.getGameState().equals(GameState.GAME)){
			e.setCancelled(true);
			return;
		}
		if(plugin.game.getSpectatorList().contains(e.getPlayer().getName())){
			e.setCancelled(true);
			return;
		}
		for(String s : plugin.itemConfig.getItemsConfig().getStringList("blocks.whitelist")){
			Material m = Material.getMaterial(s.toUpperCase());
			if(e.getBlock().getType().equals(m))
				return;
		} */
		e.setCancelled(true);
	}
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		if(plugin.game.getSpectatorList().contains(e.getPlayer().getName())) e.setCancelled(true);
		if(plugin.game.getGameState().equals(GameState.AFTER_GAME)) e.setCancelled(true);
	}
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		if(plugin.game.getGameState().equals(GameState.GAME) && plugin.game.getSpectatorList().contains(e.getPlayer().getName())) e.setCancelled(true);
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		if(plugin.game.getGameState().equals(GameState.AFTER_GAME) || plugin.game.getSpectatorList().contains(e.getWhoClicked().getName())){
			e.setCancelled(true);
			e.getWhoClicked().closeInventory();
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e){
		if(plugin.game.getSpectatorList().contains(e.getPlayer().getName()) && !plugin.game.getGameState().equals(GameState.AFTER_GAME)){
			e.setCancelled(true);
			for(Player p : Bukkit.getOnlinePlayers()){
				if(plugin.game.getSpectatorList().contains(p.getName())){
					p.sendMessage(ChatColor.YELLOW + "SPEC " + ChatColor.WHITE + e.getPlayer().getName() + " " + e.getMessage());
				}
			}
			return;
		}
		e.setCancelled(true);
		if(!plugin.game.getGameState().equals(GameState.GAME)){
			return;
		}
		if(plugin.game.getSyndicateList().contains(e.getPlayer().getName())){
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + e.getPlayer().getName() + "&8: &7" + e.getMessage()));
		}else if(plugin.game.getCivillianList().contains(e.getPlayer().getName())){
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a" + e.getPlayer().getName() + "&8: &7" + e.getMessage()));
		}else if(plugin.game.getTarget().equals(e.getPlayer())){
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b" + e.getPlayer().getName() + "&8: &7" + e.getMessage()));
		}
	}
	@EventHandler
	public void onHatch(CreatureSpawnEvent e){
		if(e.getSpawnReason().equals(SpawnReason.EGG)){
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPHit(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(plugin.game.getSpectatorList().contains(p.getName()))
				e.setCancelled(true);
		}
	}
}
