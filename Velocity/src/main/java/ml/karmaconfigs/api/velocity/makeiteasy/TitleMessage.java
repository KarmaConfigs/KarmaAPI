package ml.karmaconfigs.api.velocity.makeiteasy;

import com.velocitypowered.api.proxy.Player;
import ml.karmaconfigs.api.common.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
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
