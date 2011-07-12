package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class PlayerWieldSwitch extends EntitySwitchCalculation<Material>
{
	public PlayerWieldSwitch(boolean forAttacker, LinkedHashMap<Material, List<ModDamageCalculation>> switchStatements) 
	{
		super(forAttacker, switchStatements);
	}

	@Override
	protected Material getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.materialInHand_attacker:eventInfo.materialInHand_target);}
	@Override
	protected Material getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
}