package com.moddamage.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import com.moddamage.LogUtil;
import com.moddamage.ModDamage;

public class PlayerTags<T> implements ITags<T, OfflinePlayer> {
    private final Map<String, Map<UUID, T>> tags = new HashMap<>();
    private final int UUID_LENGTH = 36;

    public PlayerTags() { }

    @Override
    public void addTag(OfflinePlayer player, String tag, T tagValue) {
        // mark tags collection as dirty
        ModDamage.getTagger().dirty();

        Map<UUID, T> tags = this.tags.get(tag);
        if (tags == null) {
            tags = new HashMap<UUID, T>();
            this.tags.put(tag, tags);
        }

        tags.put(player.getUniqueId(), tagValue);
    }

    @Override
    public boolean isTagged(OfflinePlayer player, String tag) {
        Map<UUID, T> tags = this.tags.get(tag);
        return tags != null && tags.containsKey(player.getUniqueId());
    }

    @Override
    public List<String> getTags(OfflinePlayer player) {
        List<String> tagsList = new ArrayList<String>();
        for (Map.Entry<String, Map<UUID, T>> entry : tags.entrySet())
            if (entry.getValue().containsKey(player.getUniqueId()))
                tagsList.add(entry.getKey());
        return tagsList;
    }

    @Override
    public Map<OfflinePlayer, T> getAllTagged(String tag) {
        Map<UUID, T> players = tags.get(tag);

        if (players == null) return null;

        // populate local player cache for speed
        Bukkit.getOfflinePlayers();

        Map<OfflinePlayer, T> map = new HashMap<OfflinePlayer, T>(players.size());

        for (Entry<UUID, T> entry : players.entrySet()) {
            map.put(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue());
        }

        return map;
    }

    @Override
    public T getTagValue(OfflinePlayer player, String tag) {
        Map<UUID, T> tags = this.tags.get(tag);
        if (tags == null) return null;

        return tags.get(player.getUniqueId());
    }

    @Override
    public void removeTag(OfflinePlayer player, String tag) {
        if (tags.containsKey(tag))
            tags.get(tag).remove(player.getUniqueId());
    }

    @Override
    public void clear() {
        tags.clear();
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    @Override
    public void load(Map tagMap, Map<UUID, Entity> entities) {
        Map<String, Map<String, T>> inputMap = tagMap;

        // if there is no player data, do nothing
        if (inputMap == null) return;

        // go through each tag and organize player to tag value relations
        for (Entry<String, Map<String, T>> tagEntry : inputMap.entrySet()) {

            String tagName = tagEntry.getKey();
            Map<String, T> tagData = tagEntry.getValue();
            Map<UUID, T> playerMap = new HashMap<>(tagData.size());

            for (Entry<String, T> playerEntry : tagData.entrySet()) {
                // fetch the username or UUID
                String label = playerEntry.getKey();
                T t = playerEntry.getValue();

                // determine whether a conversion is required
                boolean converted = (label.length() == UUID_LENGTH);

                UUID uuid;

                // either fetch the UUID from username, or parse the string
                if (!converted) {
                    uuid = Bukkit.getOfflinePlayer(label).getUniqueId();
                    LogUtil.info("Performing tagfile UUID conversion for " + uuid.toString() + " (" + label + ")");
                } else {
                    uuid = UUID.fromString(label);
                }

                playerMap.put(uuid, t);
            }

            this.tags.put(tagName, playerMap);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map save(Set<Entity> entities) {
        Map<String, Map<String, T>> playerMap = new HashMap<>();

        for (Entry<String, Map<UUID, T>> tagEntry : this.tags.entrySet()) {
            HashMap<String, T> savedPlayers = new HashMap<String, T>();

            if (tagEntry.getValue().isEmpty()) continue;

            for (Entry<UUID, T> entry : tagEntry.getValue().entrySet())
                savedPlayers.put(entry.getKey().toString(), entry.getValue());

            if (!savedPlayers.isEmpty())
                playerMap.put(tagEntry.getKey(), savedPlayers);

        }

        return playerMap;
    }
}
