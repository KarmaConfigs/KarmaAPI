package ml.karmaconfigs.api.bukkit.inventories;

import ml.karmaconfigs.api.common.utils.FileUtilities;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class InventorySerializer {

    private final Inventory inventory;
    private final JavaPlugin main;

    /**
     * Initialize the itemstack serializer
     *
     * @param owningPlugin the plugin to separate
     *                     the inventory between plugins
     * @param i the inventory to get
     *          itemstacks from
     */
    public InventorySerializer(final JavaPlugin owningPlugin, final Inventory i) {
        main = owningPlugin;
        inventory = i;
    }

    /**
     * Clear the inventory data
     *
     * @param owner the inventory data owner
     */
    private void clear(final UUID owner) throws Throwable {
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, owner.toString() + ".yml");

        if (!cacheDir.exists())
            Files.createDirectories(cacheDir.toPath());

        if (cache.exists()) {
            Files.delete(cache.toPath());
            Files.createFile(cache.toPath());
        }
    }

    /**
     * Clear the inventory data
     *
     * @param owner the inventory data owner
     */
    private void clear(final String owner) throws Throwable {
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, owner + ".yml");

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
     * @param owner        the owner uuid
     * @throws Throwable if something goes wrong while saving
     */
    public final void save(final UUID owner) throws Throwable {
        clear(owner);

        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, owner.toString() + ".yml");

        YamlConfiguration cacheYaml = YamlConfiguration.loadConfiguration(cache);

        /*if (!cacheDir.exists() && cacheDir.mkdirs()) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.toColor("&e[SYSTEM] &7| &bCreated directory " + FileUtilities.getPath(cacheDir)));
        }
        if (!cache.exists() && cache.createNewFile()) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.toColor("&e[SYSTEM] &7| &bCreated file " + FileUtilities.getPath(cache)));
        }*/

        cacheYaml.options().header("Do not modify this file unless you know\nwhat you are doing");
        cacheYaml.options().copyHeader(true);

        int size = inventory.getSize();
        if (inventory instanceof PlayerInventory) {
            size = 45;
        }

        cacheYaml.set("Size", size);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);

            if (stack != null) {
                Map<String, Object> serialized = stack.serialize();
                for (String str : serialized.keySet()) {
                    cacheYaml.set("Inventory." + i + "." + str, serialized.get(str));
                }
            }
        }

        cacheYaml.save(cache);
    }

    /**
     * Create and serialize the itemstack in
     * the specified file
     *
     * @param name         the inventory name
     * @throws Throwable if something goes wrong while saving
     */
    public final void save(final String name) throws Throwable {
        clear(name);
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, name + ".yml");

        YamlConfiguration cacheYaml = YamlConfiguration.loadConfiguration(cache);

        /*if (!cacheDir.exists() && cacheDir.mkdirs()) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.toColor("&e[SYSTEM] &7| &bCreated directory " + FileUtilities.getPath(cacheDir)));
        }
        if (!cache.exists() && cache.createNewFile()) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.toColor("&e[SYSTEM] &7| &bCreated file " + FileUtilities.getPath(cache)));
        }*/

        cacheYaml.options().header("Do not modify this file unless you know\nwhat you are doing");
        cacheYaml.options().copyHeader(true);

        int size = inventory.getSize();
        if (inventory instanceof PlayerInventory) {
            size = 45;
        }

        cacheYaml.set("Size", size);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);

            if (stack != null) {
                Map<String, Object> serialized = stack.serialize();
                for (String str : serialized.keySet()) {
                    cacheYaml.set("Inventory." + i + "." + str, serialized.get(str));
                }
            }
        }

        cacheYaml.save(cache);
    }

    /**
     * Check if the player inventory exists
     *
     * @param owner the uuid of the inventory owner
     * @return if the inventory file exists
     */
    public final boolean exists(final UUID owner) {
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, owner.toString() + ".yml");

        return cache.exists();
    }

    /**
     * Check if the inventory with the specified name
     * exists
     *
     * @param name the inventory name
     * @return if the inventory file exists
     */
    public final boolean exists(final String name) {
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
        File cache = new File(cacheDir, name + ".yml");

        return cache.exists();
    }
}
