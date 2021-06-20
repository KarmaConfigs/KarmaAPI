package ml.karmaconfigs.api.bukkit.soundutil;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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
 * Play sounds to a player
 */
public final class SoundPlayer {

    private final org.bukkit.Sound sound;
    private final JavaPlugin main;

    /**
     * Initialize the sound player with
     * the KarmaSound
     *
     * @param s the sound
     * @param p the plugin
     */
    public SoundPlayer(@NotNull final JavaPlugin p, @NotNull final Sound s) {
        main = p;
        sound = s.parseSound();
    }

    /**
     * Play the sound from the specific location
     *
     * @param location the location to play from
     */
    public final void playFrom(final Location location) {
        for (Player player : main.getServer().getOnlinePlayers()) {
            playTo(player, location);
        }
    }

    /**
     * Play the sound from the specific location
     * with the specified pitch and volume
     *
     * @param location the location to play from
     * @param volume the volume of the sound
     * @param pitch the pitch of the sound
     */
    public final void playFrom(final Location location, final Number volume, final Number pitch) {
        for (Player player : main.getServer().getOnlinePlayers()) {
            playTo(player, location, volume.floatValue(), pitch.floatValue());
        }
    }

    /**
     * Play the sound to the player
     *
     * @param player the player
     */
    public final void playTo(@NotNull final Player player) {
        if (isValid(player)) {
            playSound(player, 2.5, 1.0);
        }
    }

    /**
     * Play the sound to the player
     * from the specified location
     *
     * @param player the player
     * @param from the location to play from
     */
    public final void playTo(@NotNull final Player player, @NotNull final Location from) {
        if (isValid(player)) {
            playSound(player, from, 2.5, 1.0);
        }
    }

    /**
     * Play the sound to the player
     * with the specified volume
     * and pitch
     *
     * @param player the player
     * @param volume the volume
     * @param pitch  the pitch
     */
    public final void playTo(@NotNull final Player player, final Number volume, final Number pitch) {
        if (isValid(player)) {
            playSound(player, volume.floatValue(), pitch.floatValue());
        }
    }

    /**
     * Play the sound to the player
     * from the specified location
     * with volume and pitch
     *
     * @param player the player
     * @param from   the location to play from
     * @param volume the volume
     * @param pitch  the pitch
     */
    public final void playTo(@NotNull final Player player, @NotNull final Location from, final Number volume, final Number pitch) {
        if (isValid(player)) {
            playSound(player, from, volume.floatValue(), pitch.floatValue());
        }
    }

    /**
     * Play the sound progressively
     * to the player
     *
     * @param player     the player
     * @param regressive if the sound should
     *                   be played from HIGH to LOW
     * @param period     the note play period (in seconds)
     */
    public final void playProgressivelyNote(@NotNull final Player player, final boolean regressive, final int period) {
        if (isValid(player)) {
            if (regressive) {
                new BukkitRunnable() {
                    double pitch = 2.5;

                    @Override
                    public void run() {
                        playSound(player, 2.5, pitch);
                        if (pitch <= 0.5) {
                            cancel();
                        }
                        pitch = pitch - 0.1;
                    }
                }.runTaskTimer(main, 0, 20L * period);
            } else {
                new BukkitRunnable() {
                    double pitch = 0.5;

                    @Override
                    public void run() {
                        playSound(player, 2.5, pitch);
                        if (pitch >= 2.5) {
                            cancel();
                        }
                        pitch = pitch + 0.1;
                    }
                }.runTaskTimer(main, 0, 20L * period);
            }
        }
    }

    /**
     * Play the sound progressively
     * to the player
     *
     * @param player     the player
     * @param regressive if the sound should
     *                   be played from HIGH to LOW
     * @param period     the note play period (in seconds)
     */
    public final void playProgressivelyVolume(@NotNull final Player player, final boolean regressive, final int period) {
        if (isValid(player)) {
            if (regressive) {
                new BukkitRunnable() {
                    double volume = 2.5;

                    @Override
                    public void run() {
                        playSound(player, volume, 1.0);
                        if (volume <= 0.5) {
                            cancel();
                        }
                        volume = volume - 0.1;
                    }
                }.runTaskTimer(main, 0, 20L * period);
            } else {
                new BukkitRunnable() {
                    double volume = 0.5;

                    @Override
                    public void run() {
                        playSound(player, volume, 1.0);
                        if (volume >= 2.5) {
                            cancel();
                        }
                        volume = volume + 0.1;
                    }
                }.runTaskTimer(main, 0, 20L * period);
            }
        }
    }

