package ml.karmaconfigs.api.velocity.makeiteasy;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import ml.karmaconfigs.api.common.boss.BossColor;
import ml.karmaconfigs.api.common.boss.BossNotFoundException;
import ml.karmaconfigs.api.common.boss.BossType;
import ml.karmaconfigs.api.common.boss.ProgressiveBar;
import ml.karmaconfigs.api.common.utils.StringUtils;
import ml.karmaconfigs.api.velocity.timer.AdvancedPluginTimer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

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

    private final PluginContainer plugin;

    private String message;
    private final double live_time;
    private static int bars = 0;

    private static final List<BossMessage> b_bars = new ArrayList<>();

    private final static Map<Integer, BossMessage> boss_bars = new LinkedHashMap<>();
    private final static Map<Integer, BossBar> bar_objects = new LinkedHashMap<>();

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
    public BossMessage(final PluginContainer owner, final String _message, final int duration) {
        plugin = owner;

        message = _message;
        live_time = duration;

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

        BossBar bar = BossBar.bossBar(
                Component.text().content(StringUtils.toColor(message)).build(),
                1,
                BossBar.Color.valueOf(color.name()),
                BossBar.Overlay.valueOf(type.name().replace("SEGMENTED", "NOTCHED").replace("SOLID", "PROGRESS")));

        bar_objects.put(id, bar);

        for (Player player : players)
            player.showBossBar(bar);

        bar_timer = new AdvancedPluginTimer(plugin, (int) live_time, false);
        bar_timer.addActionOnEnd(() -> {
            for (Player player : players)
                player.hideBossBar(bar);

            boss_bars.remove(id);
            bar_objects.remove(id);
            bars--;
        }).addActionOnCancel(() -> {
            for (Player player : players)
                player.hideBossBar(bar);

            boss_bars.remove(id);
            bar_objects.remove(id);
            bars--;
        }).start();

        AdvancedPluginTimer hp_timer = new AdvancedPluginTimer(plugin, 1, true);
        hp_timer.addAction(() -> {
            if (!cancelled) {
                try {
                    bar.color(BossBar.Color.valueOf(color.name()));
                    bar.overlay(BossBar.Overlay.valueOf(type.name().replace("SEGMENTED", "NOTCHED").replace("SOLID", "PROGRESS")));

                    double life_value;
                    switch (progress) {
                        case UP:
                            life_value = lived_time / live_time;

                            if (life_value <= 1.0 && life_value >= 0.0) {
                                bar.progress((float) life_value);

                                lived_time++;
                            } else {
                                cancel();
                            }
                            break;
                        case DOWN:
                            life_value = lived_time / live_time;

                            if (life_value <= 1.0 && life_value >= 0.0) {
                                bar.progress((float) life_value);

                                lived_time--;
                            } else {
                                cancel();
                            }
                            break;
                        default:
                        case NONE:
                            break;
                    }
                } catch (Throwable ex) {
                    cancel();
                }
            } else {
                bar_timer.setCancelled();
                hp_timer.setCancelled();
            }
        }).start();
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

            BossBar bar = bar_objects.get(id);
            bar.name(TextComponent.ofChildren(Component.text().content(StringUtils.toColor(message))));

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
        return bar_objects.containsKey(id);
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
}

