package ml.karmaconfigs.api.bukkit.inventories;

import ml.karmaconfigs.api.common.utils.FileUtilities;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
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
 * Deserialize a previously serialized
 * array of stacks
 */
public final class ItemStackDeserializer {

    private final File cacheDir;

    /**
     * Initialize the inventory deserializer
     *
     * @param owningPlugin the plugin to separate
     *                     the inventory between plugins
     */
    public ItemStackDeserializer(final JavaPlugin owningPlugin) {
        cacheDir = new File(FileUtilities.getServerFolder() + File.separator + "cache" + File.separator + "KarmaAPI" + File.separator + "inventories-" + owningPlugin.getDescription().getName());
    }

    public ItemStackDeserializer() {
        cacheDir = new File(FileUtilities.getServerFolder() + File.separator + "cache" + File.separator + "KarmaAPI" + File.separator + "inventories-KarmaAPI");
    }

    /**
     * Get the stored item stacks from file
     *
     * @param uuid the item stack data owner uuid
     * @return an array of item stacks
     */
    public ItemStack[] getItems(final UUID uuid) {
        File cache = new File(cacheDir, uuid.toString() + "_item.yml");

        if (cache.exists()) {
            YamlConfiguration cacheYaml = YamlConfiguration.loadConfiguration(cache);

            ConfigurationSection stacks = cacheYaml.getConfigurationSection("ItemStack");

            if (stacks != null) {
                ItemStack[] final_stacks = new ItemStack[stacks.getKeys(false).size()];
                int stack_id = 0;
                for (String str : stacks.getKeys(false)) {
                    String generated_path = "ItemStack." + str;
                    if (cacheYaml.get(generated_path) instanceof ConfigurationSection) {
                        ConfigurationSection generated_section = cacheYaml.getConfigurationSection(generated_path);

                        if (generated_section != null) {
                            Map<String, Object> serialized = new HashMap<>();

                            for (String str_sub : generated_section.getKeys(false)) {
                                Object value = cacheYaml.get("ItemStack." + str + "." + str_sub);

                                serialized.put(str_sub, value);
                            }

                            ItemStack stack = ItemStack.deserialize(serialized);
                            final_stacks[stack_id] = stack;
                        }
                    }

                    stack_id++;
                }

                return final_stacks;
            }
        }

        return new ItemStack[]{};
    }

    /**
     * Get the stored item stacks from file
     *
     * @param name the item stack data owner name
     * @return an array of item stacks
     */
    public ItemStack[] getItems(final String name) {
        File cache = new File(cacheDir, name + "_item.yml");

        if (cache.exists()) {
            YamlConfiguration cacheYaml = YamlConfiguration.loadConfiguration(cache);

            ConfigurationSection stacks = cacheYaml.getConfigurationSection("ItemStack");

            if (stacks != null) {
                ItemStack[] final_stacks = new ItemStack[stacks.getKeys(false).size()];
                int stack_id = 0;
                for (String str : stacks.getKeys(false)) {
                    String generated_path = "ItemStack." + str;
                    if (cacheYaml.get(generated_path) instanceof ConfigurationSection) {
                        ConfigurationSection generated_section = cacheYaml.getConfigurationSection(generated_path);

                        if (generated_section != null) {
                            Map<String, Object> serialized = new HashMap<>();

                            for (String str_sub : generated_section.getKeys(false)) {
                                Object value = cacheYaml.get("ItemStack." + str + "." + str_sub);

                                serialized.put(str_sub, value);
                            }

                            ItemStack stack = ItemStack.deserialize(serialized);
                            final_stacks[stack_id] = stack;
                        }
                    }

                    stack_id++;
                }

                return final_stacks;
            }
        }

        return new ItemStack[]{};
    }

    /**
     * Check if the player inventory exists
     *
     * @param owner the uuid of the inventory owner
     * @return if the inventory file exists
     */
    public final boolean exists(final UUID owner) {
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
        File cache = new File(cacheDir, name + ".yml");

        return cache.exists();
    }
}
