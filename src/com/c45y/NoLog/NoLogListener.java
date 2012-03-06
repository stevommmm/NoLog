package com.c45y.NoLog;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NoLogListener implements Listener{
	public final NoLog plugin;

	public NoLogListener(NoLog instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event){
		if (event instanceof PlayerDeathEvent) {
			Player player = (Player) event.getEntity();
			if (plugin.PlayerLog.containsKey(player)) {
				plugin.PlayerLog.remove(player);
			}
		}
	}
	
	//InventoryOpenEvent
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event){
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
		if (!(damageEvent.getEntity() instanceof Player)) {
			return;
		}
		if (damageEvent.getDamager() instanceof Player) {
			Player attacker = (Player) damageEvent.getDamager();
			// Are we being attacked by an invulnerable player? 200 standard 10 seconds?
			if (damageEvent.getDamager().getTicksLived() < 60) {
				for(Player serv_players: plugin.getServer().getOnlinePlayers()) {
					if(serv_players.hasPermission("NoLog.view")) {
						serv_players.sendMessage(ChatColor.DARK_RED + attacker.getDisplayName() + " attacked while invulnerable!");
					}
				}
				plugin.log.info("NoLog: " + attacker.getDisplayName() + " attacked while invulnerable!");
			}
			Player player = (Player) event.getEntity();
			NoLogObject nlo = new NoLogObject((Player)damageEvent.getDamager(),System.currentTimeMillis(),player.getLocation());
			plugin.PlayerLog.put(player, nlo);
		}
		else if (damageEvent.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) damageEvent.getDamager();
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) event.getEntity();
				Player shooter = (Player) arrow.getShooter();
				if (player != shooter) {
					NoLogObject nlo = new NoLogObject(shooter,System.currentTimeMillis(),player.getLocation());
					plugin.PlayerLog.put(player, nlo);
				}
			}
		}
		/*else if (damageEvent.getDamager() instanceof Wolf) {
			Wolf wolf = (Wolf) damageEvent.getEntity();
			if (wolf.isTamed() && wolf.getOwner() != null) {
				Player tamer = (Player) wolf.getOwner();
				Player player = (Player) event.getEntity();
				if (tamer == player) {
					plugin.PlayerLog.put(player, System.currentTimeMillis());
				}
			}
		}*/
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.isDead()) { return; }
		if (plugin.PlayerLog.containsKey(player)) {
			if ( plugin.PlayerLog.get(player).getTimestamp() > System.currentTimeMillis() - 10000) {
				for(Player serv_players: plugin.getServer().getOnlinePlayers()) {
					if(serv_players.hasPermission("NoLog.view")) {
						serv_players.sendMessage(ChatColor.DARK_GRAY + "NL " + genNoLogMessage(player));
					}
				}
				plugin.log.info("NoLog: " + genNoLogMessage(player));
			}
			plugin.PlayerLog.remove(player);
		}
	}
	
	// Generate the resulting string here to keep it all neat and easy to change.
	// Possibly add a config to turn off parts of the message. i.e. location
	public String genNoLogMessage(Player player) {
		NoLogObject nlo = plugin.PlayerLog.get(player);
		return "[" + player.getDisplayName() + "] < [" + nlo.getAttackerName() + "] seconds: " + ((System.currentTimeMillis() - nlo.getTimestamp()) /1000 ) + " ,distance: " + nlo.getDistance(player);
	}
}
