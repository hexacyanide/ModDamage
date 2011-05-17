package com.KoryuObihiro.bukkit.ModDamage.Handling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;



public class GroupHandler
{
//// MEMBERS //// 
	
	public boolean isLoaded = false;
	public boolean pvpLoaded = false;
	
	//world-type and mob-type damage
	final public HashMap<DamageType, List<String>> offensiveRoutines = new HashMap<DamageType, List<String>>();
	final public HashMap<DamageType, List<String>> defensiveRoutines = new HashMap<DamageType, List<String>>();
	
	//pvp damage
	final public HashMap<String, List<String>> pvpOffensiveRoutines = new HashMap<String, List<String>>();
	final public HashMap<String, List<String>> pvpDefensiveRoutines = new HashMap<String, List<String>>();
	
	//item damage
	final public HashMap<Material, List<String>> itemOffensiveRoutines = new HashMap<Material, List<String>>();
	final public HashMap<Material, List<String>> itemDefensiveRoutines = new HashMap<Material, List<String>>();
	
	//scan
	final public List<Material> scanItems = new ArrayList<Material>();
	
	final private ConfigurationNode offensiveNode;
	final private ConfigurationNode defensiveNode;
	final public DamageCalculator damageCalc;
	final public WorldHandler worldHandler;
	final String groupName;
	
	
//// CONSTRUCTOR ////
	public GroupHandler(WorldHandler worldHandler, String name, ConfigurationNode offensiveGroupNode, ConfigurationNode defensiveGroupNode, DamageCalculator damageCalc) 
	{
		this.worldHandler = worldHandler;
		this.groupName = name; //for debugging purposes
		this.offensiveNode = offensiveGroupNode;
		this.defensiveNode = defensiveGroupNode;
		this.damageCalc = damageCalc;
		this.isLoaded = loadRoutines();
	}
	
	
	private boolean loadRoutines() 
	{
		try
		{
			//TODO Looks a bit messy - refactor for fewer != null checks?
			//clear everything first
			clearRoutines();
	
	//load group settings
			String damageCategories[] = {"animal", "item", "mob", "nature"};
			for(int i = 0; i < damageCategories.length; i++)
			{
				if(offensiveNode != null && !loadDamageTypeGroup(damageCategories[i], true) 
						&& ModDamage.consoleDebugging_verbose)
					worldHandler.log.warning("Couldn't find group " + groupName + " (in world \"" + worldHandler.getWorld().getName() + "\") Offensive category node \"" + damageCategories[i] + "\"");
				if(defensiveNode != null && !loadDamageTypeGroup(damageCategories[i], false) 
						&& ModDamage.consoleDebugging_verbose)
					worldHandler.log.warning("Couldn't find " + groupName + " (in world \"" + worldHandler.getWorld().getName() + "\") Defensive category node \"" + damageCategories[i] + "\"");
			}
			
	//load PvP settings
			
	//load item settings - item-specifics are not handled by "item" damage category loading
			if(offensiveNode != null)
			{
				loadItemRoutines(true);
				loadPVPRoutines(true, true);
			}	
			if(defensiveNode != null)
			{
				loadItemRoutines(false);
				loadPVPRoutines(false, true);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			worldHandler.log.severe("[" + worldHandler.plugin.getDescription().getName() 
					+ "] Invalid configuration for group " + groupName + " world \"" 
					+ worldHandler.getWorld().getName() + "\"; using default settings");
			useDefaults();
			return false;
		}
		return true;
	}

	//// CLASS-SPECIFIC FUNCTIONS ////
	//getters
	public String getGroupName(){ return groupName;}
	
	//setters
	
	//TODO Clean up to just use simple return?
	public int calcAttackBuff(DamageType mobType_target, int eventDamage)
	{
		return runRoutines(mobType_target, true, eventDamage);
	}
	public int calcDefenseBuff(DamageType damageType, int eventDamage)
	{	
		return runRoutines(damageType, false, eventDamage);
	}
	
	public int calcAttackBuff(String group_target, int eventDamage)
	{
		//TODO BOW weapon type
		worldHandler.log.info("Grouphandler: " + groupName + " says to " + group_target + ": LOLWUT");
		return runRoutines(DamageType.NATURE_PLAYER, true, eventDamage) 
			+ runPVPRoutines(group_target, true, eventDamage);
	}
	public int calcDefenseBuff(String group_attacking, int eventDamage)
	{	
		worldHandler.log.info("Grouphandler: " + groupName + " says to " + group_attacking + ": BITE ME");
		return runRoutines(DamageType.NATURE_PLAYER, false, eventDamage) 
			+ runPVPRoutines(group_attacking, false, eventDamage);
	}
	
	public int calcAttackBuff(Material inHand_attacking, int eventDamage)
	{
		return runItemRoutines(inHand_attacking, true, eventDamage);
	}

	public int calcDefenseBuff(Material inHand_attacking, int eventDamage)
	{
		return runItemRoutines(inHand_attacking, false, eventDamage);
	}
	public boolean damageType_isLoaded(DamageType damageType){ return offensiveRoutines.containsKey(damageType);}
	
	public boolean sendGroupConfig(Player player, String configReference)
	{ //TODO see sendWorldConfig for template to use here
		if(isLoaded)
		{
			if(player != null)
			{
				
			}
			else
			{
				
			}
			return true;
		}
		return false;
	}
	
	public void loadDamageType(String damageDescriptor, boolean isOffensive)
	{
		ConfigurationNode relevantNode = (isOffensive
											?offensiveNode.getNode(damageDescriptor)
											:defensiveNode.getNode(damageDescriptor));
		for(DamageType damageType : DamageType.values())
			if(damageType.getDescriptor().equals("mob"))
			{
				List<String> calcStrings = relevantNode.getStringList(damageType.getConfigReference(), null);
				if(!calcStrings.equals(null))
					for(String calcString : calcStrings)
						if(!damageCalc.checkCommandString(calcString))
						{
							worldHandler.log.severe("Invalid command string \"" + calcString + "\" for group " + groupName 
								+ " in " + (isOffensive?"Offensive ":"Defensive ") + damageType.getConfigReference() 
								+ " definition - refer to config for proper calculation node");
							calcStrings.clear();
						}
				if(calcStrings.size() > 0)
				{
					if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
						(isOffensive?offensiveRoutines:defensiveRoutines).put(damageType, calcStrings);
					else if(ModDamage.consoleDebugging_normal) 
						worldHandler.log.warning("Repetitive " + damageType.getConfigReference() + " definition in " 
								+ (isOffensive?"Offensive":"Defensive") + " settings for group " + groupName + " - ignoring");
				}
				else if(ModDamage.consoleDebugging_verbose)
				{		
					worldHandler.log.warning("No instructions found for group " + groupName + " " + damageType.getConfigReference() 
						+ " node in " + (isOffensive?"Offensive":"Defensive") + " for world " 
						+ worldHandler.world.getName() +  " - is this on purpose?");
				}
				if(ModDamage.consoleDebugging_normal)
					worldHandler.log.info("[" + worldHandler.plugin.getDescription().getName() + "] " + worldHandler.world.getName() 
						+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive") + ":" 
						+ damageType.getDescriptor() + ":" + damageType.getConfigReference() + " " 
						+ (ModDamage.consoleDebugging_verbose?("\n" + calcStrings.toString()):""));//debugging
			}
	}
	
	public boolean loadItemRoutines(boolean isOffensive){ return loadItemRoutines(isOffensive, false);}
	public boolean loadItemRoutines(boolean isOffensive, boolean force)
	{
		ConfigurationNode itemNode = isOffensive?offensiveNode.getNode("item"):defensiveNode.getNode("item");
		if(itemNode != null)	
		{
			List<String> itemList = (isOffensive?offensiveNode:defensiveNode).getKeys("item");
			for(Material material : Material.values())
				if(itemList.contains(material.name()) || itemList.contains(material.getId()))
				{
					List<String> calcStrings = itemNode.getStringList(material.name(), null);
					worldHandler.log.warning(material.name() + " " + itemNode.getStringList(Integer.toString(material.getId()).toUpperCase(), null).toString());
					if(calcStrings == null) calcStrings = itemNode.getStringList(Integer.toString(material.getId()), null);
					if(!calcStrings.equals(null))
					{
						for(String calcString : calcStrings)
							if(!damageCalc.checkCommandString(calcString))
							{
								worldHandler.log.severe("Invalid command string \"" + calcString + "\" in " 
										+ (isOffensive?"Offensive":"Defensive") + " " + material.name() + "(" + material.getId()
										+ ") definition - refer to config for proper calculation node");
								calcStrings.clear();
							}
						if(calcStrings.size() > 0)
						{
							if(ModDamage.consoleDebugging_normal) worldHandler.log.info("-" + worldHandler.getWorld().getName() 
									+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive") 
									+ ":" + material.name() + "(" + material.getId() + ")"
									+ (ModDamage.consoleDebugging_verbose?(" " + calcStrings.toString()):""));//debugging
							if(!(isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).containsKey(material))
								(isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).put(material, calcStrings);
							else if(ModDamage.consoleDebugging_normal) worldHandler.log.warning("[" + worldHandler.plugin.getDescription().getName() + "] Repetitive " 
									+ material.name() + "(" + material.getId() + ") definition in " + (isOffensive?"Offensive":"Defensive") 
									+ " item group settings - ignoring");
						}
						else if(ModDamage.consoleDebugging_verbose)
						{
							worldHandler.log.warning("No instructions found for group " + material.name() + "(" + material.getId()
								+ ") item node in " + (isOffensive?"Offensive":"Defensive") + " - is this on purpose?");
						}
						if(ModDamage.consoleDebugging_normal)
							worldHandler.log.info("-" + worldHandler.getWorld().getName() + ":" 
									+ ":" + groupName + (isOffensive?"Offensive":"Defensive") + ":" 
									+ material.name() + "(" + material.getId() + ") "
									+ calcStrings.toString());//debugging
						return true;
					}
				}
		}
		return false;
	}
	
	public boolean loadPVPRoutines(boolean isOffensive, boolean force)
	{
		//get all of the groups in configuration
		worldHandler.log.info("LOADING PVP ROUTINES FOR " + (isOffensive?"OFFENSIVE":"DEFENSIVE" + " FOR GROUP " + groupName));
		List<String> groups = (isOffensive?offensiveNode:defensiveNode).getKeys("groups");
		
		//load groups with offensive and defensive settings first
		if(groups != null)
			for(String group : groups)
			{
				List<String> calcStrings = (isOffensive?offensiveNode:defensiveNode).getNode("groups").getStringList(group, null);
				if(!calcStrings.equals(null))
					for(String calcString : calcStrings)
						if(!damageCalc.checkCommandString(calcString))
						{
							worldHandler.log.severe("Invalid command string \"" + calcString + "\" for group " + groupName 
								+ " in " + (isOffensive?"Offensive":"Defensive") + " \"" + group
								+ "\" PvP definition - refer to config for proper calculation node");
							calcStrings.clear();
						}
				if(calcStrings.size() > 0)
				{
					if(!(isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).containsKey(group))
						(isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).put(group, calcStrings);
					else if(ModDamage.consoleDebugging_normal) 
						worldHandler.log.warning("Repetitive " + group + " definition in " 
								+ (isOffensive?"Offensive":"Defensive") + " settings for group " + groupName + " - ignoring");
				}
				else if(ModDamage.consoleDebugging_verbose)
				{		
					worldHandler.log.warning("No instructions found for group " + groupName + " " + group 
						+ " PvP node in " + (isOffensive?"Offensive":"Defensive") + " for world " 
						+ worldHandler.world.getName() +  " - is this on purpose?");
				}
				if(ModDamage.consoleDebugging_normal)
					worldHandler.log.info("[" + worldHandler.plugin.getDescription().getName() + "] " + worldHandler.world.getName() 
						+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive") + ":PvP:" + group + " " 
						+ (ModDamage.consoleDebugging_verbose?("\n" + calcStrings.toString()):""));//debugging
			}
		return true;
	}
	
	public boolean loadDamageTypeGroup(String damageDescriptor, boolean isOffensive)
	{
		ConfigurationNode relevantNode = (isOffensive
											?offensiveNode.getNode(damageDescriptor)
											:defensiveNode.getNode(damageDescriptor));
			if(relevantNode != null)
			{
				if(ModDamage.consoleDebugging_normal) worldHandler.log.info("{Found group " + (isOffensive?"Offensive":"Defensive") + " " + damageDescriptor + " node}");
				for(DamageType damageType : DamageType.values())
					if(damageType.getDescriptor().equals(damageDescriptor))
					{
						//check for leaf-node buff strings
						List<String> calcStrings = relevantNode.getStringList(damageType.getConfigReference(), null);
						if(!calcStrings.equals(null)) //!calcStrings.equals(null)
						{
							for(String calcString : calcStrings)
								if(!damageCalc.checkCommandString(calcString))
								{
									worldHandler.log.severe("[" + worldHandler.plugin.getDescription().getName() + "] Invalid command string \"" 
											+ calcString + "\" in " + (isOffensive?"Offensive":"Defensive") + " " + damageType.getConfigReference() 
											+ " definition - refer to config for proper calculation node");
									calcStrings.clear();
								}
							if(calcStrings.size() > 0)
							{
								if(ModDamage.consoleDebugging_normal) worldHandler.log.info("-" + worldHandler.world.getName() 
										+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive") 
										+ ":" + damageType.getConfigReference() 
										+ (ModDamage.consoleDebugging_verbose?(" " + calcStrings.toString()):""));//debugging
								if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
									(isOffensive?offensiveRoutines:defensiveRoutines).put(damageType, calcStrings);
								else if(ModDamage.consoleDebugging_normal) worldHandler.log.warning("Repetitive "  + damageType.getConfigReference() 
										+ " definition in " + (isOffensive?"Offensive":"Defensive") + " " 
										+ damageDescriptor + " group settings - ignoring");
							}
							else if(ModDamage.consoleDebugging_verbose)
							{
								worldHandler.log.warning("No instructions found for group setting " + damageType.getConfigReference() 
									+ " node in " + (isOffensive?"Offensive":"Defensive") + " - is this on purpose?");
							}
						}
						else if(ModDamage.consoleDebugging_verbose) worldHandler.log.info("Group " + damageType.getConfigReference() 
								+ " node for" + (isOffensive?"Offensive":"Defensive") + " not found.");
					}
				return true;
			}
			return false;
	}
	
	private int runRoutines(DamageType damageType, boolean isOffensive, int eventDamage)
	{
		int result = 0;
		if(damageType != null && (isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
				for(String calcString : (isOffensive?offensiveRoutines:defensiveRoutines).get(damageType))
					result += damageCalc.parseCommand(calcString, eventDamage, isOffensive);
		return result;
	}
	private int runPVPRoutines(String groupName, boolean isOffensive, int eventDamage)
	{
		int result = 0;
		if(groupName != null && (isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).containsKey(groupName))
				for(String calcString : (isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).get(groupName))
					result += damageCalc.parseCommand(calcString, eventDamage, isOffensive);
		return result;
	}
	
	private int runItemRoutines(Material material, boolean isOffensive, int eventDamage) 
	{
		int result = 0;
		if(material != null && (isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).containsKey(material))
			for(String calcString : (isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).get(material))
				result += damageCalc.parseCommand(calcString, eventDamage, isOffensive);
		return result;
	}
	
	public void useDefaults(){ isLoaded = false;}
	

	private void clearRoutines() 
	{
		offensiveRoutines.clear();
		defensiveRoutines.clear();
		pvpOffensiveRoutines.clear();
		pvpDefensiveRoutines.clear();
		itemOffensiveRoutines.clear();
		itemDefensiveRoutines.clear();
	}

}

	