package com.c45y.NoLog;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogPlayerParts extends PlayerListener{
	public NoLog plugin;

	public LogPlayerParts(NoLog instance) {
		plugin = instance;
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		// return following in message
		// Seconds from last pvp
			Player player = event.getPlayer();
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
