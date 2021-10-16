package ml.karmaconfigs.api.bukkit.serializer.util;

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

import ml.karmaconfigs.api.bukkit.server.VersionUtils;
import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Serialized inventory
 */
public final class SerializedInventory {

    private final String name;
    private final String itemsVersion;
    private final int slotSize;
    private final int itemSize;
    private final Set<Map<String, Object>> items = new LinkedHashSet<>();

    /**
     * Initialize a serialized inventory
     *
     * @param inventoryLocalizer the inventory name
     * @param inventory the inventory to serialize
     */
    public SerializedInventory(final String inventoryLocalizer, final Inventory inventory) {
        name = StringUtils.stripColor(inventoryLocalizer).replaceAll("\\s", "_").toLowerCase();
        itemsVersion = VersionUtils.newInstance().version().name();
        slotSize = inventory.getSize();
        boolean legacy = true;

        try {
            Class<?> inventoryClass = inventory.getClass();
            inventoryClass.getMethod("getMaxStackSize");
            legacy = false;
        } catch (Throwable ignored) {}

        if (legacy)
            itemSize = 64;
        else
            itemSize = inventory.getMaxStackSize();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && !item.getType().equals(Material.AIR)) {
                items.add(item.serialize());
            }
        }
    }

    /**
     * Get the inventory unique id
     *
     * @return the inventory unique id
     */
    public UUID getUniqueId() {
        return UUID.nameUUIDFromBytes(("Inventory:" + name).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Get the inventory name
     *
     * @return the inventory name
     */
    public String getLocalizer() {
        return name;
    }

    /**
     * Get the inventory size
     *
     * @return the inventory size
     */
    public int getInventorySize() {
        return slotSize;
    }

    /**
     * Get the inventory item stack size
     *
     * @return the inventory item stack size
     */
    public int getItemStackSize() {
        return itemSize;
    }

    /**
     * Get the inventory items
     *
     * @return the inventory items
     */
    public ItemStack[] getItems() {
        int failed = 0;

        Set<ItemStack> loaded = new LinkedHashSet<>();
        for (Map<String, Object> item : items) {
            try {
                ItemStack stack = ItemStack.deserialize(item);
                if (!stack.getType().equals(Material.AIR)) {
                    loaded.add(stack);
                }
            } catch (Throwable ex) {
                failed++;
            }
        }

        if (failed > 0) {
            APISource.getConsole().send(
                    "Failed to load {0} items, maybe they were null, air, or not valid for this server version ( {1} => {2} )", Level.GRAVE,
                    failed,
                    itemsVersion,
                    VersionUtils.newInstance().version().name());
        }

        return loaded.toArray(new ItemStack[0]);
    }
}
