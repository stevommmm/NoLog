package com.c45y.NoLog;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class NoLog extends JavaPlugin {
	private final LogEntityParts logEntityParts = new LogEntityParts(this);
	private final LogPlayerParts logPlayerParts = new LogPlayerParts(this);
	Logger log = Logger.getLogger("Minecraft");
	public Map<Player, Long> PlayerLog = new HashMap<Player, Long>();

	public void onEnable(){ 
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DEATH, logEntityParts, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, logEntityParts, Event.Priority.Normal, this);
		//pm.registerEvent(Event.Type.PLAYER_JOIN, logPlayerParts, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, logPlayerParts, Event.Priority.Normal, this);
		log.info("NoLog enabled.");
	}

	public void onDisable(){ 
		log.info("NoLog disabled.");
	}
}
