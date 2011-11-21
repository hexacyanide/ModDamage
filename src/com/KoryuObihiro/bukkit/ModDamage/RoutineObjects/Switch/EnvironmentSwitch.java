package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine.SingleValueSwitchRoutine;

public class EnvironmentSwitch extends SingleValueSwitchRoutine<Environment>
{	
	public EnvironmentSwitch(String configString, LinkedHashMap<String, Object> switchStatements){ super(configString, switchStatements);}
	@Override
	protected Environment getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo.world.getEnvironment();}
	@Override
	protected Collection<Environment> matchCase(String switchCase){ return Arrays.asList(Environment.valueOf(switchCase.toUpperCase()));}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("event\\.environment", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public EnvironmentSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
		{
			return new EnvironmentSwitch(matcher.group(), switchStatements);
		}
	}
}