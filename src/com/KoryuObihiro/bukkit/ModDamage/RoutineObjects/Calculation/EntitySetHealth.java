package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntitySetHealth extends LivingEntityCalculationRoutine
{
	public EntitySetHealth(String configString, EntityReference entityReference, IntegerMatch match)
	{
		super(configString, entityReference, match);
	}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setHealth(input);
	}
	
	public static void register()
	{
		CalculationRoutine.registerCalculation(EntitySetHealth.class, Pattern.compile("(\\w+)effect\\.sethealth", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntitySetHealth getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null && EntityReference.isValid(matcher.group(1)))
			return new EntitySetHealth(matcher.group(), EntityReference.match(matcher.group(1)), match);
		return null;
	}
}