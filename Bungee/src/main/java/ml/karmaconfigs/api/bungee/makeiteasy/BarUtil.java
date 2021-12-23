package ml.karmaconfigs.api.bungee.makeiteasy;

/*
 * Copyright 2019 Ivan Pekov (MrIvanPlays)

 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/

import ml.karmaconfigs.api.common.boss.BossColor;
import ml.karmaconfigs.api.common.boss.BossFlag;
import ml.karmaconfigs.api.common.boss.BossType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.BossBar;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a boss bar
 */
final class BarUtil {

    /**
     * Boss bar title
     */
    private BaseComponent[] title;

    /**
     * Boss bar color
     */
    private BossColor color;
    /**
     * Boss bar style
     */
    private BossType style;
    /**
     * Boss bar flags
     */
    private final Set<BossFlag> flags;

    /**
     * Boss bar health
     */
    private float health;

    /**
     * Boss bar uuid
     */
    private final UUID uuid;

    /**
     * If the boss bar is visible
     */
    private boolean visible;

    /**
     * Players that will see the boss bar
     */
    private final List<ProxiedPlayer> players;

    /**
     * Creates a new boss bar
     *
     * @param title boss bar title
     * @param color boss bar color
     * @param style boss bar style
     * @param health boss bar health. Should be a number between 0.1 and 1
     * @throws IllegalStateException if the health is not a valid health value
     */
    public BarUtil(final @NotNull BaseComponent[] title, final @NotNull BossColor color, final @NotNull BossType style, final float health) throws IllegalArgumentException {
        this.title = title;
        this.color = color;
        this.style = style;
        this.health = health;
        this.flags = EnumSet.noneOf(BossFlag.class);
        this.uuid = UUID.randomUUID();
        this.players = new ArrayList<>();
        this.visible = true;

        if (health > 1 || health < 0.1) {
            throw new IllegalArgumentException("The boss health must be between 0.1 and 1. Provided: " + health);
        }
    }

    /**
     * Creates a new boss bar
     *
     * @param title boss bar title
     * @param color boss bar color
     * @param style boss bar style
     */
    public BarUtil(final @NotNull BaseComponent[] title, final @NotNull BossColor color, final @NotNull BossType style) {
        this(title, color, style, 1);
    }

    /**
     * Creates a new boss bar
     *
     * @param title boss bar title
     */
    public BarUtil(final @NotNull BaseComponent[] title) {
        this(title, BossColor.PINK, BossType.SOLID);
    }

    /**
     * Adds all players to the boss bar
     *
     * @param players the players you wish to add
     * @see #addPlayer(ProxiedPlayer)
     */
    void addPlayers(final @NotNull Iterable<ProxiedPlayer> players) {
        for (ProxiedPlayer player : players) {
            addPlayer(player);
        }
    }

    /**
     * Adds a player to the boss bar. This makes the player see the boss bar if it is visible.
     *
     * @param player the player you wish to add
     */
    void addPlayer(final @NotNull ProxiedPlayer player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        if (player.isConnected() && visible) {
            sendPacket(player, addPacket());
        }
    }

    /**
     * Removes all specified players from the boss bar
     *
     * @param playerArray the players you wish to remove
     * @see #removePlayer(ProxiedPlayer)
     */
    void removePlayers(final @NotNull ProxiedPlayer... playerArray) {
        Iterator<ProxiedPlayer> iterator = players.iterator();
        Set<UUID> uuids = new HashSet<>();
        for (ProxiedPlayer player : playerArray)
            uuids.add(player.getUniqueId());

        if (iterator.hasNext()) {
            do {
                ProxiedPlayer player = iterator.next();

                if (uuids.contains(player.getUniqueId())) {
                    if (player.isConnected() && visible) {
                        sendPacket(player, removePacket());
                        players.remove(player);
                    }
                }
            } while (iterator.hasNext());
        }
    }

    /**
     * Removes all added players from the boss bar
     */
    void removeAllPlayers() {
        Iterator<ProxiedPlayer> iterator = players.iterator();
        if (iterator.hasNext()) {
            do {
                ProxiedPlayer player = iterator.next();

                if (player.isConnected() && visible) {
                    sendPacket(player, removePacket());
                }
            } while (iterator.hasNext());
        }

        players.clear();
    }

    /**
     * Removes a player from the boss bar. This makes the player not see the boss bar no matter if it
     * is visible or not.
     *
     * @param player the player you wish to remove
     */
    void removePlayer(final @NotNull ProxiedPlayer player) {
        if (player.isConnected() && visible) {
            sendPacket(player, removePacket());
        }
        players.remove(player);
    }

    /**
     * Sets a (new) boss bar title (name)
     *
     * @param title the title you wish to set
     */
    void setTitle(final @NotNull BaseComponent... title) {
        this.title = title;
        if (visible) {
            BossBar packet = new BossBar(uuid, 3);
            packet.setTitle(ComponentSerializer.toString(title));
            sendToAffected(packet);
        }
    }

