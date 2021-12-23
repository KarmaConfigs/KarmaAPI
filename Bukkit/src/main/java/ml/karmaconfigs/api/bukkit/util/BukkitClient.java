package ml.karmaconfigs.api.bukkit.util;

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
import ml.karmaconfigs.api.bukkit.reflection.BarMessage;
import ml.karmaconfigs.api.bukkit.reflection.TitleMessage;
import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.utils.placeholder.GlobalPlaceholderEngine;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Karma bukkit client
 */
public class BukkitClient extends Client {

    private final static Set<KarmaPlugin> placeholders = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    private final KarmaPlugin plugin;
    private final Player player;

    /**
     * Initialize the bukkit client
     *
     * @param owner the client owner
     * @param client the client
     */
    public BukkitClient(final KarmaPlugin owner, final Player client) {
        plugin = owner;
        player = client;
    }
    
    /**
     * Send a message to the client
     *
     * @param message the message
     */
    @Override
    public void sendMessage(String message) {
        if (placeholders.contains(plugin)) {
            GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(plugin);
            message = engine.parse(message, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(false));
            message = engine.parse(message, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
            message = engine.parse(message, player);
        }
        
        player.sendMessage(StringUtils.toColor(message));
    }

    /**
     * Send a message to the client
     *
     * @param message  the message
     * @param replaces the message replaces
     */
    @Override
    public void sendMessage(String message, Object... replaces) {
        if (placeholders.contains(plugin)) {
            GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(plugin);
            message = engine.parse(message, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(false));
            message = engine.parse(message, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
            message = engine.parse(message, player);
        }
        
        player.sendMessage(StringUtils.toColor(StringUtils.formatString(message, replaces)));
    }

    /**
     * Send a title to the client
     *
     * @param title    the title
     * @param subtitle the subtitle
     */
    @Override
    public void sendTitle(String title, String subtitle) {
        if (placeholders.contains(plugin)) {
            GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(plugin);
            title = engine.parse(title, player);
            subtitle = engine.parse(subtitle, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(false));
            title = engine.parse(title, player);
            subtitle = engine.parse(subtitle, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
            title = engine.parse(title, player);
            subtitle = engine.parse(subtitle, player);
        }
        
        TitleMessage message = new TitleMessage(player, title, subtitle);
        message.send();
    }

    /**
     * Send a title to the client
     *
     * @param title    the title
     * @param subtitle the subtitle
     * @param showIn   the time to show in
     * @param keepIn   the time to keep in
     * @param hideIn   the time to hide in
     */
    @Override
    public void sendTitle(String title, String subtitle, int showIn, int keepIn, int hideIn) {
        if (placeholders.contains(plugin)) {
            GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(plugin);
            title = engine.parse(title, player);
            subtitle = engine.parse(subtitle, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(false));
            title = engine.parse(title, player);
            subtitle = engine.parse(subtitle, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
            title = engine.parse(title, player);
            subtitle = engine.parse(subtitle, player);
        }
        
        TitleMessage message = new TitleMessage(player, title, subtitle);
        message.send(showIn, keepIn, hideIn);
    }

    /**
     * Send an action bar to the client
     *
     * @param message the action bar message
     * @param repeats the action bar repeats
     */
    @Override
    public void sendActionBar(String message, int repeats) {
        if (placeholders.contains(plugin)) {
            GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(plugin);
            message = engine.parse(message, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(false));
            message = engine.parse(message, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
            message = engine.parse(message, player);
        }

        BarMessage bar = new BarMessage(player, message);
        bar.send(repeats);
    }

    /**
     * Send an action bar to the client
     *
     * @param message    the action bar message
     * @param persistent if the action bar should be visible for ever
     */
    @Override
    public void sendActionBar(String message, boolean persistent) {
        if (placeholders.contains(plugin)) {
            GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(plugin);
            message = engine.parse(message, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(false));
            message = engine.parse(message, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
            message = engine.parse(message, player);
        }

        BarMessage bar = new BarMessage(player, message);
        bar.send(persistent);
    }

    /**
     * Disconnect the client
     *
     * @param reason the disconnect reason
     */
    @Override
    public void disconnect(List<String> reason) {
        if (placeholders.contains(plugin)) {
            GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(plugin);
            reason = engine.parse(reason, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(false));
            reason = engine.parse(reason, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
            reason = engine.parse(reason, player);
        }
        
        player.kickPlayer(StringUtils.toColor(StringUtils.listToString(reason, false)));
    }

    /**
     * Disconnect the client
     *
     * @param reason the disconnect reason
     */
    @Override
    public void disconnect(String... reason) {
        if (placeholders.contains(plugin)) {
            GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(plugin);
            reason = engine.parse(reason, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(false));
            reason = engine.parse(reason, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
            reason = engine.parse(reason, player);
        }

        player.kickPlayer(StringUtils.toColor(StringUtils.listToString(Arrays.asList(reason), false)));
    }

    /**
     * Disconnect the client
     *
     * @param reason the disconnect reason
     */
    @Override
    public void disconnect(String reason) {
        if (placeholders.contains(plugin)) {
            GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(plugin);
            reason = engine.parse(reason, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(false));
            reason = engine.parse(reason, player);
            engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
            reason = engine.parse(reason, player);
        }

        player.kickPlayer(StringUtils.toColor(reason));
    }

    /**
     * Set the current plugin placeholder status
     * for bukkit client
     * 
     * @param status the placeholder status
     */
    public void setPlaceholders(final boolean status) {
        if (status) {
            placeholders.add(plugin);
        } else {
            placeholders.remove(plugin);
        }
    }

    /**
     * Get if the current plugin has placeholders.contains(plugin) 
     * enabled for bukkit client
     * 
     * @return if the bukkit client uses placeholders.contains(plugin)
     * system
     */
    public boolean hasPlaceholders() {
        return placeholders.contains(plugin);
    }
}
