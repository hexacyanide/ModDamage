package com.moddamage.backend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.moddamage.magic.MagicStuff;

public class ItemHolder {
    private ItemStack item;

    public ItemHolder(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    protected void setItem(ItemStack item) {
        this.item = item;

    	save();
    }

    public Material getType() {
    	if (item == null) return null;
    	else return item.getType();
    }

    public void setType(Material material) {
    	if (item == null) item = new ItemStack(material);
    	else item.setType(material);

    	save();
    }

    @SuppressWarnings("deprecation")
    public int getTypeId() {
    	if (item == null) return 0;
    	else return item.getTypeId();
    }

    @SuppressWarnings("deprecation")
    public void setTypeId(int type) {
    	if (item == null) item = new ItemStack(type);
    	else item.setTypeId(type);

    	save();
    }

    @SuppressWarnings("deprecation")
    public byte getData() {
    	if (item == null) return 0;
    	else return item.getData().getData();
    }

    @SuppressWarnings("deprecation")
    public void setData(byte data) {
    	if (item == null) item = new ItemStack(Material.STONE);
        item.getData().setData(data);

    	save();
    }

    public short getDurability() {
    	if (item == null) return 0;
       return item.getDurability();
    }

    public void setDurability(short durability) {
    	if (item == null) item = new ItemStack(Material.STONE);
        item.setDurability(durability);

    	save();
    }

    public int getMaxDurability() {
    	if (item == null) return 0;
        return MagicStuff.getMaxDurability(item);
    }

    public int getAmount() {
    	if (item == null) return 0;
        return item.getAmount();
    }

    public void setAmount(int amount) {
    	if (item == null) item = new ItemStack(Material.STONE);
        item.setAmount(amount);

    	save();
    }

    public int getMaxStackSize() {
    	if (item == null) return 0;
        return item.getMaxStackSize();
    }

    public boolean isEnchanted() {
    	if (item == null) return false;
    	return !item.getEnchantments().isEmpty();
    }

    public int getEnchantmentLevel(Enchantment enchantment) {
    	if (item == null) return 0;
        return item.getEnchantmentLevel(enchantment);
    }

    public void setEnchantmentLevel(Enchantment enchantment, int level) {
    	if (item == null) item = new ItemStack(Material.STONE);
        item.addUnsafeEnchantment(enchantment, level);

    	save();
    }

    public void clearEnchantments() {
    	if (item == null) return;
        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            item.removeEnchantment(enchantment);
        }

    	save();
    }

    /// Meta info ///

    public String getName() {
    	if (item == null) return null;
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null) return null;

    	return meta.getDisplayName();
    }

    public void setName(String name) {
    	if (item == null) item = new ItemStack(Material.STONE);
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null) return;

    	meta.setDisplayName(name);
    	item.setItemMeta(meta);

    	save();
    }

    public String getLore(int index) {
    	if (item == null) return null;
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null) return null;
    	List<String> lore = meta.getLore();
    	if (lore == null || index < 0 || index >= lore.size()) return null;

    	return lore.get(index);
    }

    public void setLore(int index, String text) {
    	if (item == null) item = new ItemStack(Material.STONE);
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null) return;

    	List<String> lore = meta.getLore();
    	if (lore == null) lore = new ArrayList<String>(1);
    	if (index < 0 || index > lore.size()) return;

    	if (text == null || text.equals("")) {
    		if (index >= 0 && index < lore.size())
    			lore.remove(index);
    	} else if (index == lore.size())
    		lore.add(text);
    	else
    		lore.set(index, text);

    	meta.setLore(lore);
    	item.setItemMeta(meta);

    	save();
    }


    public String getOwner() {
    	if (item == null) return null;
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null || !(meta instanceof SkullMeta)) return null;

    	SkullMeta smeta = (SkullMeta) meta;

    	// TODO(esu): Review the return type of this method.
    	return smeta.getOwningPlayer().toString();
    }

    // TODO(esu): Review usage and input types.
    @SuppressWarnings("deprecated")
    public void setOwner(String name) {
    	if (item == null) return;
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null || !(meta instanceof SkullMeta)) return;

    	SkullMeta smeta = (SkullMeta) meta;

    	OfflinePlayer player = Bukkit.getOfflinePlayer(name);
    	if (player == null) return;

    	smeta.setOwningPlayer(player);
    	item.setItemMeta(smeta);

    	save();
    }


    public void save() {}

    @Override
    public String toString() {
    	if (item == null)
    		return "none";
    	return item.toString();
    }
}
