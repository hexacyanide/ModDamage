package com.moddamage.events.entity;

import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;
import com.moddamage.events.Init;

public class Spawn extends MDEvent implements Listener
{
	public Spawn() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class, "entity",
			World.class, "world",
			Double.class, "health", "-default",
			SpawnReason.class, "reason", "spawn_reason",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
        AttributeInstance attr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHealth = attr.getValue();

		EventData data = myInfo.makeData(
				player, // entity
				player.getWorld(),
                maxHealth,  // TODO(esu): Review for side effects.
				null,
				null
				);
		
		runRoutines(data);
		
		player.setHealth(data.get(Double.class, data.start + 2));
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if(!ModDamage.isEnabled || event.isCancelled()) return;
		
		LivingEntity entity = event.getEntity();
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld(),
				entity.getHealth(),
				event.getSpawnReason(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
		
		double health = data.get(Double.class, data.start + 2);
		
		if (health > 0) {
            AttributeInstance attr = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            // TODO(esu): Review for side effects/wrong value.
            double maxHealth = attr.getValue();
			entity.setHealth(Math.min(health, maxHealth));
		}
		else
			event.setCancelled(true);
		
		if (!event.isCancelled())
			Init.onInit(entity);
	}
}