    /**
     * Play the sound progressively
     * to the player
     *
     * @param player        the player
     * @param startingPitch the starting pitch value
     * @param regressive    if the sound should
     *                      be played from HIGH to LOW
     * @param period        the note play period (in seconds)
     */
    public final void playProgressivelyNote(@NotNull final Player player, final Number startingPitch, final boolean regressive, final int period) {
        if (isValid(player)) {
            if (regressive) {
                new BukkitRunnable() {
                    double pitch = startingPitch.doubleValue();

                    @Override
                    public void run() {
                        playSound(player, 2.5, pitch);
                        if (pitch <= 0.5) {
                            cancel();
                        }
                        pitch = pitch - 0.1;
                    }
                }.runTaskTimer(main, 0, 20L * period);
            } else {
                new BukkitRunnable() {
                    double pitch = startingPitch.doubleValue();

                    @Override
                    public void run() {
                        playSound(player, 2.5, pitch);
                        if (pitch >= 2.5) {
                            cancel();
                        }
                        pitch = pitch + 0.1;
                    }
                }.runTaskTimer(main, 0, 20L * period);
            }
        }
    }

    /**
     * Play the sound progressively
     * to the player
     *
     * @param player         the player
     * @param startingVolume the starting volume value
     * @param regressive     if the sound should
     *                       be played from HIGH to LOW
     * @param period         the note play period (in seconds)
     */
    public final void playProgressivelyVolume(@NotNull final Player player, final Number startingVolume, final boolean regressive, final int period) {
        if (isValid(player)) {
            if (regressive) {
                new BukkitRunnable() {
                    double volume = startingVolume.doubleValue();

                    @Override
                    public void run() {
                        playSound(player, volume, 1.0);
                        if (volume <= 0.5) {
                            cancel();
                        }
                        volume = volume - 0.1;
                    }
                }.runTaskTimer(main, 0, 20L * period);
            } else {
                new BukkitRunnable() {
                    double volume = startingVolume.doubleValue();

                    @Override
                    public void run() {
                        playSound(player, volume, 1.0);
                        if (volume >= 2.5) {
                            cancel();
                        }
                        volume = volume + 0.1;
                    }
                }.runTaskTimer(main, 0, 20L * period);
            }
        }
    }

    /**
     * Play the sound to the players
     * in the collection
     *
     * @param players the player
     */
    public final void playTo(@NotNull final Collection<Player> players) {
        playSound(players,2.5, 1.0);
    }

    /**
     * Play the sound to the players
     * in the collection from the
     * specified location
     *
     * @param players the player
     * @param from the location to play from
     */
    public final void playTo(@NotNull final Collection<Player> players, @NotNull final Location from) {
        playSound(players, from, 2.5, 1.0);
    }

    /**
     * Play the sound to the players
     * in the collection with the
     * specified volume and pitch
     *
     * @param players the players
     * @param volume  the volume
     * @param pitch   the pitch
     */
    public final void playTo(@NotNull final Collection<Player> players, final Number volume, final Number pitch) {
        playSound(players, volume.floatValue(), pitch.floatValue());
    }

    /**
     * Play the sound to the players
     * in the collection from the
     * specified location with volume
     * and pitch
     *
     * @param players the players
     * @param from    the location to play from
     * @param volume  the volume
     * @param pitch   the pitch
     */
    public final void playTo(@NotNull final Collection<Player> players, @NotNull final Location from, final Number volume, final Number pitch) {
        playSound(players, from, volume.floatValue(), pitch.floatValue());
    }

    /**
     * Play the sound progressively
     * to the players in the collection
     *
     * @param players    the players
     * @param regressive if the sound should
     *                   be played from HIGH to LOW
     * @param period     the note play period (in seconds)
     */
    public final void playProgressivelyNote(@NotNull final Collection<Player> players, final boolean regressive, final int period) {
        if (regressive) {
            new BukkitRunnable() {
                double pitch = 2.5;

                @Override
                public void run() {
                    playSound(players, 2.5, pitch);
                    if (pitch <= 0.5) {
                        cancel();
                    }
                    pitch = pitch - 0.1;
                }
            }.runTaskTimer(main, 0, 20L * period);
        } else {
            new BukkitRunnable() {
                double pitch = 0.5;

                @Override
                public void run() {
                    playSound(players, 2.5, pitch);
                    if (pitch >= 2.5) {
                        cancel();
                    }
                    pitch = pitch + 0.1;
                }
            }.runTaskTimer(main, 0, 20L * period);
        }
    }

