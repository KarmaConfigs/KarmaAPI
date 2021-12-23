package ml.karmaconfigs.api.bukkit.reflection;

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

import ml.karmaconfigs.api.bukkit.server.BukkitServer;
import ml.karmaconfigs.api.bukkit.server.Version;
import ml.karmaconfigs.api.common.boss.*;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.SourceSecondsTimer;
import ml.karmaconfigs.api.common.timer.TimeCondition;
import ml.karmaconfigs.api.common.timer.scheduler.SimpleScheduler;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Karma boss bar message
 */
public final class BossMessage extends BossProvider<Player> {

    /**
     * Boss bar source
     */
    private final KarmaSource plugin;

    /**
     * Boss bar message
     */
    private String message;

    /**
     * Boss bar time to live
     */
    private final double live_time;

    /**
     * Boss bars amount
     */
    private static int bars = 0;

    /**
     * If the boss bar needs reflection
     */
    private static boolean isLegacy = BukkitServer.isUnder(Version.v1_13);

    /**
     * If the boss bar set health/progress method
     * is float instead of double
     */
    private static boolean toFloat = false;

    /**
     * Craft world
     */
    private static Class<?> craft_world;
    /**
     * Craft player
     */
    private static Class<?> craft_player;
    /**
     * Packet
     */
    private static Class<?> packet_class;
    /**
     * Connection packet
     */
    private static Class<?> packet_connection;
    /**
     * Destroy packet
     */
    private static Class<?> packet_play_out_destroy;

    /**
     * Create wither
     */
    private static Constructor<?> wither_constructor;
    /**
     * Create living entity
     */
    private static Constructor<?> entity_living_constructor;
    /**
     * Teleport entity
     */
    private static Constructor<?> packet_play_teleport_constructor;

    /**
     * Craft world handler
     */
    private static Method craft_world_handle;
    /**
     * Craft player handler
     */
    private static Method craft_player_handle;
    /**
     * Send packet
     */
    private static Method packet_connect_send;
    /**
     * Set wither location
     */
    private static Method wither_set_location_method;
    /**
     * Set boss bar progress
     */
    private static Method wither_set_progress_method;

    /**
     * List of boss bars
     */
    private static final List<BossMessage> b_bars = new ArrayList<>();

    /**
     * A map containing id => boss bar
     */
    private static final Map<Integer, BossMessage> boss_bars = new LinkedHashMap<>();
    /**
     * A map containing id => bar util
     */
    private static final Map<Integer, Object> wither_objects = new LinkedHashMap<>();

    /**
     * Boss bar shown players
     */
    private final Set<UUID> shown = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Boss bar color style
     */
    private BossColor color = BossColor.PURPLE;
    /**
     * Boss bar type style
     */
    private BossType type = BossType.SOLID;
    /**
     * Boss bar progress style
     */
    private ProgressiveBar progress = ProgressiveBar.NONE;

    /**
     * The boss bar lived time
     */
    private double lived_time = 0.0;

    /**
     * If the boss bar is cancelled
     */
    private boolean cancelled = false;

    /**
     * Last boss bar ID, used for new boss bar
     * creations
     */
    private static int total_ids = 0;

    /**
     * Boss bar id
     */
    private final int id;

    /**
     * Boss bar timer
     */
    private SimpleScheduler bar_timer = null;

    /**
     * New boss bar location
     */
    private Location newLoc;

    /**
     * Boss bar teleport packet
     */
    private Object teleport_packet;
    /**
     * Craft player
     */
    private Object new_c_player;
    /**
     * Entity player
     */
    private Object new_e_player;
    /**
     * Player connection
     */
    private Object new_p_connection;
    /**
     * Destroy wither and boss bar
     */
    private Object remove_wither;

