package ml.karmaconfigs.api.common.utils;

import ml.karmaconfigs.api.common.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public interface ReflectionUtil {

    /**
     * Try to broadcast a message into bukkit, if fail,
     * it will try it in bungeecord
     *
     * @param message the message
     */
    static void tryBroadcast(@NotNull final String message) {
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");

            Method getServer = bukkitClass.getMethod("getServer");
            getServer.setAccessible(true);

            Object bukkitServer = getServer.invoke(bukkitClass);

            Method getConsoleSender = bukkitServer.getClass().getMethod("getConsoleSender");
            getConsoleSender.setAccessible(true);

            Object bukkitConsoleSender = getConsoleSender.invoke(bukkitServer);
            Method sendMessage = bukkitConsoleSender.getClass().getMethod("sendMessage", String.class);

            sendMessage.invoke(bukkitConsoleSender, StringUtils.toColor(message));
        } catch (Throwable ex) {
            try {
                Class<?> textComponent = Class.forName("net.md_5.bungee.api.chat.TextComponent");

                Method fromLegacyText = textComponent.getMethod("fromLegacyText", String.class);
                fromLegacyText.setAccessible(true);

                Object component = fromLegacyText.invoke(textComponent, StringUtils.toColor(message));

                Class<?> proxyServer = Class.forName("net.md_5.bungee.api.ProxyServer");

                Method getInstance = proxyServer.getMethod("getInstance");
                getInstance.setAccessible(true);

                Object proxy = getInstance.invoke(proxyServer);

                Method getConsole = proxy.getClass().getMethod("getConsole");
                getConsole.setAccessible(true);

                Object bungeeConsole = getConsole.invoke(proxy);
                Method sendMessage = bungeeConsole.getClass().getMethod("sendMessage", Object.class);

                sendMessage.invoke(bungeeConsole, component);
            } catch (Throwable exc) {
                System.out.println(StringUtils.toConsoleColor(message));
            }
        }
    }

    /**
     * Try to broadcast a message into bukkit, if fail,
     * it will try it in bungeecord, the message
     * will be replaced with the replaces in each
     * placeholder "{0}, {1}, {2}"
     *
     * @param message  the message
     * @param replaces the message replaces
     */
    static void tryBroadcast(@NotNull String message, @NotNull Object... replaces) {
        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            String value = replaces[i].toString();

            message = message.replace(placeholder, value);
        }

        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");

            Method getServer = bukkitClass.getMethod("getServer");
            getServer.setAccessible(true);

            Object bukkitServer = getServer.invoke(bukkitClass);

            Method getConsoleSender = bukkitServer.getClass().getMethod("getConsoleSender");
            getConsoleSender.setAccessible(true);

            Object bukkitConsoleSender = getConsoleSender.invoke(bukkitServer);
            Method sendMessage = bukkitConsoleSender.getClass().getMethod("sendMessage", String.class);

            sendMessage.invoke(bukkitConsoleSender, StringUtils.toColor(message));
        } catch (Throwable ex) {
            try {
                Class<?> textComponent = Class.forName("net.md_5.bungee.api.chat.TextComponent");

                Method fromLegacyText = textComponent.getMethod("fromLegacyText", String.class);
                fromLegacyText.setAccessible(true);

                Object component = fromLegacyText.invoke(textComponent, StringUtils.toColor(message));

                Class<?> proxyServer = Class.forName("net.md_5.bungee.api.ProxyServer");

                Method getInstance = proxyServer.getMethod("getInstance");
                getInstance.setAccessible(true);

                Object proxy = getInstance.invoke(proxyServer);

                Method getConsole = proxy.getClass().getMethod("getConsole");
                getConsole.setAccessible(true);

                Object bungeeConsole = getConsole.invoke(proxy);
                Method sendMessage = bungeeConsole.getClass().getMethod("sendMessage", Object.class);

                sendMessage.invoke(bungeeConsole, component);
            } catch (Throwable exc) {
                System.out.println(StringUtils.toConsoleColor(message));
            }
        }
    }

    /**
     * Schedule the log data
     *
     * @param plugin the plugin
     * @param level the log level
     * @param info the log info
     */
    static void scheduleLog(final Object plugin, final Level level, final String info) {
        if (plugin != null) {
            Class<?> loggerClass;

            try {
                loggerClass = Class.forName("ml.karmaconfigs.api.bukkit.Logger");
            } catch (Throwable ex) {
                try {
                    loggerClass = Class.forName("ml.karmaconfigs.api.bungee.Logger");
                } catch (Throwable exc) {
                    loggerClass = null;
                }
            }

            if (loggerClass != null) {
                try {
                    Constructor<?> loggerConstructor = loggerClass.getConstructor(Object.class);
                    loggerConstructor.setAccessible(true);

                    Class<?> logger = (Class<?>) loggerConstructor.newInstance(plugin);

                    Method logError = logger.getMethod("scheduleLog", Level.class, String.class);
                    logError.setAccessible(true);

                    logError.invoke(logger, level, info);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Schedule the log data
     *
     * @param plugin the plugin
     * @param level the log level
     * @param error the log error
     */
    static void scheduleLog(final Object plugin, final Level level, final Throwable error) {
        if (plugin != null) {
            Class<?> loggerClass;

            try {
                loggerClass = Class.forName("ml.karmaconfigs.api.bukkit.Logger");
            } catch (Throwable ex) {
                try {
                    loggerClass = Class.forName("ml.karmaconfigs.api.bungee.Logger");
                } catch (Throwable exc) {
                    loggerClass = null;
                }
            }

            if (loggerClass != null) {
                try {
                    Constructor<?> loggerConstructor = loggerClass.getConstructor(Object.class);
                    loggerConstructor.setAccessible(true);

                    Class<?> logger = (Class<?>) loggerConstructor.newInstance(plugin);

                    Method logError = logger.getMethod("scheduleLog", Level.class, Throwable.class);
                    logError.setAccessible(true);

                    logError.invoke(logger, level, error);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Get the plugin object name
     *
     * @param plugin the plugin object
     * @return the plugin name
     */
    static String getName(final Object plugin) {
        try {
            Method getDescription = plugin.getClass().getMethod("getDescription");
            getDescription.setAccessible(true);

            Object pluginDescription = getDescription.invoke(plugin);

            Method getName = pluginDescription.getClass().getMethod("getName");
            getName.setAccessible(true);

            return (String) getName.invoke(pluginDescription);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return "";
    }

    /**
     * Get the plugin object version
     *
     * @param plugin the plugin object
     * @return the plugin version
     */
    static String getVersion(final Object plugin) {
        try {
            Method getDescription = plugin.getClass().getMethod("getDescription");
            getDescription.setAccessible(true);

            Object pluginDescription = getDescription.invoke(plugin);

            Method getVersion = pluginDescription.getClass().getMethod("getVersion");
            getVersion.setAccessible(true);

            return (String) getVersion.invoke(pluginDescription);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return "";
    }
}
