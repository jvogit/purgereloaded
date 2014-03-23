package com.gmail.mcraftworldmc.thepurge;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PowerUpsListener implements Listener {
	private Main plugin;

	public PowerUpsListener(Main instance) {
		this.plugin = instance;
	}
	FireworkEffects fEffect = new FireworkEffects();
	@EventHandler
	public void onSnowballHit(ProjectileHitEvent e) {
		if (!(e.getEntity() instanceof Snowball))
			return;
		if(!(e.getEntity().getShooter() instanceof Player))
			return;
		Snowball snowball = (Snowball) e.getEntity();
		for (Entity ent : snowball.getNearbyEntities(5, 5, 5)) {
			if (ent instanceof Player) {
				Player p = (Player) ent;
				if(plugin.game.getSpectatorList().contains(p.getName()))
					continue;
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 10));
				this.playFireworkEffect(p.getLocation(), fEffect.randomFireworkEffect());
			}
		}
		for(int i = 0; i <= 2; i++){
			this.playFireworkEffect(snowball.getLocation().add(0,1,i), fEffect.randomFireworkEffect());
			this.playFireworkEffect(snowball.getLocation().add(i,1,0), fEffect.randomFireworkEffect());
			this.playFireworkEffect(snowball.getLocation().subtract(i,0,0), fEffect.randomFireworkEffect());
			this.playFireworkEffect(snowball.getLocation().subtract(i,0,i), fEffect.randomFireworkEffect());
		}
	}
	public void playFireworkEffect(Location loc, FireworkEffect effect){
		final Firework firework = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(effect);
		firework.setFireworkMeta(meta);
		new BukkitRunnable(){
			public void run(){
				firework.detonate();
			}
		}.runTaskLater(plugin, 1L);
	}
	@EventHandler
	public void onEggHit(ProjectileHitEvent e){
		if(!(e.getEntity() instanceof Egg))
			return;
		if(!(e.getEntity().getShooter() instanceof Player))
			return;
		Egg egg = (Egg) e.getEntity();
		egg.getWorld().createExplosion(egg.getLocation().getX(), egg.getLocation().getY() + 1, egg.getLocation().getZ(), 2.0F, false, false);
		for(int i = 0; i <= 2; i++){
			this.playFireworkEffect(egg.getLocation().add(0,1,i), fEffect.randomFireworkEffect());
			this.playFireworkEffect(egg.getLocation().add(i,1,0), fEffect.randomFireworkEffect());
			this.playFireworkEffect(egg.getLocation().subtract(i,0,0), fEffect.randomFireworkEffect());
			this.playFireworkEffect(egg.getLocation().subtract(i,0,i), fEffect.randomFireworkEffect());
		}
		for(Entity ent : egg.getNearbyEntities(5, 5, 5)){
			if(ent instanceof Player){
				Player p = (Player) ent;
				this.playFireworkEffect(p.getLocation(), fEffect.deathEffect());
			}
		}
	}
}
