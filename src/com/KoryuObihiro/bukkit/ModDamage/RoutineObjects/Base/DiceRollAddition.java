package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class DiceRollAddition extends Chanceroutine 
{
	private int maxValue;
	public DiceRollAddition(String configString, int value)
	{
		super(configString);
		maxValue = value;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue += Math.abs(random.nextInt()%(maxValue + 1));}
	
	public static DiceRollAddition getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new DiceRollAddition(matcher.group(), Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register() 
	{
		Routine.registerBase(DiceRollAddition.class, Pattern.compile("roll\\." + IntegerMatch.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
	}
}