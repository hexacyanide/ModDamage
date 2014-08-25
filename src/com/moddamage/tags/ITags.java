package com.moddamage.tags;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Entity;

@SuppressWarnings("rawtypes")
public interface ITags<T, O> {
    void addTag(O obj, String tag, T tagValue);
    boolean isTagged(O obj, String tag);
    List<String> getTags(O obj);
    Map<O, T> getAllTagged(String tag);
    T getTagValue(O obj, String tag);
    void removeTag(O obj, String tag);
    void clear();

    void load(Map tagMap, Map<UUID, Entity> entities);
    Map save(Set<Entity> entities);
}
