package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffectRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class PlayerCalculatedEffectRoutine extends CalculatedEffectRoutine<Player>
{
	protected final boolean forAttacker;
	protected static final String entityPart = "(entity|attacker|target)";
	public PlayerCalculatedEffectRoutine(boolean forAttacker, List<Routine> routines)
	{
		super(routines);
		this.forAttacker = forAttacker;
	}
	@Override
	protected Player getAffectedObject(TargetEventInfo eventInfo){ return (getAffectedObject(eventInfo) instanceof Player)?((Player)getAffectedObject(eventInfo)):null;}
}
