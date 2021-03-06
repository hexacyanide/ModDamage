package com.moddamage.events.player;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class Fish extends MDEvent implements Listener
{
	public Fish() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class, "player",
			World.class, "world",
			Entity.class, "caught",
			PlayerFishEvent.State.class, "state",
			Integer.class, "experience", "-default",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFish(PlayerFishEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		Entity caught = event.getCaught();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				caught,
                event.getState(),
                event.getExpToDrop(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setExpToDrop(data.get(Integer.class, data.start + data.objects.length - 2));
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