    /**
     * Sets the health of the boss bar. The health is being represented as a number between 0 and 1.
     * The minimum is 0.1 and the maximum is 1
     *
     * @param health the health you wish to set.
     * @throws IllegalArgumentException if the health is not a valid health value
     */
    void setHealth(final float health) throws IllegalArgumentException {
        if (health > 1 || health < 0.1) {
            throw new IllegalArgumentException("The boss health must be between 0.1 and 1. Provided: " + health);
        }

        this.health = health;
        if (visible) {
            BossBar packet = new BossBar(
                    uuid, 2);
            packet.setHealth(health);
            sendToAffected(packet);
        }
    }
    
    /**
     * Sets a (new) color of the boss bar
     *
     * @param newColor the color you wish to set
     */
    void setColor(final @NotNull BossColor newColor) {
        if (visible) {
            color = newColor;
            setDivisions(color, style);
        }
    }

    /**
     * Sets a (new) boss bar style (overlay)
     *
     * @param newStyle the style you wish to set
     */
    void setStyle(final @NotNull BossType newStyle) {
        if (visible) {
            style = newStyle;
            setDivisions(color, style);
        }
    }

    /**
     * Sets this boss bar's visible state
     *
     * @param visible value
     */
    void setVisible(final boolean visible) {
        boolean previous = this.visible;
        if (previous && !visible) {
            sendToAffected(removePacket());
        } else if (!previous && visible) {
            sendToAffected(addPacket());
        }
        this.visible = visible;
    }

    /**
     * Adds flags to the boss bar
     *
     * @param flags the flags you wish to add
     */
    void addFlags(final @NotNull BossFlag... flags) {
        if (this.flags.addAll(Arrays.asList(flags)) && visible) {
            sendToAffected(updateFlags());
        }
    }

    /**
     * Removes a flag from the boss bar
     *
     * @param flag the flag you wish to remove
     */
    void removeFlag(final @NotNull BossFlag flag) {
        if (this.flags.remove(flag) && visible) {
            sendToAffected(updateFlags());
        }
    }

    /**
     * Removes the specified flag(s) from the boss bar
     *
     * @param flags the flag(s) you wish to remove
     */
    void removeFlags(final @NotNull BossFlag... flags) {
        removeFlags(Arrays.asList(flags));
    }

    /**
     * Removes the specified flags from the boss bar
     *
     * @param flags the flags you wish to remove
     */
    void removeFlags(final @NotNull Collection<BossFlag> flags) {
        if (this.flags.removeAll(flags) && visible) {
            sendToAffected(updateFlags());
        }
    }

    /**
     * Returns a unmodifiable list, containing all of the players, added to this boss bar
     *
     * @return players, if no one added empty list
     */
    Collection<ProxiedPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Gets the set title of the boss bar
     *
     * @return title
     */
    BaseComponent[] getTitle() {
        return title;
    }

    /**
     * Gets the health of the boss bar. The health is a number between 0 and 1
     *
     * @return health
     */
    float getHealth() {
        return health;
    }

    /**
     * Gets the color of the boss bar
     *
     * @return color
     */
    BossColor getColor() {
        return color;
    }

    /**
     * Gets the style of the boss bar
     *
     * @return style
     */
    BossType getStyle() {
        return style;
    }

    /**
     * Returns whenever this boss bar is being visible.
     *
     * @return <code>true</code> if visible, otherwise
     * <code>false</code>
     */
    boolean isVisible() {
        return visible;
    }

    /**
     * Returns a unmodifiable set, containing all of the flags, added to this boss bar
     *
     * @return flags, if no one added empty set
     */
    Collection<BossFlag> getFlags() {
        return Collections.unmodifiableSet(flags);
    }

    /**
     * Get the boss bar as string
     * 
     * @return boss bar util as string
     */
    @Override
    public String toString() {
        StringBuilder titleBuilder = new StringBuilder();
        for (BaseComponent component : title) {
            titleBuilder.append(component.toString()).append(", ");
        }

        return "BossBar(" +
                "title=" + titleBuilder +
                ", color=" + color +
                ", style=" + style +
                ", flags=" + flags +
                ", health=" + health +
                ", visible=" + visible +
                ", players=" + players +
                ')';
    }
    
    /**
     * Creates a new boss bar builder
     *
     * @return builder
     */
    public static BarUtil.Builder builder() {
        return new BarUtil.Builder();
    }

    /**
     * Represents a boss bar builder
     */
    public static final class Builder {

        /**
         * The boss bar title
         */
        private BaseComponent[] title;

        /**
         * The boss bar color
         */
        private BossColor color;
        /**
         * The boss bar style
         */
        private BossType style;

