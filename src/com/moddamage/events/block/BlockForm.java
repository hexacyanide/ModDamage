package com.moddamage.events.block;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class BlockForm extends MDEvent implements Listener
{
	public BlockForm() { super(myInfo); }

	static final EventInfo myInfo = new SimpleEventInfo(
			World.class, "world",
			Block.class, "block",
			Material.class, "newtype",
			Integer.class, "newtypeid",
			Integer.class, "newdata",
			Boolean.class, "cancelled");

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockForm(BlockFormEvent event)
	{
		if(!ModDamage.isEnabled) return;

		@SuppressWarnings("deprecation")
        EventData data = myInfo.makeData(
                event.getBlock().getWorld(),
				event.getBlock(),
                event.getNewState().getType(),
                // TODO(esu): Fix use of deprecated magic number.
                event.getNewState().getType().getId(),
                (int) event.getNewState().getData().getData(),
				event.isCancelled());

		runRoutines(data);

		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
