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

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class ItemStackDeserializer {

    private final JavaPlugin main;

    /**
     * Initialize the inventory deserializer
     *
     * @param owningPlugin the plugin to separate
     *                     the inventory between plugins
     */
    public ItemStackDeserializer(final JavaPlugin owningPlugin) {
        main = owningPlugin;
    }

    /**
     * Get the stored item stacks from file
     *
     * @param uuid the item stack data owner uuid
     * @return an array of item stacks
     */
    public ItemStack[] getItems(final UUID uuid) {
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
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
        File cacheDir = new File(FileUtilities.getServerFolder() + "/cache/KarmaAPI/inventories-" + main.getDescription().getName());
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
