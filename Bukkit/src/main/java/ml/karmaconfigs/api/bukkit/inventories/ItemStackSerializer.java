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

/*
 * This file is part of KarmaAPI, licensed under the MIT License.
 *
 *  Copyright (c) karma (KarmaDev) <karmaconfigs@gmail.com>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

/**
 * Serialize an array of stacks
 */
public final class ItemStackSerializer {

    private final ItemStack[] items;
    private final File cacheDir;

    /**
     * Initialize the itemstack serializer
     *
     * @param owningPlugin the plugin to separate
     *                     the inventory between plugins
     * @param i the itemstacks
     */
    public ItemStackSerializer(final JavaPlugin owningPlugin, final ItemStack... i) {
        cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + owningPlugin.getDescription().getName());
        items = i;
    }

    /**
     * Initialize the itemstack serializer
     *
     * @param i the itemstacks
     */
    public ItemStackSerializer(final ItemStack... i) {
        cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-KarmaAPI");
        items = i;
    }

    /**
     * Clear the itemstack data
     *
     * @param owner the stack data owner
     */
    private void clear(final UUID owner) throws Throwable {
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
        File cache = new File(cacheDir, name + "_item" + ".yml");

        return cache.exists();
    }
}