    /**
     * Initialize the boss message
     *
     * @param owner the boss message source
     * @param _message the boss bar message
     * @param duration the boss bar duration
     */
    public BossMessage(final KarmaSource owner, final String _message, final double duration) {
        plugin = owner;
        message = _message;
        live_time = duration;
        if (isLegacy) {
            doReflectionStuff();
        }

        id = ++total_ids;
    }

    /**
     * Set the boss bar color
     *
     * @param newColor the boss bar color
     * @return this boss bar instance
     */
    @Override
    public BossProvider<Player> color(final BossColor newColor) {
        color = newColor;
        return this;
    }

    /**
     * Set the boss bar style
     *
     * @param newType the boss bar style
     * @return this boss bar instance
     */
    @Override
    public BossProvider<Player> style(final BossType newType) {
        type = newType;
        try {
            BossBar wither = (BossBar) wither_objects.get(id);
            wither.setColor(BarColor.valueOf(color.name()));
            wither.setVisible(false);
            wither.setVisible(true);
        } catch (Throwable ignored) {
        }
        return this;
    }

    /**
     * Set the boss bar progress type
     *
     * @param type the boss bar progress type
     * @return this boss bar instance
     */
    @Override
    public BossProvider<Player> progress(final ProgressiveBar type) {
        progress = type;
        try {
            BossBar wither = (BossBar) wither_objects.get(id);
            wither.setStyle(BarStyle.valueOf(type.name()));
            wither.setVisible(false);
            wither.setVisible(true);
        } catch (Throwable ignored) {
        }
        return this;
    }

    /**
     * Destroy the current boss bar
     */
    @Override
    public void cancel() {
        cancelled = true;
    }

