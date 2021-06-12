package ml.karmaconfigs.api.common.utils;

import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.utils.enums.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

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
                Console.send(message);
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
                Console.send(message, replaces);
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
            try {
                Class<?> loggerClass = Class.forName("ml.karmaconfigs.api.bukkit.Logger");
                Class<?> bukkitPlugin = Class.forName("org.bukkit.plugin.java.JavaPlugin");

                Constructor<?> loggerConstructor = loggerClass.getConstructor(bukkitPlugin);
                loggerConstructor.setAccessible(true);

                Class<?> logger = (Class<?>) loggerConstructor.newInstance(plugin);

                Method logError = logger.getMethod("scheduleLog", Level.class, String.class);
                logError.setAccessible(true);

                logError.invoke(logger, level, info);
            } catch (Throwable ex) {
                try {
                    Class<?> loggerClass = Class.forName("ml.karmaconfigs.api.bungee.Logger");
                    Class<?> bungeePlugin = Class.forName("net.md_5.bungee.api.plugin.Plugin");

                    Constructor<?> loggerConstructor = loggerClass.getConstructor(bungeePlugin);
                    loggerConstructor.setAccessible(true);

                    Class<?> logger = (Class<?>) loggerConstructor.newInstance(plugin);

                    Method logError = logger.getMethod("scheduleLog", Level.class, String.class);
                    logError.setAccessible(true);

                    logError.invoke(logger, level, info);
                } catch (Throwable exc) {
                    try {
                        Class<?> loggerClass = Class.forName("ml.karmaconfigs.api.velocity.Logger");
                        Class<?> velocityPlugin = Class.forName("com.velocitypowered.api.plugin.PluginContainer");

                        Constructor<?> loggerConstructor = loggerClass.getConstructor(velocityPlugin);
                        loggerConstructor.setAccessible(true);

                        Class<?> logger = (Class<?>) loggerConstructor.newInstance(plugin);

                        Method logError = logger.getMethod("scheduleLog", Level.class, String.class);
                        logError.setAccessible(true);

                        logError.invoke(logger, level, info);
                    } catch (Throwable exce) {
                        exce.printStackTrace();
                    }
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
            try {
                Class<?> loggerClass = Class.forName("ml.karmaconfigs.api.bukkit.Logger");
                Class<?> bukkitPlugin = Class.forName("org.bukkit.plugin.java.JavaPlugin");

                Constructor<?> loggerConstructor = loggerClass.getConstructor(bukkitPlugin);
                loggerConstructor.setAccessible(true);

                Class<?> logger = (Class<?>) loggerConstructor.newInstance(plugin);

                Method logError = logger.getMethod("scheduleLog", Level.class, Throwable.class);
                logError.setAccessible(true);

                logError.invoke(logger, level, error);
            } catch (Throwable ex) {
                try {
                    Class<?> loggerClass = Class.forName("ml.karmaconfigs.api.bungee.Logger");
                    Class<?> bungeePlugin = Class.forName("net.md_5.bungee.api.plugin.Plugin");

                    Constructor<?> loggerConstructor = loggerClass.getConstructor(bungeePlugin);
                    loggerConstructor.setAccessible(true);

                    Class<?> logger = (Class<?>) loggerConstructor.newInstance(plugin);

                    Method logError = logger.getMethod("scheduleLog", Level.class, Throwable.class);
                    logError.setAccessible(true);

                    logError.invoke(logger, level, error);
                } catch (Throwable exc) {
                    try {
                        Class<?> loggerClass = Class.forName("ml.karmaconfigs.api.velocity.Logger");
                        Class<?> velocityPlugin = Class.forName("com.velocitypowered.api.plugin.PluginContainer");

                        Constructor<?> loggerConstructor = loggerClass.getConstructor(velocityPlugin);
                        loggerConstructor.setAccessible(true);

                        Class<?> logger = (Class<?>) loggerConstructor.newInstance(plugin);

                        Method logError = logger.getMethod("scheduleLog", Level.class, Throwable.class);
                        logError.setAccessible(true);

                        logError.invoke(logger, level, error);
                    } catch (Throwable exce) {
                        exce.printStackTrace();
                    }
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

            Method getVersion = pluginDescription.getClass().getMethod("getName");
            getVersion.setAccessible(true);

            return (String) getVersion.invoke(pluginDescription);
        } catch (Throwable ex) {
            try {
                Class<?> pluginContainer = null;
                try {
                    pluginContainer = Class.forName("com.velocitypowered.api.plugin.PluginContainer");
                } catch (Throwable ignored) {}

                Object pluginDescription;
                if (pluginContainer != null) {
                    if (pluginContainer.isAssignableFrom(plugin.getClass()) || plugin.getClass().isAssignableFrom(pluginContainer)) {
                        pluginDescription = plugin.getClass().getMethod("getDescription").invoke(plugin);

                        Method getName = pluginDescription.getClass().getMethod("getName");

                        Optional<String> name = (Optional<String>) getName.invoke(pluginDescription);
                        if (name.isPresent())
                            return name.get();
                    }
                }
            } catch (Throwable exc) {
                exc.printStackTrace();
            }
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
            try {
                Method getDescription = plugin.getClass().getDeclaredMethod("getDescription");
                getDescription.setAccessible(true);

                Object pluginDescription = getDescription.invoke(plugin);

                Method getVersion = pluginDescription.getClass().getMethod("getVersion");
                getVersion.setAccessible(true);

                Optional<String> version = (Optional<String>) getVersion.invoke(pluginDescription);
                return version.orElse("");
            } catch (Throwable exc) {
                exc.printStackTrace();
            }
        }

        return "";
    }
}
