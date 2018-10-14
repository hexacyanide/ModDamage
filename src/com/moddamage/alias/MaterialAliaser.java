package com.moddamage.alias;

import java.util.Collection;

import org.bukkit.Material;

import com.moddamage.alias.Aliaser.CollectionAliaser;

public class MaterialAliaser extends CollectionAliaser<Material>
{
	public static MaterialAliaser aliaser = new MaterialAliaser();
	public static Collection<Material> match(String string) { return aliaser.matchAlias(string); }

	public MaterialAliaser() { super(AliasManager.Material.name()); }

	@SuppressWarnings("deprecation")
    @Override
	public Material matchNonAlias(String key)
	{
		if (key.toLowerCase().startsWith("id_"))
		{
		    String num = key.substring(3);

			try
			{
				int idx = Integer.parseInt(num);
				// TODO(esu): Fix this for high potential of problems (like OOB).
				Material[] materials = Material.values();
				return materials[idx];
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}

		return Material.matchMaterial(key);
	}
}
