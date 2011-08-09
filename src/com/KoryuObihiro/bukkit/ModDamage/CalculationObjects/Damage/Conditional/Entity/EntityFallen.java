package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityFallen extends EntityConditionaDamageCalculation 
{
	final int fallDistance;
	public EntityFallen(boolean inverted, boolean forAttacker, int fallDistance, List<DamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.fallDistance = fallDistance;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getFallDistance() > fallDistance;}
}