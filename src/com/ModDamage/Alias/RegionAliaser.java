package com.ModDamage.Alias;

import java.util.Collection;

import com.ModDamage.LogUtil;
import com.ModDamage.Alias.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.ExternalPluginManager;

public class RegionAliaser extends CollectionAliaser<String> 
{
	public static RegionAliaser aliaser = new RegionAliaser();
	public static Collection<String> match(String string) { return aliaser.matchAlias(string); }
	
	public RegionAliaser() { super(AliasManager.Region.name()); }

	@Override
	protected String matchNonAlias(String key)
	{
		if(!ExternalPluginManager.getAllRegions().contains(key))
			LogUtil.warning_strong("Warning: region \"" + key + "\" does not currently exist.");
		return key;
	}

	//@Override
	//protected String getObjectName(String object){ return object; }
}