    /**
     * Play the sound progressively
     * to the players in the collection
     *
     * @param players    the players
     * @param regressive if the sound should
     *                   be played from HIGH to LOW
     * @param period     the note play period (in seconds)
     */
    public final void playProgressivelyVolume(@NotNull final Collection<Player> players, final boolean regressive, final int period) {
        if (regressive) {
            new BukkitRunnable() {
                double volume = 2.5;

                @Override
                public void run() {
                    playSound(players, volume, 1.0);
                    if (volume <= 0.5) {
                        cancel();
                    }
                    volume = volume - 0.1;
                }
            }.runTaskTimer(main, 0, 20L * period);
        } else {
            new BukkitRunnable() {
                double volume = 0.5;

                @Override
                public void run() {
                    playSound(players, volume, 1.0);
                    if (volume >= 2.5) {
                        cancel();
                    }
                    volume = volume + 0.1;
                }
            }.runTaskTimer(main, 0, 20L * period);
        }
    }

    /**
     * Play the sound progressively
     * to the players in the collection
     *
     * @param players       the players
     * @param startingPitch the starting pitch value
     * @param regressive    if the sound should
     *                      be played from HIGH to LOW
     * @param period        the note play period (in seconds)
     */
    public final void playProgressivelyNote(@NotNull final Collection<Player> players, final Number startingPitch, final boolean regressive, final int period) {
        if (regressive) {
            new BukkitRunnable() {
                double pitch = startingPitch.doubleValue();

                @Override
                public void run() {
                    playSound(players, 2.5, pitch);
                    if (pitch <= 0.5) {
                        cancel();
                    }
                    pitch = pitch - 0.1;
                }
            }.runTaskTimer(main, 0, 20L * period);
        } else {
            new BukkitRunnable() {
                double pitch = startingPitch.doubleValue();

                @Override
                public void run() {
                    playSound(players, 2.5, pitch);
                    if (pitch >= 2.5) {
                        cancel();
                    }
                    pitch = pitch + 0.1;
                }
            }.runTaskTimer(main, 0, 20L * period);
        }
    }

    /**
     * Play the sound progressively
     * to the players in the collection
     *
     * @param players        the players
     * @param startingVolume the starting volume value
     * @param regressive     if the sound should
     *                       be played from HIGH to LOW
     * @param period         the note play period (in seconds)
     */
    public final void playProgressivelyVolume(@NotNull final Collection<Player> players, final Number startingVolume, final boolean regressive, final int period) {
        if (regressive) {
            new BukkitRunnable() {
                double volume = startingVolume.doubleValue();

                @Override
                public void run() {
                    playSound(players, volume, 1.0);
                    if (volume <= 0.5) {
                        cancel();
                    }
                    volume = volume - 0.1;
                }
            }.runTaskTimer(main, 0, 20L * period);
        } else {
            new BukkitRunnable() {
                double volume = startingVolume.doubleValue();

                @Override
                public void run() {
                    playSound(players, volume, 1.0);
                    if (volume >= 2.5) {
                        cancel();
                    }
                    volume = volume + 0.1;
                }
            }.runTaskTimer(main, 0, 20L * period);
        }
    }

    private void playSound(final Player player, Number volume, Number pitch) {
        main.getServer().getScheduler().runTask(main, () -> {
            if (isValid(player)) {
                player.getWorld().playSound(player.getLocation(), sound, volume.floatValue(), pitch.floatValue());
            }
        });
    }

    private void playSound(final Player player, final Location src, Number volume, Number pitch) {
        main.getServer().getScheduler().runTask(main, () -> {
            if (isValid(player)) {
                player.getWorld().playSound(src, sound, volume.floatValue(), pitch.floatValue());
            }
        });
    }

    private void playSound(final Collection<Player> players, Number volume, Number pitch) {
        main.getServer().getScheduler().runTask(main, () -> players.forEach(player -> {
            if (isValid(player)) {
                player.getWorld().playSound(player.getLocation(), sound, volume.floatValue(), pitch.floatValue());
            }
        }));
    }

    private void playSound(final Collection<Player> players, final Location src, Number volume, Number pitch) {
        main.getServer().getScheduler().runTask(main, () -> players.forEach(player -> {
            if (isValid(player)) {
                player.getWorld().playSound(src, sound, volume.floatValue(), pitch.floatValue());
            }
        }));
    }

    private boolean isValid(final Player player) {
        return player != null && player.isOnline() && !player.isDead();
    }
}