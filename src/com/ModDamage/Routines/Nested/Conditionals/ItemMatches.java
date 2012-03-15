package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.Aliasing.ItemAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class ItemMatches extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.(material|matches)\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	private final DataRef<ItemStack> itemRef;
	private final Collection<ModDamageItemStack> items;
	public ItemMatches(DataRef<ItemStack> itemRef, Collection<ModDamageItemStack> items)
	{  
		this.itemRef = itemRef;
		this.items = items;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		ItemStack item = itemRef.get(data);
		for (ModDamageItemStack mdis : items)
		{
			mdis.update(data);
			if (mdis.matches(item))
				return true;
		}
		return false;
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}	
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public ItemMatches getNew(Matcher matcher, EventInfo info)
		{
			DataRef<ItemStack> itemRef = info.get(ItemStack.class, matcher.group(1).toLowerCase());
			Collection<ModDamageItemStack> matchedItems = ItemAliaser.match(matcher.group(3), info);
			if(itemRef != null && !matchedItems.isEmpty())
			{
				if (matcher.group(1).equalsIgnoreCase("material"))
					ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Using the material version is deprecated. Please use '"+ matcher.group(1) +".matches."+ matcher.group(3) +"' instead.");
				return new ItemMatches(itemRef, matchedItems);
			}
			return null;
		}
	}
}