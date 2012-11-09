package com.c45y.NoLog;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class NoLogListener implements Listener {
	public final NoLog plugin;

	public NoLogListener(NoLog instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		if (event instanceof PlayerDeathEvent) {
			Player player = (Player) event.getEntity();
			if (plugin.PlayerLog.containsKey(player)) {
				plugin.PlayerLog.remove(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
		if (!(damageEvent.getEntity() instanceof Player)) {
			return;
		}
		if (damageEvent.getDamager() instanceof Player) {
			Player player = (Player) event.getEntity();
			NoLogObject nlo = new NoLogObject(
					(Player) damageEvent.getDamager(),
					System.currentTimeMillis(), player.getLocation());
			plugin.PlayerLog.put(player, nlo);
		} else if (damageEvent.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) damageEvent.getDamager();
			if (projectile.getShooter() instanceof Player) {
				Player player = (Player) event.getEntity();
				Player shooter = (Player) projectile.getShooter();
				if (player != shooter) {
					NoLogObject nlo = new NoLogObject(shooter,
							System.currentTimeMillis(), player.getLocation());
					plugin.PlayerLog.put(player, nlo);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.getFallDistance() > 4F) {
			System.out.println("*NL: " + player.getName() + " tried to avoid '"
					+ player.getFallDistance() + "' fall damage.");
		}
		if (plugin.PlayerLog.containsKey(player)) {
			if (!containsPlayer(player.getNearbyEntities(4, 4, 4))) {
				return; // Check we actually have players close by
			}
			if (isInventoryEmpty(player.getInventory())) {
				return; // Do we really care if they have nothing anyway?
			}
			NoLogObject nlo = plugin.PlayerLog.get(player);
			plugin.PlayerLog.remove(player);
			if (player.getTicksLived() < 200) {
				return; // Player is not a suitable age
			}
			if (nlo.getAttacker().getTicksLived() < 200) {
				return; // Attacker is not a suitable age
			}
			if (nlo.getTimestamp() > System.currentTimeMillis() - 10000) {
				return; // PVP was more than 10 seconds ago
			}
			if (nlo.getDistance(nlo.getAttacker()) < 50) {
				return; // They are a large distance from the attacker
			}
			plugin.messageMods(ChatColor.BLUE + "NL: "
					+ genNoLogMessage(player));
			plugin.log.info("NoLog: " + genNoLogMessage(player));
			if (plugin.chicken) {
				event.setQuitMessage(event.getPlayer() + " chickened out");
				Location loc = player.getLocation();
				loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
			}
		}
	}

	private boolean containsPlayer(List<Entity> entityList) {
		for (Entity e : entityList) {
			if (e.getClass().isInstance(Player.class)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInventoryEmpty(PlayerInventory pli) {
		for (final ItemStack is : pli.getArmorContents()) {
			if (is != null) {
				return false;
			}
		}
		for (final ItemStack is : pli.getContents()) {
			if (is != null) {
				return false;
			}
		}
		return true;
	}

	// Generate the resulting string here to keep it all neat and easy to
	// change.
	// Possibly add a config to turn off parts of the message. i.e. location
	public String genNoLogMessage(Player player) {
		NoLogObject nlo = plugin.PlayerLog.get(player);
		return "" + player.getDisplayName() + " logged on "
				+ nlo.getAttackerName() + ", seconds: "
				+ ((System.currentTimeMillis() - nlo.getTimestamp()) / 1000)
				+ " ,distance: " + nlo.getDistance(player);
	}
}
