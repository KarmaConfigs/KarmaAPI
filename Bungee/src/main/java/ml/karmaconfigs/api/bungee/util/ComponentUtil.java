package ml.karmaconfigs.api.bungee.util;

import ml.karmaconfigs.api.common.utils.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public class ComponentUtil {

    public static BaseComponent[] toComponent(@NotNull final String text) {
        return TextComponent.fromLegacyText(StringUtils.toColor(text));
    }
}
