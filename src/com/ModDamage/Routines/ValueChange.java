package com.ModDamage.Routines;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Utils;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class ValueChange extends Routine 
{
	private static final LinkedHashMap<Pattern, ValueBuilder> builders = new LinkedHashMap<Pattern, ValueBuilder>();
	protected enum ValueChangeType
	{
		Add
		{
			@Override
			int changeValue(int current, int value){ return current + value; }
			@Override
			double changeValueDouble(double current, double value){ return current + value; }
		},
		Set
		{
			@Override
			int changeValue(int current, int value){ return value; }
			@Override
			double changeValueDouble(double current, double value){ return value; }
		},
		Subtract
		{
			@Override
			int changeValue(int current, int value){ return current - value; }
			@Override
			double changeValueDouble(double current, double value){ return current - value; }
		};
		abstract int changeValue(int current, int value);
		abstract double changeValueDouble(double current, double value);

		public String getStringAppend(){ return " (" + this.name().toLowerCase() + ")"; }

	}
	
	private final ValueChangeType changeType;
	protected final IDataProvider<Number> number;
	protected final ISettableDataProvider<Number> defaultDP;
	protected ValueChange(String configString, ISettableDataProvider<Number> defaultDP, ValueChangeType changeType, IDataProvider<Number> number)
	{
		super(configString);
		this.defaultDP = defaultDP;
		this.changeType = changeType;
		this.number = number;
	}
	
	@Override
	public final void run(final EventData data) throws BailException{
		Number defN = defaultDP.get(data);
		if (Utils.isFloating(defaultDP.provides()) || Utils.isFloating(number.provides())) {
	        double def;
	        if (defN == null) def = 0;
	        else def = defN.doubleValue();
	        Double value = getValueDouble(def, data).doubleValue();
	        if (value == null) return;
	        
			defaultDP.set(data, changeType.changeValueDouble(
					def, value));
		}
		else {
	        int def;
	        if (defN == null) def = 0;
	        else def = defN.intValue();
	        Integer value = getValue(def, data).intValue();
	        if (value == null) return;
	        
			defaultDP.set(data, changeType.changeValue(
					def, value));
		}
	}
	
	protected Number getValue(Integer def, EventData data) throws BailException
	{
		return number.get(data).intValue();
	}
	

	protected Number getValueDouble(Double def, EventData data) throws BailException
	{
		return number.get(data).doubleValue();
	}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(?:(\\+|add\\.)|(\\-|sub\\.)|(set\\.|=|))(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		
		Division.register();
		Multiplication.register();
	}
	
	private static final class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public ValueChange getNew(final Matcher matcher, EventInfo info)
		{
			ValueChangeType changeType = null;
			if(matcher.group(1) != null)
				changeType = ValueChangeType.Add;
			else if(matcher.group(2) != null)
				changeType = ValueChangeType.Subtract;
			else if(matcher.group(3) != null)
				changeType = ValueChangeType.Set;
			if (changeType == null) throw new Error("changeType == null $VC86");
			
			for(Entry<Pattern, ValueBuilder> entry : builders.entrySet())
			{
				Matcher anotherMatcher = entry.getKey().matcher(matcher.group(4));
				if(anotherMatcher.matches())
					return entry.getValue().getNew(anotherMatcher, changeType, info);
			}
			IDataProvider<Number> value = DataProvider.parse(info, Number.class, matcher.group(4));
			if (value == null) return null;
			ISettableDataProvider<Number> defaultDP = info.get(Number.class, "-default");
			if (defaultDP == null) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, changeType.name() + ": " + matcher.group(4));
			return new ValueChange(matcher.group(), defaultDP, changeType, value);
		}
	}
	
	public static void registerRoutine(Pattern pattern, ValueBuilder builder){ builders.put(pattern, builder); }
	public static abstract class ValueBuilder
	{
		public abstract ValueChange getNew(final Matcher matcher, final ValueChangeType changeType, EventInfo info);
	}
}
