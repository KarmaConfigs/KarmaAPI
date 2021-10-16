package ml.karmaconfigs.api.velocity.makeiteasy;

import com.velocitypowered.api.proxy.Player;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

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
 * Title message
 */
public final class TitleMessage {

    private final Player player;
    private final String title, subtitle;

    /**
     * Initialize the title class
     *
     * @param p the player
     * @param t the title text
     * @param s the subtitle text
     */
    public TitleMessage(@NotNull final Player p, @Nullable String t, @Nullable String s) {
        player = p;
        if (t == null)
            t = "";
        title = StringUtils.toColor(t);
        if (s == null)
            s = "";
        subtitle = StringUtils.toColor(s);
    }

    /**
     * Initialize the title class
     *
     * @param p the player
     * @param t the title text
     */
    public TitleMessage(@NotNull final Player p, @Nullable String t) {
        player = p;
        if (t == null)
            t = "";
        title = StringUtils.toColor(t);
        subtitle = "";
    }

    /**
     * Send the title
     */
    public final void send() {
        Title.Times times = Title.Times.of(Duration.ofSeconds(2), Duration.ofSeconds(2), Duration.ofSeconds(2));
        Title server_title = Title.title(
                TextComponent.ofChildren(Component.text().content(StringUtils.toColor(title))),
                TextComponent.ofChildren(Component.text().content(StringUtils.toColor(subtitle))), times);



        player.showTitle(server_title);
    }

    /**
     * Send the title
     *
     * @param showIn the time that will take to
     *               completely show the title
     * @param keepIn the time to keep in
     * @param hideIn the time that will take to
     *               completely hide the title
     */
    public final void send(final int showIn, final int keepIn, final int hideIn) {
        Title.Times times = Title.Times.of(Duration.ofSeconds(showIn), Duration.ofSeconds(keepIn), Duration.ofSeconds(hideIn));
        Title server_title = Title.title(
                TextComponent.ofChildren(Component.text().content(StringUtils.toColor(title))),
                TextComponent.ofChildren(Component.text().content(StringUtils.toColor(subtitle))), times);

        player.showTitle(server_title);
    }
}