        /**
         * The boss bar flags
         */
        private final Set<BossFlag> flags;

        /**
         * The boss bar health
         */
        private float health;


        /**
         * Players that will see the boss bar
         */
        private final List<ProxiedPlayer> players;
        
        /**
         * If the boss bar is visible
         */
        private boolean visible;

        /**
         * Initialize the boss bar
         */
        public Builder() {
            this.title = new ComponentBuilder("KarmaAPI boss bar").color(ChatColor.AQUA).create();
            this.color = BossColor.PINK;
            this.style = BossType.SOLID;
            this.flags = EnumSet.noneOf(BossFlag.class);
            this.health = 1;
            this.players = new ArrayList<>();
            this.visible = true;
        }

        /**
         * Set the boss bar title
         * 
         * @param title the boss bar title
         * @return this instance
         */
        public Builder title(final @NotNull BaseComponent... title) {
            this.title = title;
            return this;
        }

        /**
         * Add a player to the boss bar
         * 
         * @param playerArray the players to add
         * @return this instance
         */
        public Builder player(final @NotNull ProxiedPlayer... playerArray) {
            players.addAll(Arrays.asList(playerArray));
            return this;
        }

        /**
         * Set the boss bar health
         * 
         * @param health the boss bar health
         * @return this instance
         */
        public Builder health(final float health) {
            this.health = health;
            return this;
        }

        /**
         * Set the boss bar flags
         * 
         * @param flags the boss bar flags
         * @return this instance
         */
        public Builder flags(final @NotNull BossFlag... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        /**
         * Set the boss bar color
         * 
         * @param color the boss bar color
         * @return this instance
         */
        public Builder color(final @NotNull BossColor color) {
            this.color = color;
            return this;
        }

        /**
         * Set the boss bar style
         * 
         * @param style the boss bar style
         * @return this instance
         */
        public Builder style(final @NotNull BossType style) {
            this.style = style;
            return this;
        }

        /**
         * Set if the boss bar is visible
         * 
         * @param visible if the boss bar is visible
         * @return this instance
         */
        public Builder visible(final boolean visible) {
            this.visible = visible;
            return this;
        }

        /**
         * Build the boss bar
         * 
         * @return a new bar util instance
         */
        public BarUtil build() {
            BarUtil bossBar = new BarUtil(title, color, style, health);
            bossBar.addFlags(flags.toArray(new BossFlag[0]));
            bossBar.setVisible(visible);
            bossBar.addPlayers(players);
            return bossBar;
        }
    }

    /**
     * Serialize the boss bar flags
     * 
     * @return the serialized flags
     */
    private byte serializeFlags() {
        byte flagMask = 0x0;
        if (flags.contains(BossFlag.DARKEN_SCREEN)) {
            flagMask |= 0x1;
        }
        if (flags.contains(BossFlag.PLAY_BOSS_MUSIC)) {
            flagMask |= 0x2;
        }
        if (flags.contains(BossFlag.CREATE_WORLD_FOG)) {
            flagMask |= 0x4;
        }
        return flagMask;
    }

    /**
     * Set boss bar division
     * 
     * @param color the boss bar color
     * @param style the boss bar style
     */
    private void setDivisions(final @NotNull BossColor color, final @NotNull BossType style) {
        BossBar packet = new BossBar(uuid, 4);
        packet.setColor(color.ordinal());
        packet.setDivision(style.ordinal());
        sendToAffected(packet);
    }

    /**
     * Update the boss bar flags
     * 
     * @return this instance
     */
    private BossBar updateFlags() {
        BossBar packet = new BossBar(
                uuid, 5);
        packet.setFlags(serializeFlags());
        return packet;
    }

    /**
     * Get the boss bar add packet
     * 
     * @return the boss bar add packet
     */
    private BossBar addPacket() {
        BossBar packet = new BossBar(
                uuid, 0);
        packet.setTitle(ComponentSerializer.toString(title));
        packet.setColor(color.ordinal());
        packet.setDivision(style.ordinal());
        packet.setHealth(health);
        packet.setFlags(serializeFlags());
        return packet;
    }

    /**
     * Send the boss bar to the specified players
     * 
     * @param packet the boss bar to send
     */
    private void sendToAffected(final @NotNull DefinedPacket packet) {
        for (ProxiedPlayer player : players) {
            if (player.isConnected() && visible) {
                sendPacket(player, packet);
            }
        }
    }

    /**
     * Send a packet to the player
     * 
     * @param player the player 
     * @param packet the packet
     */
    private void sendPacket(final @NotNull ProxiedPlayer player, final @NotNull DefinedPacket packet) {
        if (player.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_9) {
            player.unsafe().sendPacket(packet);
        }
    }

    /**
     * Remove the boss bar
     * 
     * @return a clean boss bar
     */
    private BossBar removePacket() {
        return new BossBar(uuid, 1);
    }
}