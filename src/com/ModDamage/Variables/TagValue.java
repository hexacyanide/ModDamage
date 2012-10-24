package com.ModDamage.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.EventInfo.*;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Tags.Tag;
import com.ModDamage.Tags.Taggable;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;

public class TagValue<T, S> extends SettableDataProvider<T, S>
{	
	public static void register()
	{
		DataProvider.register(Object.class, Object.class, Pattern.compile("_(s?)tag(?:value)?_", Pattern.CASE_INSENSITIVE),
            new IDataParser<Object, Object>()
				{
					@Override
                    @SuppressWarnings("unchecked")
					public IDataProvider<Object> parse(EventInfo info, IDataProvider<Object> objDP, Matcher m, StringMatcher sm)
					{
                        Tag<?> tag = Tag.get(InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info), m.group(1));
                        if (tag == null) return null;

                        Taggable<?> taggable = Taggable.get(objDP, info);

						return sm.acceptIf(new TagValue(tag, taggable));
					}
				});
	}

    private final Tag<T> tag;
    private final Taggable<S> taggable;
	
	TagValue(Tag<T> tag, Taggable<S> taggable)
	{
		super(taggable.inner.provides(), taggable.inner);
        this.tag = tag;
        this.taggable = taggable;
    }
	
	
	@Override
	public T get(S obj, EventData data) throws BailException
	{
        return taggable.get(tag, obj, data);
	}

    @Override
    public void set(S obj, EventData data, T value) throws BailException
    {
        taggable.set(tag, obj, data, value);
    }

    @Override
    public Class<T> provides() {
        return tag.type;
    }
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return taggable + "_tag_" + tag;
	}
}