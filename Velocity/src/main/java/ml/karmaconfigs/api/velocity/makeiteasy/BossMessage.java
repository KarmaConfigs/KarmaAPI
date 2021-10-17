package ml.karmaconfigs.api.velocity.makeiteasy;

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

import com.velocitypowered.api.proxy.Player;
import ml.karmaconfigs.api.common.boss.*;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.SourceSecondsTimer;
import ml.karmaconfigs.api.common.timer.scheduler.SimpleScheduler;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

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
    private static final Map<Integer, BossBar> bar_objects = new LinkedHashMap<>();

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
        Collection<Player> fixed = new ArrayList<>();
        for (Player player : players) {
            if (player != null && !shown.contains(player.getUniqueId())) {
                fixed.add(player);
                shown.add(player.getUniqueId());
            }
        }

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

        BossBar bar = BossBar.bossBar(
                Component.text().content(StringUtils.toColor(message)),
                        1.0f,
                        BossBar.Color.valueOf(color.name()),
                        BossBar.Overlay.valueOf(type.name().replace("SEGMENTED", "NOTCHED").replace("SOLID", "PROGRESS")));
        for (Player player : fixed)
            player.showBossBar(bar);

        bar_objects.put(id, bar);
        bar_timer = new SourceSecondsTimer(plugin, live_time, false);

        bar_timer.endAction(() -> {
            for (Player player : fixed) {
                player.hideBossBar(bar);
                shown.remove(player.getUniqueId());
            }
            boss_bars.remove(id);
            bar_objects.remove(id);

            bars--;
        }).cancelAction(time -> {
            for (Player player : fixed) {
                player.hideBossBar(bar);
                shown.remove(player.getUniqueId());
            }
            boss_bars.remove(id);
            bar_objects.remove(id);

            bars--;
        }).start();

        SimpleScheduler hp_timer = new SourceSecondsTimer(plugin, live_time - 1.0, false);
        hp_timer.secondChangeAction(second -> {
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
                            life_value = second / live_time;
                            if (life_value <= 1.0 && life_value >= 0.0) {
                                bar.progress((float) life_value);
                                lived_time--;
                            } else {
                                cancel();
                            }
                            break;
                    }
                } catch (Throwable ex) {
                    cancel();
                }
            } else {
                bar_timer.cancel();
                hp_timer.cancel();
            }
        }).start();
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

        SimpleScheduler timer = new SourceSecondsTimer(plugin, 1, false).multiThreading(true);
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

        SimpleScheduler timer = new SourceSecondsTimer(plugin, 1, false).multiThreading(true);
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
            BossBar bar = bar_objects.get(id);
            bar.name(Component.text().content(StringUtils.toColor(message)).build());
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
        return bar_objects.containsKey(id);
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
                    final BossMessage boss = boss_bars.getOrDefault(id, null);
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
