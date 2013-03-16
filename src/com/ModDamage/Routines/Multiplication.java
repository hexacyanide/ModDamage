package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class Multiplication extends ValueChange 
{
	public Multiplication(String configString, ISettableDataProvider<Number> defaultDP, ValueChangeType changeType, IDataProvider<Number> value)
	{ 
		super(configString, defaultDP, changeType, value);
	}
	@Override
	public Number getValue(Integer def, EventData data) throws BailException
	{
		return def * number.get(data).intValue();
	}
	@Override
	public Number getValueDouble(Double def, EventData data) throws BailException
	{
		return def * number.get(data).doubleValue();
	}
	
	public static void register()
	{
		ValueChange.registerRoutine(Pattern.compile("(?:mult(?:iply)?\\.|\\*)(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends ValueChange.ValueBuilder
	{
		@Override
		public Multiplication getNew(Matcher matcher, ValueChangeType changeType, EventInfo info)
		{ 
			IDataProvider<Number> match = DataProvider.parse(info, Number.class, matcher.group(1));
			if (match == null) return null;
			ISettableDataProvider<Number> defaultDP = info.get(Number.class, "-default");
			if (defaultDP == null) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Multiply" + changeType.getStringAppend() + ": " + matcher.group(1));
			return new Multiplication(matcher.group(), defaultDP, changeType, match);
		}
	}
}
