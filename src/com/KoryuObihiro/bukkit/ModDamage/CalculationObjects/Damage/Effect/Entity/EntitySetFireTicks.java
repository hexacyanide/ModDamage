package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntitySetFireTicks extends EntityEffectDamageCalculation 
{
	int ticks;
	public EntitySetFireTicks(boolean forAttacker, List<DamageCalculation> calculations)
	{
		this.forAttacker = forAttacker;
		this.calculations = calculations;
		this.ticks = 0;
	}
	public EntitySetFireTicks(boolean forAttacker, int ticks)
	{
		this.forAttacker = forAttacker;
		this.ticks = ticks;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			ticks = eventInfo.eventDamage;
			for(DamageCalculation calculation : calculations)
				calculation.calculate(eventInfo);
			(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).setFireTicks(eventInfo.eventDamage);
			eventInfo.eventDamage = ticks;
		}
		else (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).setFireTicks(ticks);
	}
}