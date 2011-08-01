package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class EntityTypeSwitch extends EntitySwitchRoutine<ModDamageElement>
{
	public EntityTypeSwitch(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		super(forAttacker, switchStatements);
	}
	@Override
	protected ModDamageElement getRelevantInfo(TargetEventInfo eventInfo){ return (forAttacker && eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).element_attacker:eventInfo.element_target;}
	@Override
	protected ModDamageElement matchCase(String switchCase){ return ModDamageElement.matchElement(switchCase);}
	
	@Override
	protected boolean compare(ModDamageElement info_1, ModDamageElement info_2){ return info_1.matchesType(info_2);}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, EntityTypeSwitch.class, Pattern.compile(ModDamage.entityPart + "type(?:\\.(" + ModDamage.elementRegex + "))?", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityTypeSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null)
		{
			boolean forAttacker = matcher.group(1).equalsIgnoreCase("attacker");
			String typeString = matcher.group(2);
			ModDamageElement element = (typeString != null?ModDamageElement.matchElement(typeString):ModDamageElement.GENERIC);
			if(element != null) return new EntityTypeSwitch(forAttacker, switchStatements);
		}
		return null;
	}
}
