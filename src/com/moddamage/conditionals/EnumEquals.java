package com.moddamage.conditionals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.matchables.Matchable;

@SuppressWarnings("rawtypes")
public class EnumEquals extends Conditional<Enum>
{
	public static final Pattern pattern = Pattern.compile("\\.(?:is|type)\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	
	private final Collection<Enum> types;
	
	public EnumEquals(IDataProvider<Enum> enumDP, Collection<Enum> types)
	{ 
		super(Enum.class, enumDP);
		this.types = types;
		
		if (types.contains(null))
			defaultValue = true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Boolean get(Enum matchable, EventData data)
	{
		if (matchable instanceof Matchable) {
			Matchable m = (Matchable) matchable;

			for(Enum type : types)
				if(m.matches((Matchable) type))
					return true;
		}
		for(Enum type : types)
			if(matchable == type)
				return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".is." + Utils.joinBy(",", types);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Enum.class, pattern, new IDataParser<Boolean, Enum>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Enum> enumDP, Matcher m, StringMatcher sm)
				{
					@SuppressWarnings("unchecked")
					Map<String, ? extends Enum<?>> possibleTypes = Utils.getTypeMapForEnum(enumDP.provides(), true);
					
					List<Enum> types = new ArrayList<Enum>();
					
					for (String typeStr : m.group(1).split(","))
					{
						Enum type = possibleTypes.get(typeStr.toUpperCase());
						if (type == null)
						{
							if (typeStr.equalsIgnoreCase("none"))
							{
								types.add(null);
								continue;
							}
							LogUtil.error("Error: \"" + typeStr + "\" is not a valid " + enumDP.provides().getSimpleName());
							return null;
						}
						types.add(type);
					}
					
					if(types == null || types.isEmpty()) return null;
					
					return new EnumEquals(enumDP, types);
				}
			});
	}
}
