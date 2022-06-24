package ml.karmaconfigs.api.bukkit.region;

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

import ml.karmaconfigs.api.bukkit.KarmaPlugin;
import ml.karmaconfigs.api.bukkit.region.corner.util.Corner;
import ml.karmaconfigs.api.bukkit.region.dummy.BlockListener;
import ml.karmaconfigs.api.bukkit.region.dummy.DummyListener;
import ml.karmaconfigs.api.bukkit.region.wall.util.Wall;
import ml.karmaconfigs.api.bukkit.region.wall.util.WallType;
import ml.karmaconfigs.api.common.ResourceDownloader;
import ml.karmaconfigs.api.common.karma.KarmaConfig;
import ml.karmaconfigs.api.common.utils.url.URLUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Karma cuboid region
 *
 * Code from:
 * https://www.spigotmc.org/threads/region-cuboid.329859/
 */
@SuppressWarnings("unused")
public abstract class Cuboid implements Serializable {

    private static Listener dummy = null;
    private static Listener blocks = null;

    private final static Set<Cuboid> regions = new HashSet<>();

    /**
     * Initialize the cuboid region
     */
    public Cuboid() {
        boolean hooked = dummy != null && blocks != null;
        if (hooked) {
            boolean dummy = false;
            boolean block = false;

            RegisteredListener[] dummyListeners = PlayerMoveEvent.getHandlerList().getRegisteredListeners();
            RegisteredListener[] blockListeners = BlockPlaceEvent.getHandlerList().getRegisteredListeners();

            for (RegisteredListener listener : dummyListeners) {
                Listener l = listener.getListener();
                if (l instanceof DummyListener) {
                    dummy = true;
                    break;
                }
            }

            for (RegisteredListener listener : blockListeners) {
                Listener l = listener.getListener();
                if (l instanceof BlockListener) {
                    block = true;
                    break;
                }
            }

            hooked = dummy && block;
        }

        if (!hooked) {
            KarmaPlugin abc = KarmaPlugin.getABC();
            PluginManager manager = Bukkit.getPluginManager();

            abc.console().send("Hooking into KarmaAPI region listeners to completely enable the region API", Level.WARNING);
            if (!manager.isPluginEnabled("BKCommonLib")) {
                KarmaConfig config = new KarmaConfig();

                if (config.debug(Level.GRAVE)) {
                    abc.console().send("KarmaAPI region API needs BKCommonLib to work but we didn't found it. We will download it", Level.GRAVE);
                }

                File pluginsFolder = new File(abc.getServer().getWorldContainer(), "plugins");
                File destination = new File(pluginsFolder, "BKCommonLib.jar");

                URL dlURL = URLUtils.getOrNull(
                        "https://ci.mg-dev.eu/job/BKCommonLib/1334/artifact/target/BKCommonLib-1.19-v1-1334.jar");

                if (dlURL != null) {
                    ResourceDownloader downloader = new ResourceDownloader(destination, dlURL.toString());
                    downloader.downloadAsync().whenComplete((dl) -> {
                        if (dl) {
                            try {
                                Plugin result = manager.loadPlugin(destination);
                                if (result != null) {
                                    abc.console().send("Downloaded and enabled BKCommonLib", Level.OK);

                                    if (dummy == null) {
                                        dummy = new DummyListener(abc);
                                        manager.registerEvents(dummy, abc);
                                    }
                                    if (blocks == null) {
                                        blocks = new DummyListener(abc);
                                        manager.registerEvents(blocks, abc);
                                    }
                                } else {
                                    abc.console().send("BKCommonLib is downloaded, but couldn't be loaded", Level.GRAVE);
                                }
                            } catch (Throwable error) {
                                abc.console().send("Corrupted BKCommonLib. Please download it manually", Level.GRAVE);
                            }
                        } else {
                            abc.console().send("Couldn't download BKCommonLib. Is this server connected to the internet?", Level.GRAVE);
                        }
                    });
                }
            } else {
                if (dummy == null) {
                    dummy = new DummyListener(abc);
                    manager.registerEvents(dummy, abc);
                }
                if (blocks == null) {
                    blocks = new DummyListener(abc);
                    manager.registerEvents(blocks, abc);
                }
            }
        }

        regions.add(this);
    }

