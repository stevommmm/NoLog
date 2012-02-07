package com.c45y.NoLog;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class LogEntityParts extends EntityListener{
	public NoLog plugin;

	public LogEntityParts(NoLog instance) {
		plugin = instance;
	}

	public void onEntityDeath(EntityDeathEvent event){
		if (event instanceof PlayerDeathEvent) {
			Player player = (Player) event.getEntity();
			plugin.PlayerLog.remove(player);
		}
	}
	
	public void onEntityDamage(EntityDamageEvent event){
		if (event.getEntity() instanceof Player) {
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
			if (damageEvent.getDamager() instanceof Player) {
				Player player = (Player) event.getEntity();
				plugin.PlayerLog.put(player, System.currentTimeMillis());
			}
		}	
	}
}
