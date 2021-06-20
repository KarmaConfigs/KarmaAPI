package ml.karmaconfigs.api.bukkit.inventories;

import ml.karmaconfigs.api.common.utils.FileUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
 * Deserialize a previously serialized
 * inventory
 */
public final class InventoryDeserializer {

    private final JavaPlugin main;

    /**
     * Initialize the inventory deserializer
     *
     * @param owningPlugin the plugin to separate
     *                     the inventory between plugins
     */
    public InventoryDeserializer(final JavaPlugin owningPlugin) {
        main = owningPlugin;
    }

    /**
     * Get the inventory from the file
     *
     * @param uuid the inventory owner uuid
     * @return an inventory
     */
    @Nullable
    public Inventory getInventory(final UUID uuid) {
        try {
            File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
            File cache = new File(cacheDir, uuid.toString() + ".yml");

            if (cache.exists()) {
                YamlConfiguration cacheYaml = YamlConfiguration.loadConfiguration(cache);

                Inventory inventory = Bukkit.createInventory(null, cacheYaml.getInt("Size"));

                for (String str : Objects.requireNonNull(cacheYaml.getConfigurationSection("Inventory")).getKeys(false)) {
                    if (cacheYaml.get("Inventory." + str) instanceof ConfigurationSection) {
                        int slot = Integer.parseInt(str);

                        Map<String, Object> serialized = new HashMap<>();

                        for (String str_sub : Objects.requireNonNull(cacheYaml.getConfigurationSection("Inventory." + str)).getKeys(false)) {
                            Object value = cacheYaml.get("Inventory." + str + "." + str_sub);

                            serialized.put(str_sub, value);
                        }

                        ItemStack stack = ItemStack.deserialize(serialized);
                        inventory.setItem(slot, stack);
                    }
                }

                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack stack = inventory.getItem(i);

                    if (stack == null)
                        inventory.setItem(i, new ItemStack(Material.AIR));
                }

                return inventory;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Get the inventory from the file
     *
     * @param name the inventory owner name
     * @return an inventory
     */
    @Nullable
    public Inventory getInventory(final String name) {
        try {
            File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
            File cache = new File(cacheDir,name + ".yml");

            if (cache.exists()) {
                YamlConfiguration cacheYaml = YamlConfiguration.loadConfiguration(cache);

                Inventory inventory = Bukkit.createInventory(null, cacheYaml.getInt("Size"));

                for (String str : Objects.requireNonNull(cacheYaml.getConfigurationSection("Inventory")).getKeys(false)) {
                    if (cacheYaml.get("Inventory." + str) instanceof ConfigurationSection) {
                        int slot = Integer.parseInt(str);

                        Map<String, Object> serialized = new HashMap<>();

                        for (String str_sub : Objects.requireNonNull(cacheYaml.getConfigurationSection("Inventory." + str)).getKeys(false)) {
                            Object value = cacheYaml.get("Inventory." + str + "." + str_sub);

                            serialized.put(str_sub, value);
                        }

                        ItemStack stack = ItemStack.deserialize(serialized);
                        inventory.setItem(slot, stack);
                    }
                }

                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack stack = inventory.getItem(i);

                    if (stack == null)
                        inventory.setItem(i, new ItemStack(Material.AIR));
                }

                return inventory;
            }
        } catch (Throwable ignored) {
        }
        return null;
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
