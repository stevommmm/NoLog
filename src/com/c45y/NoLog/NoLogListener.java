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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event){
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (player.getOpenInventory().getType() == InventoryType.CHEST) {
				return;
			}
			if (player.getOpenInventory().getType() == InventoryType.CREATIVE) {
				return;
			}
			if (player.isSprinting()) {
				event.setCancelled(true);
				Integer count = plugin.InvLog.get(player.getName());
				if (count == null) { 
					count = 0;
				}
				plugin.InvLog.put(player.getName(), count++);
				if (count % 10 == 0) {
					//player.kickPlayer("Inventory Tweaks is not allowed on this server.");
					plugin.messageMods(ChatColor.BLUE + "NL: " + player.getName() + " is using invtweaks, level " + count);
					plugin.log.info("*NL: " + player.getName() + " is using invtweaks, level " + count);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.isDead()) { return; }
		if (player.getFallDistance() > 4F ) {
			System.out.println("*NL: " + player.getName() + " tried to avoid '" + player.getFallDistance() + "' fall damage.");
		}
		if (plugin.PlayerLog.containsKey(player)) {
			// Check if the attacker is dead, we don't care if you log after killing someone
			if ((!plugin.PlayerLog.get(player).getAttacker().isDead()) && (plugin.PlayerLog.get(player).getTimestamp() > System.currentTimeMillis() - 10000)) {
				for (Player playeri : plugin.getServer().getOnlinePlayers()) {
					if (playeri.hasPermission("NoLog.view")) {
						playeri.sendMessage(ChatColor.BLUE + "NL: " + genNoLogMessage(player));
					}
				}
				plugin.PlayerLog.remove(player);
			}
		}
	}

	// Generate the resulting string here to keep it all neat and easy to change.
	// Possibly add a config to turn off parts of the message. i.e. location
	public String genNoLogMessage(Player player) {
		NoLogObject nlo = plugin.PlayerLog.get(player);
		return "" + player.getDisplayName() + " logged on " + nlo.getAttackerName() + ", seconds: " + ((System.currentTimeMillis() - nlo.getTimestamp()) /1000 ) + " ,distance: " + nlo.getDistance(player);
	}
}