    /**
     * Get the region unique id
     *
     * @return the region unique id
     */
    public abstract UUID getUniqueId();

    /**
     * Get the region name
     *
     * @return the region name
     */
    public abstract String getName();

    /**
     * Get the region internal name
     *
     * @return the region internal name
     */
    public abstract String getInternalName();

    /**
     * Get if the region already exists
     *
     * @param plugin the region owner
     * @return if the region already exists
     */
    public abstract boolean exists(final KarmaPlugin plugin);

    /**
     * Get the region blocks
     *
     * @return the region blocks
     */
    public abstract Iterator<Block> getBlocks();

    /**
     * Get the region center location
     *
     * @return the region center location
     */
    public abstract Location getCenter();

    /**
     * Get the region size
     *
     * @return the region size
     */
    public abstract double getSize();

    /**
     * Get the region size
     *
     * @return the region size
     */
    public abstract double getSizeSquared();

    /**
     * Get the top location
     *
     * @return the top location
     */
    public abstract Location getTop();

    /**
     * Get the bottom location
     *
     * @return the bottom location
     */
    public abstract Location getBottom();

    /**
     * Get the region world
     *
     * @return the region world
     */
    public abstract World getWorld();

    /**
     * Get the amount of blocks inside the region
     *
     * @return the region blocks
     */
    public abstract int getBlocksAmount();

    /**
     * Get the region height
     *
     * @return the region height
     */
    public abstract int getHeight();

    /**
     * Get the region width
     *
     * @return the region width
     */
    public abstract int getWidth();

    /**
     * Get the region length
     *
     * @return the region length
     */
    public abstract int getLength();

    /**
     * Get if the entity is inside the
     * region
     *
     * @param entity the entity
     * @return if the entity is inside the
     * region
     */
    public abstract boolean isInside(final Entity entity);

    /**
     * Get if the block is inside the
     * region
     *
     * @param block the block
     * @return if the block is inside the
     * region
     */
    public abstract boolean isInside(final Block block);

    /**
     * Get if the location is inside the
     * region
     *
     * @param location the location
     * @return if the location is inside the
     * region
     */
    public abstract boolean isInside(final Location location);

    /**
     * Get if the entity is inside the
     * region
     *
     * @param entity the entity
     * @param marge the marge out of
     *              region frontier
     * @return if the entity is inside
     * the region
     */
    public abstract boolean isInside(final Entity entity, final double marge);

    /**
     * Get the region token, this is another
     * unique identifier for the region ( UUID
     * is also used as identifier ).
     *
     * The difference between this one and UUID one
     * is that this token is randomly generated, and
     * UUID name-base generated
     *
     * @return the region token
     */
    public abstract String getToken();

    /**
     * Get the top corners of the region
     *
     * @return the top region corners
     */
    public abstract Corner getTopCorners();

    /**
     * Get the bottom corners of the region
     *
     * @return the bottom region corners
     */
    public abstract Corner getBottomCorners();

    /**
     * Get the region walls
     *
     * @param type the wall type
     * @return the region walls
     */
    public abstract Wall getWalls(final WallType type);

    /**
     * Save the region to memory
     *
     * WARNING: THIS METHOD SHOULD CONTAIN
     * A {@link Cuboid#exists(KarmaPlugin)} CHECK
     * BEFORE BEING PROCESSED, OTHERWISE EXISTING
     * REGION WILL BE OVERWRITTEN
     *
     * @param owner the region owner
     */
    public abstract void saveToMemory(final KarmaPlugin owner);

    /**
     * Get all the regions
     *
     * @return the regions
     */
    public static Set<Cuboid> getRegions() {
        return regions;
    }
}
