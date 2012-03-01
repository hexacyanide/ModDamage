package com.ModDamage.Backend.Matching;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.Matching.DynamicIntegers.ConstantInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicCalculatedInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicEnchantmentInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicEntityInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicEntityTagInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicEventInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicMcMMOInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicPlayerInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicItemInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicRoutineInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicServerInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicWorldInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.Function;
import com.ModDamage.Backend.Matching.DynamicIntegers.NegativeInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.OldEventValueUpgradeHelper;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routines;

public abstract class DynamicInteger extends DynamicString
{
	public static final Pattern whitespace = Pattern.compile("\\s*");
	
	public final int getValue(EventData data) throws BailException
	{
		try
		{
			return myGetValue(data);
		}
		catch (Throwable t)
		{
			throw new BailException(this, t);
		}
	}
	protected abstract int myGetValue(EventData data) throws BailException;
	
	public boolean isSettable(){ return false; }
	public void setValue(EventData data, int value) { }
	
	public static DynamicInteger getNew(Routines routines, EventInfo info) 
	{
		if(routines != null && !routines.isEmpty())
			return new DynamicRoutineInteger(routines, info);
		return null;
	}
	
	public static DynamicInteger getIntegerFromFront(StringMatcher sm, EventInfo info)
	{
		sm.matchFront(whitespace);
		
		for(Entry<Pattern, DynamicIntegerBuilder> entry : registeredIntegers.entrySet())
		{
			StringMatcher sm2 = sm.spawn();
			Matcher matcher = sm2.matchFront(entry.getKey());
			if(matcher != null)
			{
				DynamicInteger dir = entry.getValue().getNewFromFront(matcher, sm2, info);
				if(dir != null)
				{
					sm.accept();
					return dir;
				}
			}
		}
		return null;
	}
	
	public static DynamicInteger getNew(String string, EventInfo info){ return getNew(string, info, true); }
	public static DynamicInteger getNew(String string, EventInfo info, boolean outputError)
	{
		StringMatcher sm = new StringMatcher(string);
		DynamicInteger integer = getIntegerFromFront(sm.spawn(), info);
		if (outputError)
		{
			if (integer == null)
				ModDamage.addToLogRecord(OutputPreset.FAILURE, " No match found for dynamic integer \"" + string + "\"");
			else if (!sm.string.isEmpty() && !whitespace.matcher(sm.string).matches())
				ModDamage.addToLogRecord(OutputPreset.WARNING, " Extra junk found after dynamic integer \"" + sm.string + "\"");
		}
		return integer;
	}
	
	public static void registerAllIntegers()
	{
		registeredIntegers.clear();
		
		ConstantInteger.register();
		DynamicCalculatedInteger.register();
		DynamicEnchantmentInteger.register();
		DynamicEntityInteger.register();
		DynamicEntityTagInteger.register();
		DynamicMcMMOInteger.register();
		DynamicPlayerInteger.register();
		DynamicItemInteger.register();
		DynamicServerInteger.register();
		DynamicWorldInteger.register();
		NegativeInteger.register();
		Function.register();
		DynamicEventInteger.register();
		OldEventValueUpgradeHelper.register();
		
		/*DynamicInteger.register(Pattern.compile("_\\w+"),
				new DynamicIntegerBuilder()
					{
						@Override
						public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
						{
							sm.accept();
							return DynamicInteger.getNew(RoutineAliaser.match(matcher.group()));
						}
					});*/
	}
	
	private static Map<Pattern, DynamicIntegerBuilder> registeredIntegers = new LinkedHashMap<Pattern, DynamicIntegerBuilder>();
	
	public static void register(Pattern pattern, DynamicIntegerBuilder dib)
	{
		registeredIntegers.put(pattern, dib);
	}
	
	abstract public static class DynamicIntegerBuilder
	{
		abstract public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info);
	}

	@Override
	public String getString(EventData data) throws BailException
	{
		return ""+getValue(data);
	}
}