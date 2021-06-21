package ml.karmaconfigs.api.bukkit.reflections.hologram;

import ml.karmaconfigs.api.bukkit.SerializableLocation;
import ml.karmaconfigs.api.bukkit.karmaserver.ServerVersion;
import ml.karmaconfigs.api.bukkit.karmaserver.VersionUtils;
import ml.karmaconfigs.api.bukkit.reflections.hologram.configuration.HologramConfiguration;
import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.common.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
public final class TempHologram extends KarmaHologram implements Serializable {

    private SerializableLocation location;

    private final List<String> lines = Collections.synchronizedList(new ArrayList<>());
    private final Map<Integer, ItemStack> items = new ConcurrentHashMap<>();

    private final Set<Integer> entities = Collections.synchronizedSet(new LinkedHashSet<>());
    private final Set<UUID> hidden = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<UUID> shown = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static Class<?> armorStand;
    private static Class<?> entityItem;

    private HologramConfiguration configuration = new HologramConfiguration();

    static {
        initReflection();
    }

    /**
     * Initialize the temp hologram
     *
     * @param loc the hologram location
     * @param text the hologram lines
     */
    public TempHologram(final Location loc, final String... text) {
        location = new SerializableLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), loc.getWorld());
        lines.addAll(Arrays.asList(text));
    }

    /**
     * Create the hologram file
     * <p>
     * In temporal hologram, this does
     * literally nothing
     */
    @Override
    public void create() {}

    /**
     * Spawn the hologram
     */
    public final void spawn() {
        Location location = applyLocationChanges(this.location.toLocation());

        World world = location.getWorld();
        double original_y = location.getY();
        if (world != null) {
            int index = 0;
            for (String line : lines) {
                if (line.equals("ITEM_ON_LINE:" + index)) {
                    ItemStack item = getItem(index);
                    if (item != null) {
                        summonItem(item, location, Bukkit.getOnlinePlayers().toArray(new Player[0]));
                    }
                } else {
                    summonTextStand(index - 1, line, location, Bukkit.getOnlinePlayers().toArray(new Player[0]));
                }

                index++;
            }
        } else {
            Console.send("&cFailed to spawn temp hologram, location world is null");
        }

        location.setY(original_y);
    }

    /**
     * Spawn the hologram
     *
     * @param loc the spawn location
     */
    @Override
    public final void spawn(Location loc) {
        location = new SerializableLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), loc.getWorld());
        destroy();
        spawn();
    }

    /**
     * Teleport the hologram to the specified location,
     * instead of having to spawn it again
     *
     * @param newLocation the new hologram location
     */
    @Override
    public void teleport(Location newLocation) {
        location = new SerializableLocation(newLocation.getX(), newLocation.getY(), newLocation.getZ(), newLocation.getYaw(), newLocation.getPitch(), newLocation.getWorld());
        updateLines();
    }

    /**
     * Hide the armor stand for the specified players
     *
     * @param players the player to hide the armor
     *                stand to
     */
    public final void hide(final Player... players) {
        VersionUtils utils = new ServerVersion();
        for (int id : entities) {
            Object packetPlayOutEntityDestroy = utils.createPacket("PacketPlayOutEntityDestroy", int[].class, new int[]{id});

            for (Player player : players) {
                utils.invokePacket(player, packetPlayOutEntityDestroy);
                hidden.add(player.getUniqueId());
                shown.remove(player.getUniqueId());
            }
        }
    }

    /**
     * Hide the armor stand for the specified players
     *
     * @param players the player to hide the armor
     *                stand to
     */
    public final void show(final Player... players) {
        if (this.location != null) {
            Location location = applyLocationChanges(this.location.toLocation());
            World world = location.getWorld();
            if (world != null) {
                int index = 0;
                for (String line : lines) {
                    if (line.equals("ITEM_ON_LINE:" + index)) {
                        ItemStack item = getItem(index);
                        if (item != null) {
                            summonItem(item, location, players);
                        }
                    } else {
                        summonTextStand(index - 1, line, location, players);
                    }

                    index++;
                }
            }
        }
    }

    /**
     * Set the hologram visibility
     *
     * @param status the hologram visibility
     */
    public final void setVisible(final boolean status) {
        if (status) {
            show(Bukkit.getOnlinePlayers().toArray(new Player[0]));
        } else {
            hide(Bukkit.getOnlinePlayers().toArray(new Player[0]));
            entities.clear();
        }
    }

    /**
     * Clear all the hologram lines
     */
    @Override
    public final void clearLines() {
        lines.clear();
        setVisible(false);
        setVisible(true);
    }

    /**
     * Update the lines text
     */
    @Override
    public final void updateLines() {
        destroy();
        spawn();
    }

    /**
     * Add a new line to the hologram
     *
     * @param text the new line text
     */
    public final void add(final String text) {
        lines.add(StringUtils.toColor(text));
    }

    /**
     * Insert an item stack between two lines
     *
     * @param item the item to add
     */
    @Override
    public void add(ItemStack item) {
        items.put(lines.size(), item);
        lines.add("ITEM_ON_LINE:" + lines.size());
    }

    /**
     * Insert a line or image read from the specified
     * file
     *
     * @param file the file to read from
     */
    @Override
    public void add(File file) {
        if (FileUtilities.getFileType(file).toLowerCase().startsWith("image")) {
            String result = StringUtils.readImage(file);
            lines.add(result);
        } else {
            List<String> readLines = FileUtilities.readAllLines(file);
            lines.addAll(StringUtils.toColor(readLines));
        }
    }

    /**
     * Remove an hologram line
     *
     * @param line the line number to remove
     */
    public final void remove(final int line) {
        try {
            if (getLine(line).equals("ITEM_ON_LINE:" + line)) {
                items.remove(line);
            }

            lines.remove(line);
            moveItemsDown();
        } catch (Throwable ignored) {}
    }

    /**
     * Destroy the hologram, completely
     */
    public final void destroy() {
        VersionUtils utils = new ServerVersion();
        for (int id : entities) {
            Object packetPlayOutEntityDestroy = utils.createPacket("PacketPlayOutEntityDestroy", int[].class, new int[]{id});

            for (Player player : Bukkit.getOnlinePlayers()) {
                utils.invokePacket(player, packetPlayOutEntityDestroy);
            }
        }

        shown.clear();
        hidden.clear();
        entities.clear();
    }

    /**
     * Get this persistent hologram with the specified configuration
     *
     * @param config the hologram configuration
     * @return this instance with the new configuration
     */
    @Override
    public KarmaHologram withConfiguration(final HologramConfiguration config) {
        configuration = config;

        return this;
    }

    /**
     * Get the line from an index
     *
     * @param index the index
     * @return the line
     */
    public final String getLine(final int index) {
        if (lines.size() > index)
            return lines.get(index);

        return "";
    }

    /**
     * Get an item from an index
     *
     * @param index the index
     * @return the item
     */
    @Override
    public ItemStack getItem(int index) {
        return items.getOrDefault(index, null);
    }

    /**
     * Get the index from a line
     *
     * @param line the line
     * @return the line index
     */
    public final int getIndex(final String line) {
        int index = 0;
        for (String str : lines) {
            if (str.equals(line)) {
                return index;
            }
            index++;
        }

        return -1;
    }

    /**
     * Get the hologram schema
     *
     * @return the hologram schema
     */
    public final Map<Integer, String> getHologramSchema() {
        Map<Integer, String> schema = new HashMap<>();

        int index = 0;
        for (String line : lines)
            schema.put(index++, line);

        return schema;
    }

    /**
     * Get the serializable location
     *
     * @return the serializable location
     */
    public final SerializableLocation getLocation() {
        return location;
    }

    /**
     * Get if the player can see the hologram
     *
     * @param player the player
     * @return if the player can see the hologram
     */
    @Override
    public boolean canSee(Player player) {
        return shown.contains(player.getUniqueId());
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
        return false;
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
        Set<OfflinePlayer> offline = new HashSet<>();
        for (UUID hidden : hidden)
            offline.add(Bukkit.getOfflinePlayer(hidden));

        return offline;
    }

    /**
     * Move all items down
     */
    private void moveItemsDown() {
        int index = 0;
        int max = lines.size();
        ItemStack result;
        do  {
            result = items.getOrDefault(index, null);
            if (result != null) {
                if (index != 0) {
                    items.put((index - 1), result);
                }
            }

            index++;
        } while (index < max);
    }

    /**
     * Summon an item
     *
     * @param item the item to add
     * @param location the item location
     * @param players the player to show to
     */
    private void summonItem(final ItemStack item, final Location location, final Player... players) {
        VersionUtils utils = new ServerVersion();

        Object itemEntity = utils.createEntity(entityItem, location, item);
        if (itemEntity != null) {
            utils.invoke(itemEntity, "setNoGravity", boolean.class, true);
            utils.invoke(itemEntity, "setPickupDelay", int.class, Integer.MAX_VALUE);

            Object temp_id = utils.invoke(itemEntity, "getId", null);
            if (temp_id != null) {
                int id = (int) temp_id;
                entities.add(id);

                Object packetPlayOutSpawnEntity = utils.createPacket("PacketPlayOutSpawnEntity", utils.getMinecraftClass("Entity"), itemEntity);
                Object packetPlayOutEntityVelocity = utils.createPacket("PacketPlayOutEntityVelocity", new Class[]{int.class, utils.getMinecraftClass("Vec3D")}, id, utils.createVector3D(configuration.getItemVelocity().getX(), configuration.getItemVelocity().getY(), configuration.getItemVelocity().getZ()));
                Object packetPlayOutEntityMetadata = utils.createPacket("PacketPlayOutEntityMetadata", new Class[]{int.class, utils.getMinecraftClass("DataWatcher"), boolean.class}, id, utils.invoke(itemEntity, "getDataWatcher", null), true);
                for (Player player : players) {
                    hidden.remove(player.getUniqueId());

                    shown.add(player.getUniqueId());
                    utils.invokePacket(player, packetPlayOutSpawnEntity);
                    utils.invokePacket(player, packetPlayOutEntityMetadata);

                    if (configuration.changeVelocity())
                        utils.invokePacket(player, packetPlayOutEntityVelocity);
                }

                location.setY(location.getY() - configuration.getItemSeparation());
            }
        }
    }

    /**
     * Summon a text stand
     *
     * @param line the armor stand text
     * @param location the armor stand location
     * @param players the player to show to
     */
    private void summonTextStand(final int previousIndex, final String line, final Location location, final Player... players) {
        VersionUtils utils = new ServerVersion();

        Object stand = utils.createEntity(armorStand, location);
        if (stand != null) {
            utils.invoke(stand, "setInvisible", boolean.class, true);
            utils.invoke(stand, "setSmall", boolean.class, true);
            utils.invoke(stand, "setArms", boolean.class, false);
            utils.invoke(stand, "setMarker", boolean.class, true);
            utils.invoke(stand, "setBasePlate", boolean.class, false);
            utils.invoke(stand, "setNoGravity", boolean.class, true);
            utils.invoke(stand, "setBasePlate", boolean.class, false);
            utils.invoke(stand, "setHeadPose", utils.getMinecraftClass("Vector3f"), utils.createVector3F(180f, 0f, 0f));
            utils.invoke(stand, "setLeftArmPose", utils.getMinecraftClass("Vector3f"), utils.createVector3F(180f, 0f, 0f));
            utils.invoke(stand, "setRightArmPose", utils.getMinecraftClass("Vector3f"), utils.createVector3F(180f, 0f, 0f));
            utils.invoke(stand, "setLeftLegPose", utils.getMinecraftClass("Vector3f"), utils.createVector3F(180f, 0f, 0f));
            utils.invoke(stand, "setRightLegPose", utils.getMinecraftClass("Vector3f"), utils.createVector3F(180f, 0f, 0f));
            utils.invoke(stand, "setCustomName", utils.getMinecraftClass("IChatBaseComponent"), utils.createChatMessage(StringUtils.toColor(line)));
            utils.invoke(stand, "setCustomNameVisible", boolean.class, true);

            Object temp_id = utils.invoke(stand, "getId", null);
            if (temp_id != null) {
                int id = (int) temp_id;
                entities.add(id);

                Object packetPlayOutSpawnEntityLiving = utils.createPacket("PacketPlayOutSpawnEntityLiving", utils.getMinecraftClass("EntityLiving"), stand);
                Object packetPlayOutEntityMetadata = utils.createPacket("PacketPlayOutEntityMetadata", new Class[]{int.class, utils.getMinecraftClass("DataWatcher"), boolean.class}, id, utils.invoke(stand, "getDataWatcher", null), true);
                for (Player player : players) {
                    hidden.remove(player.getUniqueId());

                    shown.add(player.getUniqueId());
                    utils.invokePacket(player, packetPlayOutSpawnEntityLiving);
                    utils.invokePacket(player, packetPlayOutEntityMetadata);
                }

                if (getItem(previousIndex) == null)
                    location.setY(location.getY() - configuration.getLineSeparation());
                else
                    location.setY(location.getY() - configuration.getItemSeparation());
            }
        }
    }

    /**
     * Apply location changes
     *
     * @param location the location
     */
    private Location applyLocationChanges(final Location location) {
        Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());

        loc.setX(location.getX() + configuration.getOffsetConfiguration().getX());
        loc.setY(location.getY() + configuration.getOffsetConfiguration().getY());
        loc.setZ(location.getZ() + configuration.getOffsetConfiguration().getZ());
        if (configuration.isAutoCenter()) {
            loc.add(0.5, 0, 0.5);
        }

        return loc;
    }

    /**
     * Initialize reflections
     */
    private static void initReflection() {
        VersionUtils utils = new ServerVersion();

        armorStand = utils.getMinecraftClass("EntityArmorStand");
        entityItem = utils.getMinecraftClass("EntityItem");
    }
}
