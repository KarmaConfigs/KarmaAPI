package ml.karmaconfigs.api.bukkit.reflections.hologram;

import ml.karmaconfigs.api.bukkit.SerializableLocation;
import ml.karmaconfigs.api.bukkit.inventories.ItemStackDeserializer;
import ml.karmaconfigs.api.bukkit.inventories.ItemStackSerializer;
import ml.karmaconfigs.api.bukkit.reflections.hologram.configuration.HologramConfiguration;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karmafile.KarmaFile;
import ml.karmaconfigs.api.common.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
 * Temporal karma hologram, this won't use any
 * file to be stored, once you remove this, you
 * must create it again
 */
public final class PersistentHologram extends KarmaHologram implements Serializable {

    private final KarmaFile data;
    private final String name;

    private final static Map<String, KarmaHologram> temp = new ConcurrentHashMap<>();

    /**
     * Initialize the persistent hologram
     *
     * @param source the hologram source
     * @param holoName the hologram name
     */
    public PersistentHologram(final KarmaSource source, final String holoName) {
        name = holoName;
        data = new KarmaFile(source, holoName + ".holo", "holograms");

        SerializableLocation location = null;
        if (data.exists()) {
            String serialized = data.getString("LOCATION", "");
            if (!StringUtils.isNullOrEmpty(serialized)) {
                location = StringUtils.loadUnsafe(serialized);
            }
        }

        if (location != null) {
            if (!temp.containsKey(name)) {
                temp.put(name, new TempHologram(location.toLocation()));
            }
        } else {
            temp.put(name, new TempHologram(new Location(Bukkit.getWorlds().get(0), 0, 0, 0)));
        }
    }

    /**
     * Create the hologram file
     * <p>
     * In temporal hologram, this does
     * literally nothing
     */
    @Override
    public void create() {
        data.create();
    }

    /**
     * Spawn the hologram
     */
    @Override
    public void spawn() {
        getTempAttachment().spawn();
    }

    /**
     * Spawn the hologram
     *
     * @param location the spawn location
     */
    @Override
    public void spawn(final Location location) {
        SerializableLocation serializable = new SerializableLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.getWorld());
        data.set("LOCATION", StringUtils.serialize(serializable));

        getTempAttachment().spawn(location);
    }

    /**
     * Teleport the hologram to the specified location,
     * instead of having to spawn it again
     *
     * @param newLocation the new hologram location
     */
    @Override
    public void teleport(final Location newLocation) {
        SerializableLocation location = new SerializableLocation(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch(), newLocation.getWorld());
        data.set("LOCATION", StringUtils.serialize(location));

        getTempAttachment().teleport(newLocation);
    }

    /**
     * Hide the armor stand for the specified players
     *
     * @param players the player to hide the armor
     *                stand to
     */
    @Override
    public void hide(final Player... players) {
        getTempAttachment().hide(players);
    }

    /**
     * Hide the armor stand for the specified players
     *
     * @param players the player to hide the armor
     *                stand to
     */
    @Override
    public void show(final Player... players) {
        getTempAttachment().show(players);
    }

    /**
     * Set the hologram visibility
     *
     * @param status the hologram visibility
     */
    @Override
    public void setVisible(final boolean status) {
        getTempAttachment().setVisible(status);
    }

    /**
     * Clear all the hologram lines
     */
    @Override
    public void clearLines() {
        getTempAttachment().clearLines();
        data.set("LINES", Collections.emptyList());
    }

    /**
     * Update the lines text
     */
    @Override
    public void updateLines() {
        getTempAttachment().updateLines();
    }

    /**
     * Add a new line to the hologram
     *
     * @param line the new line text
     */
    @Override
    public void add(final String line) {
        getTempAttachment().add(line);
        List<String> lines = data.getStringList("LINES");
        lines.add(line);

        data.set("LINES", lines);
    }

    /**
     * Insert an item stack between two lines
     *
     * @param item the item to add
     */
    @Override
    public void add(final ItemStack item) {
        getTempAttachment().add(item);

        try {
            List<String> lines = data.getStringList("LINES");
            lines.add("ITEM_ON_LINE:" + lines.size());

            ItemStackSerializer serializer = new ItemStackSerializer(item);
            String name = StringUtils.randomString(24, StringUtils.StringGen.NUMBERS_AND_LETTERS, StringUtils.StringType.RANDOM_SIZE);

            serializer.save(name);
            data.set(String.valueOf(lines.size()), name);

            data.set("LINES", lines);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Insert a line or image read from the specified
     * file
     *
     * @param file the file to read from
     */
    @Override
    public void add(final File file) {
        //Nothing for now
    }

    /**
     * Remove an hologram line
     *
     * @param index the line number to remove
     */
    @Override
    public void remove(final int index) {
        getTempAttachment().remove(index);
        List<String> lines = data.getStringList("LINES");
        lines.remove(index);

        data.set("LINES", lines);
        if (data.isSet(String.valueOf(index))) {
            data.unset(String.valueOf(index));
        }
    }

    /**
     * Destroy the hologram, completely
     */
    @Override
    public void destroy() {
        getTempAttachment().destroy();
        data.delete();
    }

    /**
     * Get this persistent hologram with the specified configuration
     *
     * @param config the hologram configuration
     * @return this instance with the new configuration
     */
    @Override
    public KarmaHologram withConfiguration(final HologramConfiguration config) {
        temp.put(name, getTempAttachment().withConfiguration(config));
        return this;
    }

    /**
     * Get the line from an index
     *
     * @param index the index
     * @return the line
     */
    @Override
    public String getLine(final int index) {
        return getTempAttachment().getLine(index);
    }

    /**
     * Get an item from an index
     *
     * @param index the index
     * @return the item
     */
    @Override
    public ItemStack getItem(final int index) {
        try {
            ItemStackDeserializer deserializer = new ItemStackDeserializer();
            String result = data.getString(String.valueOf(index), "");

            return deserializer.getItems(result)[0];
        } catch (Throwable ex) {
            return getTempAttachment().getItem(index);
        }
    }

    /**
     * Get the index from a line
     *
     * @param line the line
     * @return the line index
     */
    @Override
    public int getIndex(final String line) {
        return getTempAttachment().getIndex(line);
    }

    /**
     * Get the hologram schema
     *
     * @return the hologram schema
     */
    @Override
    public Map<Integer, String> getHologramSchema() {
        return getTempAttachment().getHologramSchema();
    }

    /**
     * Get the serializable location
     *
     * @return the serializable location
     */
    @Override
    public SerializableLocation getLocation() {
        return getTempAttachment().getLocation();
    }

    /**
     * Get if the player can see the hologram
     *
     * @param player the player
     * @return if the player can see the hologram
     */
    @Override
    public boolean canSee(final Player player) {
        return getTempAttachment().canSee(player);
    }

    /**
     * Get if the hologram exists
     *
     * @return if the hologram exists
     * <p>
     * For temporal hologram this will be
     * always false, as the hologram itself
     * doesn't exist in data
     */
    @Override
    public boolean exists() {
        return data.exists();
    }

    /**
     * Get a set of players who the hologram is
     * hidden
     *
     * @return a set of the players with hologram
     * hidden
     */
    @Override
    public Set<OfflinePlayer> getHidden() {
        return getTempAttachment().getHidden();
    }

    /**
     * Get the temp attached hologram
     *
     * @return the temporal attached hologram
     */
    private KarmaHologram getTempAttachment() {
        KarmaHologram result = temp.getOrDefault(name, null);
        if (result == null) {
            result = new TempHologram(new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
            temp.put(name, result);
        }

        return result;
    }
}
