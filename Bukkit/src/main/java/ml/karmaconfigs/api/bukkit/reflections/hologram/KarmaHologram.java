package ml.karmaconfigs.api.bukkit.reflections.hologram;

import ml.karmaconfigs.api.bukkit.SerializableLocation;
import ml.karmaconfigs.api.bukkit.reflections.hologram.configuration.HologramConfiguration;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.common.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
 * Generic karma hologram functions
 */
public abstract class KarmaHologram implements Serializable {

    /**
     * Create the hologram file
     *
     * In temporal hologram, this does
     * literally nothing
     */
    public abstract void create();

    /**
     * Spawn the hologram
     */
    public abstract void spawn();

    /**
     * Spawn the hologram
     *
     * @param location the spawn location
     */
    public abstract void spawn(final Location location);

    /**
     * Teleport the hologram to the specified location,
     * instead of having to spawn it again
     *
     * @param newLocation the new hologram location
     */
    public abstract void teleport(final Location newLocation);

    /**
     * Hide the armor stand for the specified players
     *
     * @param players the player to hide the armor
     *                stand to
     */
    public abstract void hide(final Player... players);

    /**
     * Hide the armor stand for the specified players
     *
     * @param players the player to hide the armor
     *                stand to
     */
    public abstract void show(final Player... players);

    /**
     * Set the hologram visibility
     *
     * @param status the hologram visibility
     */
    public abstract void setVisible(final boolean status);

    /**
     * Clear all the hologram lines
     */
    public abstract void clearLines();

    /**
     * Update the lines text
     */
    public abstract void updateLines();

    /**
     * Add a new line to the hologram
     *
     * @param line the new line text
     */
    public abstract void add(final String line);

    /**
     * Insert an item stack between two lines
     *
     * @param item the item to add
     */
    public abstract void add(final ItemStack item);

    /**
     * Insert a line or image read from the specified
     * file
     *
     * @param file the file to read from
     */
    public abstract void add(final File file);

    /**
     * Remove an hologram line
     *
     * @param index the line number to remove
     */
    public abstract void remove(final int index);

    /**
     * Destroy the hologram, completely
     */
    public abstract void destroy();

    /**
     * Get this persistent hologram with the specified configuration
     *
     * @param config the hologram configuration
     * @return this instance with the new configuration
     */
    public abstract KarmaHologram withConfiguration(final HologramConfiguration config);

    /**
     * Get the line from an index
     *
     * @param index the index
     * @return the line
     */
    public abstract String getLine(final int index);

    /**
     * Get an item from an index
     *
     * @param index the index
     * @return the item
     */
    public abstract ItemStack getItem(final int index);

    /**
     * Get the index from a line
     *
     * @param line the line
     * @return the line index
     */
    public abstract int getIndex(final String line);

    /**
     * Get the hologram schema
     *
     * @return the hologram schema
     */
    public abstract Map<Integer, String> getHologramSchema();

    /**
     * Get the serializable location
     *
     * @return the serializable location
     */
    public abstract SerializableLocation getLocation();

    /**
     * Get if the player can see the hologram
     *
     * @param player the player
     * @return if the player can see the hologram
     */
    public abstract boolean canSee(final Player player);

    /**
     * Get if the hologram exists
     *
     * @return if the hologram exists
     *
     * For temporal hologram this will be
     * always false, as the hologram itself
     * doesn't exist in data
     */
    public abstract boolean exists();

    /**
     * Get a set of players who the hologram is
     * hidden
     *
     * @return a set of the players with hologram
     * hidden
     */
    public abstract Set<OfflinePlayer> getHidden();

    /**
     * Create a new persistent hologram
     *
     * @param source the karma source
     * @param name the hologram name
     * @return a new persistent hologram
     */
    public static KarmaHologram createHologram(final KarmaSource source, final String name) {
        return new PersistentHologram(source, name);
    }

    /**
     * Create a new hologram
     *
     * @param source the karma source
     * @param name the hologram name
     * @param location the hologram location
     * @return a new persistent hologram
     */
    public static KarmaHologram createHologram(final KarmaSource source, final String name, final Location location) {
        PersistentHologram hologram = (PersistentHologram) createHologram(source, name);
        hologram.spawn(location);

        return hologram;
    }

    /**
     * Create a new hologram
     *
     * @param source the karma source
     * @param name the hologram name
     * @param location the hologram location
     * @param lines the hologram lines
     * @return a new persistent hologram
     */
    public static KarmaHologram createHologram(final KarmaSource source, final String name, final Location location, final String... lines) {
        PersistentHologram hologram = (PersistentHologram) createHologram(source, name);
        SerializableLocation serializable = new SerializableLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.getWorld());
        hologram.spawn(location);

        for (String line : lines)
            hologram.add(line);

        return hologram;
    }

    /**
     * Create a new temp hologram
     *
     * @param location the hologram location
     * @param lines the hologram lines
     * @return a new temporal hologram
     */
    public static KarmaHologram createHologram(final Location location, final String... lines) {
        return new TempHologram(location, lines);
    }

    /**
     * Load all the persistent holograms
     *
     * @param source the hologram source
     * @return the source holograms
     */
    public static KarmaHologram[] loadAllPersistent(final KarmaSource source) {
        Set<KarmaHologram> loaded = new LinkedHashSet<>();
        File holograms = new File(source.getDataPath().toFile() + File.separator + "holograms");
        if (holograms.exists()) {
            File[] files = holograms.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    name = StringUtils.replaceLast(name, "." + FileUtilities.getExtension(file), "");

                    PersistentHologram persistent = new PersistentHologram(source, name);
                    persistent.spawn();

                    loaded.add(persistent);
                }
            }
        }

        return loaded.toArray(new KarmaHologram[0]);
    }
}
