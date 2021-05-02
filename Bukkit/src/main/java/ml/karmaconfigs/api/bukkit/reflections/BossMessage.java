package ml.karmaconfigs.api.bukkit.reflections;

import ml.karmaconfigs.api.bukkit.karmaserver.ServerVersion;
import ml.karmaconfigs.api.bukkit.karmaserver.Version;
import ml.karmaconfigs.api.bukkit.karmaserver.VersionUtils;
import ml.karmaconfigs.api.common.boss.BossColor;
import ml.karmaconfigs.api.common.boss.BossNotFoundException;
import ml.karmaconfigs.api.common.boss.BossType;
import ml.karmaconfigs.api.common.boss.ProgressiveBar;
import ml.karmaconfigs.api.bukkit.timer.AdvancedPluginTimer;
import ml.karmaconfigs.api.common.timer.TimeCondition;
import ml.karmaconfigs.api.common.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="http://karmaconfigs.cf/license/"> here </a>
 * or (fallback domain) <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class BossMessage {

    private final Plugin plugin;

    private String message;
    private final double live_time;
    private static int bars = 0;
    
    private static boolean isLegacy;

    private static Class<?> craft_world, craft_player, packet_class, packet_connection, packet_play_out_destroy;
    private static Constructor<?> wither_constructor, entity_living_constructor, packet_play_teleport_constructor;
    private static Method craft_world_handle, craft_player_handle, packet_connect_send, wither_set_location_method, wither_set_progress_method;

    private static final List<BossMessage> b_bars = new ArrayList<>();

    private final static Map<Integer, BossMessage> boss_bars = new LinkedHashMap<>();
    private final static Map<Integer, Object> wither_objects = new LinkedHashMap<>();

    private final VersionUtils utils;

    private BossColor color = BossColor.PURPLE;
    private BossType type = BossType.SOLID;
    private ProgressiveBar progress = ProgressiveBar.NONE;
    private double lived_time = 0D;
    private boolean cancelled = false;

    private static int total_ids = 0;

    private final int id;

    private AdvancedPluginTimer bar_timer = null;

    /**
     * Initialize the BossBar with its message
     *
     * @param owner the owner plugin
     * @param _message the message
     * @param duration the message duration
     */
    public BossMessage(final JavaPlugin owner, final String _message, final double duration) {
        plugin = owner;

        message = _message;
        live_time = duration;

        ServerVersion server = new ServerVersion(owner);
        utils = server.utils();

        if (utils != null)
            isLegacy = utils.isUnder(Version.v1_13);
        
        if (isLegacy)
            doReflectionStuff();

        total_ids++;

        id = total_ids;
    }

    /**
     * Set the boss bar color
     *
     * @param newColor the new bar color
     * @return this instance
     */
    public final BossMessage color(final BossColor newColor) {
        color = newColor;

        return this;
    }

    /**
     * Set the boss bar style
     *
     * @param newType the new bar style
     * @return this instance
     */
    public final BossMessage style(final BossType newType) {
        type = newType;

        try {
            org.bukkit.boss.BossBar wither = (org.bukkit.boss.BossBar) wither_objects.get(id);
            wither.setColor(org.bukkit.boss.BarColor.valueOf(color.name()));
            wither.setVisible(false);
            wither.setVisible(true);
        } catch (Throwable ignored) {}

        return this;
    }

    /**
     * Set the boss bar HP progress style
     *
     * @param type the bar HP progress type
     * @return this instance
     */
    public final BossMessage progress(final ProgressiveBar type) {
        progress = type;

        try {
            org.bukkit.boss.BossBar wither = (org.bukkit.boss.BossBar) wither_objects.get(id);
            wither.setStyle(org.bukkit.boss.BarStyle.valueOf(type.name()));
            wither.setVisible(false);
            wither.setVisible(true);
        } catch (Throwable ignored) {}

        return this;
    }

    /**
     * Cancel the boss bar
     */
    public final void cancel() {
        cancelled = true;
    }

    /**
     * Show the boss bar and start the
     * countdown timer
     *
     * @param players the players to show
     */
    private void displayBar(Collection<Player> players) {
        bars++;
        if (cancelled)
            cancelled = false;

        switch (progress) {
            case DOWN:
                lived_time = live_time;
                break;
            case UP:
                lived_time = 0D;
                break;
        }

        if (isLegacy) {
            try {
                for (Player player : players) {
                    Location location = player.getLocation();

                    Object c_world = craft_world.cast(player.getWorld());
                    Object w_server = craft_world_handle.invoke(c_world);

                    Object wither = wither_constructor.newInstance(w_server);
                    wither.getClass().getMethod("setCustomName", String.class).invoke(wither, message);
                    wither.getClass().getMethod("setInvisible", boolean.class).invoke(wither, true);
                    wither.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(wither, location.getX(), location.getY(), location.getZ(), 0, 0);

                    Object packet = entity_living_constructor.newInstance(wither);
                    Object c_player = craft_player.cast(player);
                    Object e_player = craft_player_handle.invoke(c_player);
                    Object p_connection = e_player.getClass().getField("playerConnection").get(e_player);

                    packet_connect_send.invoke(packet_connection.cast(p_connection), packet);

                    wither_objects.put(id, wither);

                    bar_timer = new AdvancedPluginTimer(plugin, (int) live_time, false);
                    bar_timer.addAction(TimeCondition.OVER_OF, 2, () -> {
                        try {
                            Location newLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(20).add(new Vector(0, 5, 0)));

                            wither_set_location_method.invoke(wither, newLoc.getX(), newLoc.getY(), newLoc.getZ(), newLoc.getYaw(), newLoc.getPitch());
                            Object teleport_packet = packet_play_teleport_constructor.newInstance(wither);

                            Object new_c_player = craft_player.cast(player);
                            Object new_e_player = craft_player_handle.invoke(new_c_player);
                            Object new_p_connection = new_e_player.getClass().getField("playerConnection").get(new_e_player);

                            packet_connection = utils.getMinecraftClass("PlayerConnection");
                            packet_class = utils.getMinecraftClass("Packet");

                            packet_connect_send.invoke(packet_connection.cast(new_p_connection), teleport_packet);
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            bar_timer.setCancelled();
                        }
                    }).addActionOnEnd(() -> {
                        try {
                            Object remove_wither = packet_play_out_destroy.getConstructor(utils.getMinecraftClass("EntityLiving")).newInstance(player.getUniqueId());

                            packet_connection = utils.getMinecraftClass("PlayerConnection");
                            packet_class = utils.getMinecraftClass("Packet");

                            craft_player = utils.getMinecraftClass("entity.CraftPlayer");

                            if (craft_player != null) {
                                Object new_c_player = craft_player.cast(player);
                                Object new_e_player = craft_player_handle.invoke(new_c_player);
                                Object new_p_connection = new_e_player.getClass().getField("playerConnection").get(new_e_player);

                                packet_connect_send.invoke(packet_connection.cast(new_p_connection), remove_wither);

                                boss_bars.remove(id);
                                wither_objects.remove(id);
                                bars--;
                            }
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            bar_timer.setCancelled();
                        }
                    }).addActionOnCancel(() -> {
                        try {
                            Object remove_wither = packet_play_out_destroy.getConstructor(utils.getMinecraftClass("EntityLiving")).newInstance(player.getUniqueId());

                            packet_connection = utils.getMinecraftClass("PlayerConnection");
                            packet_class = utils.getMinecraftClass("Packet");

                            craft_player = utils.getMinecraftClass("entity.CraftPlayer");

                            if (craft_player != null) {
                                Object new_c_player = craft_player.cast(player);
                                Object new_e_player = craft_player_handle.invoke(new_c_player);
                                Object new_p_connection = new_e_player.getClass().getField("playerConnection").get(new_e_player);

                                packet_connect_send.invoke(packet_connection.cast(new_p_connection), remove_wither);

                                boss_bars.remove(id);
                                wither_objects.remove(id);
                                bars--;
                            }
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            bar_timer.setCancelled();
                        }
                    }).start();

                    AdvancedPluginTimer hp_timer = new AdvancedPluginTimer(plugin, 1, true);
                    hp_timer.addAction(() -> {
                        if (!cancelled) {
                            try {
                                double percentage;
                                switch (progress) {
                                    case UP:
                                        percentage = lived_time / live_time;

                                        wither_set_progress_method.invoke(wither, percentage);

                                        lived_time++;
                                        break;
                                    case DOWN:
                                        percentage = lived_time / live_time;

                                        wither_set_progress_method.invoke(wither, percentage);

                                        lived_time--;
                                        break;
                                    default:
                                    case NONE:
                                        break;
                                }
                            } catch (Throwable ex) {
                                ex.printStackTrace();
                                bar_timer.setCancelled();
                                hp_timer.setCancelled();
                            }
                        } else {
                            bar_timer.setCancelled();
                            hp_timer.setCancelled();
                        }
                    }).start();
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        } else {
            org.bukkit.boss.BossBar wither = Bukkit.getServer().createBossBar(StringUtils.toColor(message), org.bukkit.boss.BarColor.valueOf(color.name()), org.bukkit.boss.BarStyle.valueOf(type.name()));
            for (Player player : players) {
                wither.addPlayer(player);
            }
            wither.setVisible(true);

            wither_objects.put(id, wither);

            bar_timer = new AdvancedPluginTimer(plugin, (int) live_time, false);
            bar_timer.addActionOnEnd(() -> {
                wither.setVisible(false);
                wither.removeAll();

                boss_bars.remove(id);
                wither_objects.remove(id);
                bars--;
            }).addActionOnCancel(() -> {
                wither.setVisible(false);
                wither.removeAll();

                boss_bars.remove(id);
                wither_objects.remove(id);
                bars--;
            }).start();

            AdvancedPluginTimer hp_timer = new AdvancedPluginTimer(plugin, 1, true);
            hp_timer.addAction(() -> {
                if (!cancelled) {
                    try {
                        wither.setColor(org.bukkit.boss.BarColor.valueOf(color.name()));
                        wither.setStyle(org.bukkit.boss.BarStyle.valueOf(type.name()));

                        double percentage;
                        switch (progress) {
                            case UP:
                                percentage = lived_time / live_time;

                                wither.setProgress(percentage);

                                lived_time++;
                                break;
                            case DOWN:
                                percentage = lived_time / live_time;

                                wither.setProgress(percentage);

                                lived_time--;
                                break;
                            default:
                            case NONE:
                                break;
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        bar_timer.setCancelled();
                        hp_timer.setCancelled();
                    }
                } else {
                    bar_timer.setCancelled();
                    hp_timer.setCancelled();
                }
            }).start();
        }
    }

    /**
     * Schedule the bar
     *
     * @param players the players to show the bar
     */
    public final void scheduleBar(Collection<Player> players) {
        b_bars.add(this);
        boss_bars.put(id, this);
        AdvancedPluginTimer timer = new AdvancedPluginTimer(plugin, 20);
        timer.addAction(() -> {
            if (!b_bars.isEmpty() && getBarsAmount() < 4) {
                BossMessage boss = b_bars.get(0);
                boss.displayBar(players);
                b_bars.remove(boss);
            }
        }).start();
    }

    /**
     * Schedule the bar
     *
     * @param player the player to show the bar
     */
    public final void scheduleBar(final Player player) {
        b_bars.add(this);
        boss_bars.put(id, this);
        AdvancedPluginTimer timer = new AdvancedPluginTimer(plugin, 20);
        timer.addAction(() -> {
            if (!b_bars.isEmpty() && getBarsAmount() < 4) {
                BossMessage boss = b_bars.get(0);
                boss.displayBar(Collections.singleton(player));
                b_bars.remove(boss);
            }
        }).start();
    }

    /**
     * Get the amount of bars
     *
     * @return the amount of bars
     */
    public final int getBarsAmount() {
        return bars;
    }

    /**
     * Get the current bar ID
     *
     * @return the bar id
     */
    public final int getBarId() {
        return id;
    }

    /**
     * Update the boss bar message
     *
     * @param _message the new bar message
     * @param restart restart the bar after modifying the message
     * @return if the bar message could be updated
     */
    public final boolean update(final String _message, final boolean restart) {
        try {
            message = _message;

            if (isLegacy) {
                Object wither = wither_objects.get(id);

                wither.getClass().getMethod("setCustomName", String.class).invoke(wither, StringUtils.toColor(message));
            } else {
                org.bukkit.boss.BossBar bar = (org.bukkit.boss.BossBar) wither_objects.get(id);
                bar.setTitle(StringUtils.toColor(message));
                bar.getPlayers().forEach(bar::addPlayer);
            }

            if (bar_timer != null && restart)
                bar_timer.resetTimer();

            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Check if the boss bar is valid
     *
     * @return if the boss bar is valid
     */
    public final boolean isValid() {
        return wither_objects.containsKey(id);
    }

    /**
     * Get if the bar has been cancelled
     *
     * @return if the bar has been cancelled
     */
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * Get the boss bar title
     *
     * @return the bar title
     */
    public final String getTitle() {
        return message;
    }

    /**
     * Get the boss bar color
     *
     * @return the bar color
     */
    public final BossColor getColor() {
        return color;
    }

    /**
     * Get the boss bar style
     *
     * @return the boss bar style
     */
    public final BossType getStyle() {
        return type;
    }

    /**
     * BossMessage utils getters
     */
    public interface getters {

        /**
         * Get a boss message by its
         * ID
         *
         * @param id the bar id
         * @return the boss message from its id
         *
         * @throws BossNotFoundException if there's not any boss message
         * with id {id}
         */
        static BossMessage getByID(final int id) throws BossNotFoundException {
            try {
                if (boss_bars.containsKey(id)) {
                    BossMessage boss = boss_bars.getOrDefault(id, null);
                    if (boss != null)
                        return boss;
                }

                throw new BossNotFoundException(id);
            } catch (Throwable ex) {
                throw new BossNotFoundException(id);
            }
        }
    }

    /**
     * Do reflection stuff
     */
    private void doReflectionStuff() {
        try {
            craft_world = utils.getBukkitClass("CraftWorld");
            Class<?> entity_wither = utils.getMinecraftClass("EntityWither");

            if (entity_wither != null) {
                Class<?> packet_entity_living_out = utils.getMinecraftClass("PacketPlayOutSpawnEntityLiving");
                if (packet_entity_living_out != null) {
                    craft_player = utils.getBukkitClass("entity.CraftPlayer");
                    packet_class = utils.getMinecraftClass("Packet");
                    packet_connection = utils.getMinecraftClass("PlayerConnection");
                    packet_play_out_destroy = utils.getMinecraftClass("PacketPlayOutEntityDestroy");
                    Class<?> packet_play_teleport = utils.getMinecraftClass("PacketPlayOutEntityTeleport");
                    if (packet_play_teleport != null) {
                        wither_constructor = entity_wither.getConstructor(utils.getMinecraftClass("World"));
                        entity_living_constructor = packet_entity_living_out.getConstructor(utils.getMinecraftClass("EntityLiving"));
                        packet_play_teleport_constructor = packet_play_teleport.getConstructor(utils.getMinecraftClass("Entity"));
                        craft_world_handle = craft_world.getMethod("getHandle");
                        craft_player_handle = craft_player.getMethod("getHandle");
                        packet_connect_send = packet_connection.getMethod("sendPacket", packet_class);
                        wither_set_location_method = entity_wither.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
                        wither_set_progress_method = entity_wither.getMethod("setProgress", double.class);
                    }
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}

