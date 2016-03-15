package com.moddamage.tags;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import com.moddamage.LogUtil;
import com.moddamage.ModDamage;
import com.moddamage.PluginConfiguration.LoadState;

public class TagManager {
    public final TagsHolder<Number> numTags = new TagsHolder<Number>();
    public final TagsHolder<String> stringTags = new TagsHolder<String>();

    private long saveInterval;
    private int saveTaskId;

    public final File file;
    public final File newFile;
    public final File oldFile;

    private InputStream reader = null;
    private FileWriter writer = null;
    private Yaml yaml = new Yaml();
    private boolean dirty = false;

    public TagManager(File file, long saveInterval) {
        this.saveInterval = saveInterval;
        this.file = file;
        this.newFile = new File(file.getParent(), file.getName() + ".new");
        this.oldFile = new File(file.getParent(), file.getName() + ".old");

        this.load();
        this.reload(false);
    }

    public void reload() {
        this.reload(true);
    }

    private void reload(boolean initialized) {
        this.save();

        if (initialized && (this.file != null) && (this.saveTaskId != 0)) {
            Bukkit.getScheduler().cancelTask(saveTaskId);
        }

        Plugin modDamage = Bukkit.getPluginManager().getPlugin("ModDamage");
        this.saveTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(modDamage, new Runnable() {
            @Override
            public void run() {
                save();
            }
        }, saveInterval, saveInterval);
    }

    public void dirty() {
        dirty = true;
    }

    @SuppressWarnings("unchecked")
    public void load() {
        try {
            if (!this.file.exists()) {
                LogUtil.info("No tags file found at " + file.getAbsolutePath() + ", generating a new one...");

                if (!this.file.createNewFile()) {
                    LogUtil.error("Tag file creation failed! Tags will not be persistence between reloads.");
                    return;
                }
            }

            this.reader = new FileInputStream(file);
            Object tagFileObject = yaml.load(this.reader);
            this.reader.close();

            if (tagFileObject == null || !(tagFileObject instanceof Map)) return;

            Map<UUID, Entity> entities = new HashMap<UUID, Entity>();
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities())
                    if (!(entity instanceof OfflinePlayer))
                        entities.put(entity.getUniqueId(), entity);
            }

            Map<String, Object> tagMap = (Map<String, Object>) tagFileObject;

            // legacy tag storage file format
            if (!tagMap.containsKey("string")) {
                this.numTags.loadTags(tagMap, entities);
                // upgrade the file by forced save
                this.save(true);
            }

            // modern tagfile format
            else {
                if (tagMap.containsKey("int")) {
                    this.numTags.loadTags((Map<String, Object>) tagMap.get("int"), entities);
                } else {
                    this.numTags.loadTags((Map<String, Object>) tagMap.get("num"), entities);
                }

                this.stringTags.loadTags((Map<String, Object>) tagMap.get("string"), entities);

                this.save(true);
            }
        } catch (Exception e) {
            LogUtil.error("Error loading tags:");
            e.printStackTrace();
        }
    }

    public void save() {
        this.save(false);
    }


    /**
     * Saves all tags to a file.
     */
    private void save(boolean force) {
        if (this.file != null && (this.dirty || force)) {
            Set<Entity> entities = new HashSet<Entity>();
            for (World world : Bukkit.getWorlds())
                entities.addAll(world.getEntities());

            Map<String, Object> saveMap = new HashMap<String, Object>();

            saveMap.put("tagsVersion", 2);
            saveMap.put("num", numTags.saveTags(entities));
            saveMap.put("string", stringTags.saveTags(entities));

            try {
                writer = new FileWriter(newFile);
                writer.write(yaml.dump(saveMap));
                writer.close();
            } catch (IOException e) {
                ModDamage.printToLog(Level.WARNING, "Error saving tags at " + newFile.getAbsolutePath() + "!");
                return;
            }

            this.oldFile.delete();
            this.file.renameTo(oldFile);
            this.newFile.renameTo(file);
        }
    }

    /**
     * This is used in the ModDamage main to finish any file IO.
     */
    public void close() {
        this.save();
    }

    /**
     * @return LoadState reflecting the file's load state.
     */
    public LoadState getLoadState() {
        return (file != null) ? LoadState.SUCCESS : LoadState.NOT_LOADED;
    }

    public void clear() {
        this.numTags.clear();
        this.stringTags.clear();
    }
}
