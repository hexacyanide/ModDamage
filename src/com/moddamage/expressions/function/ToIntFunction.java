package com.moddamage.expressions.function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class ToIntFunction implements IDataProvider<Integer>
{
	private final IDataProvider<?> valDP;

	private ToIntFunction(IDataProvider<?> stringDP)
	{
		this.valDP = stringDP;
	}

	@Override
	public Integer get(EventData data) throws BailException
	{
		Object val = valDP.get(data);
		if (val == null) return null;
		
		if (val instanceof String) {
			try {
				return Integer.parseInt((String) val);
			}
			catch (NumberFormatException e) {
				return null;
			}
		}
		else if (val instanceof Number) {
			return ((Number) val).intValue();
		}
		else
			return null; // shouldn't happen
	}

	@Override
	public Class<Integer> provides() { return Integer.class; }

	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(Integer.class, Pattern.compile("(?:to|as)int(?:eger)?\\(", Pattern.CASE_INSENSITIVE), new BaseDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, Matcher m, StringMatcher sm)
				{
					IDataProvider<?> valDP;
					
					IDataProvider<Number> numberDP = DataProvider.parse(info, Number.class, sm.spawn(), false, false, null);
					if (numberDP != null)
						valDP = numberDP;
					else
					{
						IDataProvider<String> strDP = DataProvider.parse(info, String.class, sm.spawn(), false, false, null);
						if (strDP != null)
							valDP = strDP;
						else
						{
							IDataProvider<Object> objDP = DataProvider.parse(info, null, sm.spawn());
							if (objDP != null)
								LogUtil.error("Wanted String or Number for toint(), not " + objDP.provides().getSimpleName());
							return null;
						}
					}
					
					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						LogUtil.error("Missing end paren: \"" + sm.string + "\"");
						return null;
					}

					return sm.acceptIf(new ToIntFunction(valDP));
				}
			});
	}

	@Override
	public String toString()
	{
		return "toInt(" + valDP + ")";
	}
}
