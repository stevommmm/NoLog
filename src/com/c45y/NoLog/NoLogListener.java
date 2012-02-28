package com.c45y.NoLog;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.Potion;

public class NoLogListener implements Listener{
	public NoLog plugin;

	public NoLogListener(NoLog instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event){
		if (event instanceof PlayerDeathEvent) {
			Player player = (Player) event.getEntity();
			plugin.PlayerLog.remove(player);
		}
	}

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
			Player player = (Player) event.getEntity();
			plugin.PlayerLog.put(player, System.currentTimeMillis());
		}
		if (damageEvent.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) event.getEntity();
				if (player.getUniqueId() != arrow.getShooter().getUniqueId()) {
					plugin.PlayerLog.put(player, System.currentTimeMillis());
				}
			}
		}
		if (damageEvent.getDamager() instanceof Tameable) {
			Tameable tameable = (Tameable) damageEvent.getEntity();
			if (tameable.getOwner() != null) {
				Player tamer = (Player) tameable.getOwner();
				Player player = (Player) event.getEntity();
				if (tamer.getUniqueId() == player.getUniqueId()) {
					plugin.PlayerLog.put(player, System.currentTimeMillis());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.isDead()) { return; }
		if (plugin.PlayerLog.containsKey(player)) {
			if ( plugin.PlayerLog.get(player) > System.currentTimeMillis() - 10000) {
				for(Player serv_players: plugin.getServer().getOnlinePlayers()) {
					if(serv_players.hasPermission("NoLog.view")) {
						serv_players.sendMessage(ChatColor.DARK_GRAY + player.getDisplayName() + " left shortly after pvp. [" + ((System.currentTimeMillis() - plugin.PlayerLog.get(player)) /1000 ) + "]");
					}
				}
				plugin.log.info(player.getDisplayName() + " left shortly after pvp. [" + ((System.currentTimeMillis() - plugin.PlayerLog.get(player)) /1000 ) + "]");
			}
			plugin.PlayerLog.remove(player);
		}
	}
}
