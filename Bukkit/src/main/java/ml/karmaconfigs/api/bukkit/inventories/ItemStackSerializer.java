package ml.karmaconfigs.api.bukkit.inventories;

import ml.karmaconfigs.api.common.utils.FileUtilities;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

/**
 * Serialize items into a file
 */
public final class ItemStackSerializer {

    private final ItemStack[] items;
    private final JavaPlugin main;

    /**
     * Initialize the itemstack serializer
     *
     * @param owningPlugin the plugin to separate
     *                     the inventory between plugins
     * @param i the itemstacks
     */
    public ItemStackSerializer(final JavaPlugin owningPlugin, final ItemStack[] i) {
        main = owningPlugin;
        items = i;
    }

    /**
     * Clear the itemstack data
     *
     * @param owner the stack data owner
     */
    private void clear(final UUID owner) throws Throwable {
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, owner.toString() + "_item.yml");

        if (!cacheDir.exists())
            Files.createDirectories(cacheDir.toPath());

        if (cache.exists()) {
            Files.delete(cache.toPath());
            Files.createFile(cache.toPath());
        }
    }

    /**
     * Clear the itemstack data
     *
     * @param name the stack data name
     */
    private void clear(final String name) throws Throwable {
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, name + "_item.yml");

        if (!cacheDir.exists())
            Files.createDirectories(cacheDir.toPath());

        if (cache.exists()) {
            Files.delete(cache.toPath());
            Files.createFile(cache.toPath());
        }
    }

    /**
     * Create and serialize the itemstack in
     * the specified file
     *
     * @param owner the owner uuid
     * @throws Throwable is something goes wrong
     * while saving
     */
    public final void save(final UUID owner) throws Throwable {
        clear(owner);

        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, owner.toString() + "_item.yml");

        YamlConfiguration cacheYaml = YamlConfiguration.loadConfiguration(cache);

        /*if (!cacheDir.exists() && cacheDir.mkdirs()) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.toColor("&e[SYSTEM] &7| &bCreated directory " + FileUtilities.getPath(cacheDir)));
        }
        if (!cache.exists() && cache.createNewFile()) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.toColor("&e[SYSTEM] &7| &bCreated file " + FileUtilities.getPath(cache)));
        }*/

        cacheYaml.options().header("Do not modify this file unless you know\nwhat you are doing");
        cacheYaml.options().copyHeader(true);

        ConfigurationSection stacks = cacheYaml.getConfigurationSection("ItemStack");

        int stacks_amount = 0;
        if (stacks != null)
            stacks_amount = stacks.getKeys(false).size();

        //Add one to the max stacks amount
        //saved in file so a new one can be
        //added
        stacks_amount++;

        for (ItemStack item : items) {
            if (item != null) {
                for (int i = 0; i < stacks_amount; i++) {
                    Map<String, Object> serialized = item.serialize();

                    for (String str : serialized.keySet()) {
                        cacheYaml.set("ItemStack." + i + "." + str, serialized.get(str));
                    }
                }
            }
        }

        cacheYaml.save(cache);
    }

    /**
     * Create and serialize the itemstack in
     * the specified file
     *
     * @param name the inventory name
     * @throws Throwable if something goes wrong while saving
     */
    public final void save(final String name) throws Throwable {
        clear(name);

        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, name + "_item.yml");

        YamlConfiguration cacheYaml = YamlConfiguration.loadConfiguration(cache);

        /*if (!cacheDir.exists() && cacheDir.mkdirs()) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.toColor("&e[SYSTEM] &7| &bCreated directory " + FileUtilities.getPath(cacheDir)));
        }
        if (!cache.exists() && cache.createNewFile()) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.toColor("&e[SYSTEM] &7| &bCreated file " + FileUtilities.getPath(cache)));
        }*/

        cacheYaml.options().header("Do not modify this file unless you know\nwhat you are doing");
        cacheYaml.options().copyHeader(true);

        ConfigurationSection stacks = cacheYaml.getConfigurationSection("ItemStack");

        int stacks_amount = 0;
        if (stacks != null)
            stacks_amount = stacks.getKeys(false).size();

        //Add one to the max stacks amount
        //saved in file so a new one can be
        //added
        stacks_amount++;

        for (ItemStack item : items) {
            if (item != null) {
                for (int i = 0; i < stacks_amount; i++) {
                    Map<String, Object> serialized = item.serialize();

                    for (String str : serialized.keySet()) {
                        cacheYaml.set("ItemStack." + i + "." + str, serialized.get(str));
                    }
                }
            }
        }

        cacheYaml.save(cache);
    }

    /**
     * Check if the player item stack data exists
     *
     * @param owner the uuid of the item stack data owner
     * @return if the item stack data file exists
     */
    public final boolean exists(final UUID owner) {
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, owner.toString() + "_item" + ".yml");

        return cache.exists();
    }

    /**
     * Check if the player item stack data exists
     *
     * @param name the name if the item stack data owner
     * @return if the item stack data file exists
     */
    public final boolean exists(final String name) {
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, name + "_item" + ".yml");

        return cache.exists();
    }
}
