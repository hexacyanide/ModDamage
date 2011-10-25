package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class Chance extends ConditionalStatement
{
	protected final Random random = new Random();
	protected final DynamicInteger probability;
	public Chance(DynamicInteger probability)
	{ 
		super(false);
		this.probability = probability;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return Math.abs(random.nextInt()%101) <= probability.getValue(eventInfo);}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Chance.class, Pattern.compile("chance\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
	}
	
	public static Chance getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Chance(DynamicInteger.getNew(matcher.group(1)));
		return null;
	}
}
