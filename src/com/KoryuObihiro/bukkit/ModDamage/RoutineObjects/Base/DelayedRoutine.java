package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class DelayedRoutine extends Routine
{	
	private final DynamicInteger delay;
	private final List<Routine> routines;
	public DelayedRoutine(String configString, DynamicInteger delayValue, List<Routine> routines)
	{
		super(configString);
		this.delay = delayValue;
		this.routines = routines;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		Bukkit.getScheduler().scheduleAsyncDelayedTask(Bukkit.getPluginManager().getPlugin("ModDamage"), new DelayedRunnable(eventInfo, routines), delay.getValue(eventInfo));
	}
		
	public static void register()
	{
		Routine.registerBase(DelayedRoutine.class, Pattern.compile("delay\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
	}
	
	public static DelayedRoutine getNew(Matcher matcher, List<Routine> routines)
	{ 
		if(matcher != null)
		{
			DynamicInteger numberMatch = DynamicInteger.getNew(matcher.group(1));
			return new DelayedRoutine(matcher.group(), numberMatch, routines);
		}
		return null;
	}
	
	private class DelayedRunnable implements Runnable
	{
		private final TargetEventInfo eventInfo;
		private final List<Routine> routines;
		private DelayedRunnable(TargetEventInfo eventInfo, List<Routine> routines)
		{
			this.eventInfo = eventInfo;
			this.routines = routines;
		}
		
		@Override
		public void run() //Runnable
		{
			for(Routine routine : routines)
				routine.run(eventInfo);
		}
	}
}