    /**
     * Display the boss bar to the specified players
     *
     * @param players the players to display to
     */
    @Override
    protected void displayBar(final Collection<Player> players) {
        bars++;
        if (cancelled) {
            cancelled = false;
        }

        switch (progress) {
            case DOWN: {
                lived_time = live_time - 1.0;
                break;
            }
            case UP: {
                lived_time = 0.0;
                break;
            }
        }

        if (isLegacy) {
            try {
                for (final Player player : players) {
                    if (!shown.contains(player.getUniqueId())) {
                        shown.add(player.getUniqueId());
                        Location location = player.getLocation();
                        
                        Object c_world = craft_world.cast(player.getWorld());
                        Object w_server = craft_world_handle.invoke(c_world);
                        Object wither = wither_constructor.newInstance(w_server);
                        
                        wither.getClass().getMethod("setCustomName", String.class).invoke(wither, message);
                        wither.getClass().getMethod("setInvisible", Boolean.TYPE).invoke(wither, true);
                        wither.getClass().getMethod("setLocation", Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE).invoke(wither, location.getX(), location.getY(), location.getZ(), 0, 0);
                        
                        Object packet = entity_living_constructor.newInstance(wither);
                        Object c_player = craft_player.cast(player);
                        Object e_player = craft_player_handle.invoke(c_player);
                        Object p_connection = e_player.getClass().getField("playerConnection").get(e_player);
                        
                        packet_connect_send.invoke(packet_connection.cast(p_connection), packet);
                        wither_objects.put(id, wither);
                        bar_timer = new SourceSecondsTimer(plugin, live_time, false).cancelUnloaded(false);

                        bar_timer.conditionalAction(TimeCondition.OVER_OF, 2, second -> {
                            try {
                                newLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(20).add(new Vector(0, 5, 0)));
                                wither_set_location_method.invoke(player, newLoc.getX(), newLoc.getY(), newLoc.getZ(), newLoc.getYaw(), newLoc.getPitch());
                                teleport_packet = packet_play_teleport_constructor.newInstance(player);
                                new_c_player = craft_player.cast(player);
                                new_e_player = craft_player_handle.invoke(new_c_player);
                                new_p_connection = new_e_player.getClass().getField("playerConnection").get(new_e_player);
                                packet_connection = BukkitServer.getMinecraftClass("PlayerConnection");
                                packet_class = BukkitServer.getMinecraftClass("Packet");
                                packet_connect_send.invoke(packet_connection.cast(new_p_connection), teleport_packet);
                            } catch (Throwable ex) {
                                ex.printStackTrace();
                                bar_timer.cancel();
                            }
                        }).endAction(() -> {
                            try {
                                remove_wither = packet_play_out_destroy.getConstructor(BukkitServer.getMinecraftClass("EntityLiving"))
                                        .newInstance(player.getUniqueId());
                                packet_connection = BukkitServer.getMinecraftClass("PlayerConnection");
                                packet_class = BukkitServer.getMinecraftClass("Packet");
                                craft_player = BukkitServer.getMinecraftClass("entity.CraftPlayer");
                                if (craft_player != null) {
                                    new_c_player = craft_player.cast(player);
                                    new_e_player = craft_player_handle.invoke(new_c_player);
                                    new_p_connection = new_e_player.getClass().getField("playerConnection").get(new_e_player);
                                    packet_connect_send.invoke(packet_connection.cast(new_p_connection), remove_wither);
                                    boss_bars.remove(id);
                                    wither_objects.remove(id);
                                    shown.remove(player.getUniqueId());
                                    --bars;
                                }
                            } catch (Throwable ex2) {
                                ex2.printStackTrace();
                                bar_timer.cancel();
                            }
                        }).cancelAction(time -> {
                            try {
                                remove_wither = packet_play_out_destroy.getConstructor(BukkitServer.getMinecraftClass("EntityLiving"))
                                        .newInstance(player.getUniqueId());
                                packet_connection = BukkitServer.getMinecraftClass("PlayerConnection");
                                packet_class = BukkitServer.getMinecraftClass("Packet");
                                craft_player = BukkitServer.getMinecraftClass("entity.CraftPlayer");
                                if (craft_player != null) {
                                    new_c_player = craft_player.cast(player);
                                    new_e_player = craft_player_handle.invoke(new_c_player);
                                    new_p_connection = new_e_player.getClass().getField("playerConnection").get(new_e_player);
                                    packet_connect_send.invoke(packet_connection.cast(new_p_connection), remove_wither);
                                    boss_bars.remove(id);
                                    wither_objects.remove(id);
                                    shown.remove(player.getUniqueId());
                                    --bars;
                                }
                            } catch (Throwable ex3) {
                                ex3.printStackTrace();
                                bar_timer.cancel();
                            }
                        }).start();
                        
                        SimpleScheduler hp_timer = new SourceSecondsTimer(plugin, live_time - 1.0, false).cancelUnloaded(false);
                        hp_timer.secondChangeAction(second -> {
                            if (!cancelled) {
                                try {
                                    double percentage;

                                    switch (progress) {
                                        case UP: {
                                            percentage = lived_time / live_time;
                                            wither_set_progress_method.invoke(wither, (toFloat ? ((float) percentage) : percentage));

                                            lived_time--;
                                            break;
                                        }
                                        case DOWN: {
                                            percentage = second / live_time;
                                            wither_set_progress_method.invoke(wither, (toFloat ? ((float) percentage) : percentage));

                                            lived_time--;
                                            break;
                                        }
                                    }
                                } catch (Throwable ex5) {
                                    cancel();
                                }
                            } else {
                                bar_timer.cancel();
                                hp_timer.cancel();
                            }
                        }).start();
                    }
                }
            } catch (Throwable ex4) {
                ex4.printStackTrace();
            }
        } else {
            BossBar wither = Bukkit.getServer()
                    .createBossBar(StringUtils.toColor(message), BarColor.valueOf(color.name()), BarStyle.valueOf(type.name()));
            for (final Player player2 : players) {
                wither.addPlayer(player2);
            }

            wither.setVisible(true);
            wither_objects.put(id, wither);
            bar_timer = new SourceSecondsTimer(plugin, live_time, false).cancelUnloaded(false);

            bar_timer.endAction(() -> {
                wither.setVisible(false);
                wither.removeAll();
                boss_bars.remove(id);
                wither_objects.remove(id);
                for (Player player : players) {
                    shown.remove(player.getUniqueId());
                }

                bars--;
            }).cancelAction(end -> {
                wither.setVisible(false);
                wither.removeAll();
                boss_bars.remove(id);
                wither_objects.remove(id);
                for (Player player : players) {
                    shown.remove(player.getUniqueId());
                }

                bars--;
            }).start();

            SimpleScheduler hp_timer = new SourceSecondsTimer(plugin, live_time - 1.0, false).cancelUnloaded(false);
            hp_timer.secondChangeAction(second -> {
                if (!cancelled) {
                    double percentage;

                    try {
                        wither.setColor(BarColor.valueOf(color.name()));
                        wither.setStyle(BarStyle.valueOf(type.name()));
                        switch (progress) {
                            case UP: {
                                percentage = lived_time / live_time;
                                wither.setProgress(percentage);

                                lived_time++;
                                break;
                            }
                            case DOWN: {
                                percentage = second / live_time;
                                wither.setProgress(percentage);

                                lived_time--;
                                break;
                            }
                        }
                    } catch (Throwable ex6) {
                        cancel();
                    }
                } else {
                    bar_timer.cancel();
                    hp_timer.cancel();
                }
            }).start();
        }
    }

    /**
     * Schedule the bar to the specified players
     *
     * @param players the players to display to
     */
    @Override
    public void scheduleBar(final Collection<Player> players) {
        b_bars.add(this);
        boss_bars.put(id, this);

        SimpleScheduler timer = new SourceSecondsTimer(plugin, 1, false).cancelUnloaded(false).multiThreading(true);
        timer.periodChangeAction(milli -> {
            if (!b_bars.isEmpty() && getBarsAmount() < 4) {
                BossMessage boss = b_bars.get(0);
                boss.displayBar(players);
                b_bars.remove(boss);
            }
        }).start();
    }

    /**
     * Schedule the bar to the specified player
     *
     * @param player the player to display to
     */
    @Override
    public void scheduleBar(final Player player) {
        b_bars.add(this);
        boss_bars.put(id, this);

        SimpleScheduler timer = new SourceSecondsTimer(plugin, 1, false).cancelUnloaded(false).multiThreading(true);
        timer.periodChangeAction(milli -> {
            if (!b_bars.isEmpty() && getBarsAmount() < 4) {
                BossMessage boss = b_bars.get(0);
                boss.displayBar(Collections.singleton(player));
                b_bars.remove(boss);
            }
        }).start();
    }

    /**
     * Get the amount of bars that exist
     *
     * @return the amount of bars created
     */
    @Override
    public int getBarsAmount() {
        return bars;
    }

    /**
     * Get the current boss bar id
     *
     * @return the current boss bar id
     */
    @Override
    public int getBarId() {
        return id;
    }

    /**
     * Update the boss bar
     *
     * @param _message the new boss bar text
     * @param restart restart the bar progress
     * @return if the boss bar could be updated
     */
    @Override
    public boolean update(final String _message, final boolean restart) {
        try {
            message = _message;
            if (isLegacy) {
                final Object wither = wither_objects.get(id);
                wither.getClass().getMethod("setCustomName", String.class).invoke(wither, StringUtils.toColor(message));
            } else {
                BossBar bar = (BossBar) wither_objects.get(id);
                bar.setTitle(StringUtils.toColor(message));
                List<Player> players = bar.getPlayers();
                players.forEach(bar::addPlayer);
            }
            if (bar_timer != null && restart) {
                bar_timer.restart();
            }
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Set the boss bar display time
     *
     * @param displayTime the boss bar display time
     * @return the boss bar display time
     */
    @Override
    public BossProvider<Player> displayTime(final double displayTime) {
        cancel();
        return new BossMessage(plugin, message, displayTime).color(color).style(type).progress(progress);
    }

    /**
     * Get if the boss bar is valid
     *
     * @return if the boss bar is valid
     */
    @Override
    public boolean isValid() {
        return wither_objects.containsKey(id);
    }

    /**
     * Get if the boss bar is cancelled
     *
     * @return if the boss bar is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Get the boss bar title
     *
     * @return the boss bar title
     */
    @Override
    public String getTitle() {
        return message;
    }

    /**
     * Get the boss bar color
     *
     * @return the boss bar color
     */
    @Override
    public BossColor getColor() {
        return color;
    }

    /**
     * Get the boss bar style
     *
     * @return the boss bar style
     */
    @Override
    public BossType getStyle() {
        return type;
    }

    /**
     * Prepare the boss bar for legacy ( non-vanilla implemented boss bar API ) instances
     */
    private void doReflectionStuff() {
        try {
            craft_world = BukkitServer.getBukkitClass("CraftWorld");
            Class<?> entity_wither = BukkitServer.getMinecraftClass("EntityWither");
            if (entity_wither != null) {
                Class<?> packet_entity_living_out = BukkitServer.getMinecraftClass("PacketPlayOutSpawnEntityLiving");
                if (packet_entity_living_out != null) {
                    craft_player = BukkitServer.getBukkitClass("entity.CraftPlayer");
                    packet_class = BukkitServer.getMinecraftClass("Packet");
                    packet_connection = BukkitServer.getMinecraftClass("PlayerConnection");
                    packet_play_out_destroy = BukkitServer.getMinecraftClass("PacketPlayOutEntityDestroy");
                    Class<?> packet_play_teleport = BukkitServer.getMinecraftClass("PacketPlayOutEntityTeleport");
                    if (packet_play_teleport != null) {
                        wither_constructor = entity_wither.getConstructor(BukkitServer.getMinecraftClass("World"));
                        entity_living_constructor = packet_entity_living_out.getConstructor(BukkitServer.getMinecraftClass("EntityLiving"));
                        packet_play_teleport_constructor = packet_play_teleport.getConstructor(BukkitServer.getMinecraftClass("Entity"));
                        craft_world_handle = craft_world.getMethod("getHandle");
                        craft_player_handle = craft_player.getMethod("getHandle");
                        packet_connect_send = packet_connection.getMethod("sendPacket", packet_class);
                        wither_set_location_method = entity_wither.getMethod("setLocation", Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE);
                        wither_set_location_method = entity_wither.getMethod("setLocation", Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE);
                        try {
                            wither_set_progress_method = entity_wither.getMethod("setProgress", Double.TYPE);
                        } catch (Throwable ex) {
                            try {
                                wither_set_progress_method = entity_wither.getMethod("setProgress", Float.TYPE);
                                toFloat = true;
                            } catch (Throwable exc) {
                                try {
                                    wither_set_progress_method = entity_wither.getMethod("setHealth", Double.TYPE);
                                } catch (Throwable exce) {
                                    try {
                                        wither_set_progress_method = entity_wither.getMethod("setHealth", Float.TYPE);
                                        toFloat = true;
                                    } catch (Throwable excep) {
                                        isLegacy = false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Boss bar getters
     */
    public interface getters {

        /**
         * Get a boss bar by ID
         *
         * @param id the bar ID
         * @return the boss bar
         * @throws BossNotFoundException if the boss bar could not be found
         */
        static BossProvider<Player> getByID(final int id) throws BossNotFoundException {
            try {
                if (boss_bars.containsKey(id)) {
                    final BossProvider<Player> boss = boss_bars.getOrDefault(id, null);
                    if (boss != null) {
                        return boss;
                    }
                }
                throw new BossNotFoundException(id, boss_bars.keySet());
            } catch (Throwable ex) {
                throw new BossNotFoundException(id, boss_bars.keySet());
            }
        }
    }
}
