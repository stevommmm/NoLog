package com.c45y.NoLog;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class NoLog extends JavaPlugin {
	private final NoLogListener loglistener = new NoLogListener(this);
	Logger log = Logger.getLogger("Minecraft");
	public Map<Player, NoLogObject> PlayerLog = new HashMap<Player, NoLogObject>();

	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(loglistener, this);
		log.info("NoLog enabled.");
	}

	public void onDisable(){
		log.info("NoLog disabled.");
	}
	
	public void messageMods(String str) {
		for (Player playeri : getServer().getOnlinePlayers()) {
			if (playeri.hasPermission("NoLog.view")) {
				playeri.sendMessage(str);
			}
		}
	}
}