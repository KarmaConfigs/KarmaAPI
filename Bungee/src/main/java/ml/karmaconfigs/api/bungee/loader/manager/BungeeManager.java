package ml.karmaconfigs.api.bungee.loader.manager;

/*
 * GNU LESSER GENERAL PUBLIC LICENSE
 * Version 2.1, February 1999
 * <p>
 * Copyright (C) 1991, 1999 Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 * <p>
 * [This is the first released version of the Lesser GPL.  It also counts
 * as the successor of the GNU Library Public License, version 2, hence
 * the version number 2.1.]
 */

import ml.karmaconfigs.api.bungee.loader.BungeeBridge;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Handler;

public class BungeeManager {

    /**
     * Unload a plugin
     */
    @SuppressWarnings("all")
    public static void unload(final Plugin tarPlugin) {
        IllegalStateException error = new IllegalStateException("Errors occurred while unloading plugin " + tarPlugin.getDescription().getName()) {
            private static final long serialVersionUID = 1L;

            @Override
            public synchronized Throwable fillInStackTrace() {
                return this;
            }
        };

        PluginManager pluginmanager = ProxyServer.getInstance().getPluginManager();
        ClassLoader pluginclassloader = tarPlugin.getClass().getClassLoader();

        try {
            tarPlugin.onDisable();
        } catch (Throwable t) {
            error.addSuppressed(t);
        }

        try {
            for (Handler handler : tarPlugin.getLogger().getHandlers()) {
                handler.close();
            }
        } catch (Throwable t) {
            error.addSuppressed(t);
        }

        try {
            pluginmanager.unregisterListeners(tarPlugin);
        } catch (Throwable t) {
            error.addSuppressed(t);
        }

        try {
            pluginmanager.unregisterCommands(tarPlugin);
        } catch (Throwable t) {
            error.addSuppressed(t);
        }

        try {
            ProxyServer.getInstance().getScheduler().cancel(tarPlugin);
        } catch (Throwable t) {
            error.addSuppressed(t);
        }

        try {
            tarPlugin.getExecutorService().shutdownNow();
        } catch (Throwable t) {
            error.addSuppressed(t);
        }

        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getClass().getClassLoader() == pluginclassloader) {
                try {
                    thread.interrupt();
                    thread.join(2000);
                    if (thread.isAlive()) {
                        thread.stop();
                    }
                } catch (Throwable t) {
                    error.addSuppressed(t);
                }
            }
        }

        EventBusManager.completeIntents(tarPlugin);

        try {
            Map<String, Command> commandMap = Reflections.getFieldValue(pluginmanager, "commandMap");
            commandMap.entrySet().removeIf(entry -> entry.getValue().getClass().getClassLoader() == pluginclassloader);
        } catch (Throwable t) {
            error.addSuppressed(t);
        }

        try {
            Reflections.<Map<String, Plugin>>getFieldValue(pluginmanager, "plugins").values().remove(tarPlugin);
        } catch (Throwable t) {
            error.addSuppressed(t);
        }

        if (pluginclassloader instanceof URLClassLoader) {
            try {
                ((URLClassLoader) pluginclassloader).close();
            } catch (Throwable t) {
                error.addSuppressed(t);
            }
        }

        try {
            Reflections.<Set<ClassLoader>>getStaticFieldValue(pluginclassloader.getClass(), "allLoaders").remove(pluginclassloader);
        } catch (Throwable t) {
            error.addSuppressed(t);
        }

        if (error.getSuppressed().length > 0) {
            error.printStackTrace();
        }
    }

    /**
     * Tries to load a plugin
     *
     * @param pluginFile the plugin
     * @return the loaded plugin
     */
    public static Plugin loadPlugin(File pluginFile) {
        ProxyServer proxy = ProxyServer.getInstance();
        PluginManager pluginmanager = proxy.getPluginManager();

        try (JarFile jar = new JarFile(pluginFile)) {
            JarEntry pdf = jar.getJarEntry("bungee.yml");
            if (pdf == null) {
                pdf = jar.getJarEntry("plugin.yml");
            }
            try (InputStream in = jar.getInputStream(pdf)) {
                //load description
                PluginDescription pluginDescription = new Yaml().loadAs(in, PluginDescription.class);
                pluginDescription.setFile(pluginFile);
                //check depends
                HashSet<String> plugins = new HashSet<>();
                for (Plugin plugin : pluginmanager.getPlugins()) {
                    plugins.add(plugin.getDescription().getName());
                }
                for (String dependency : pluginDescription.getDepends()) {
                    if (!plugins.contains(dependency)) {
                        throw new IllegalArgumentException(MessageFormat.format("Missing plugin dependency {0}", dependency));
                    }
                }
                //load plugin
                Plugin plugin = createPluginInstance(proxy, pluginFile, pluginDescription);
                Reflections.invokeMethod(plugin, "init", proxy, pluginDescription);
                Reflections.<Map<String, Plugin>>getFieldValue(pluginmanager, "plugins").put(pluginDescription.getName(), plugin);
                plugin.onLoad();
                plugin.onEnable();

                return plugin;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static Plugin createPluginInstance(final ProxyServer proxy, File pluginFile, PluginDescription pluginDescription) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException, NoSuchMethodException, SecurityException, ClassNotFoundException {
        Class<?> pluginClassLoaderClass = BungeeBridge.class.getClassLoader().getClass();
        ClassLoader pluginClassLoader = null;
        for (Constructor<?> constructor : pluginClassLoaderClass.getDeclaredConstructors()) {
            Reflections.setAccessible(constructor);
            Parameter[] parameters = constructor.getParameters();
            if (
                    (parameters.length == 3) &&
                            parameters[0].getType().isAssignableFrom(ProxyServer.class) &&
                            parameters[1].getType().isAssignableFrom(PluginDescription.class) &&
                            parameters[2].getType().isAssignableFrom(URL[].class)
            ) {
                pluginClassLoader = (ClassLoader) constructor.newInstance(proxy, pluginDescription, new URL[]{pluginFile.toURI().toURL()});
                break;
            } else if (
                    (parameters.length == 4) &&
                            parameters[0].getType().isAssignableFrom(ProxyServer.class) &&
                            parameters[1].getType().isAssignableFrom(PluginDescription.class) &&
                            parameters[2].getType().isAssignableFrom(File.class) &&
                            parameters[3].getType().isAssignableFrom(ClassLoader.class)
            ) {
                pluginClassLoader = (ClassLoader) constructor.newInstance(proxy, pluginDescription, pluginFile, null);
                break;
            } else if (
                    (parameters.length == 1) &&
                            parameters[0].getType().isAssignableFrom(URL[].class)
            ) {
                pluginClassLoader = (ClassLoader) constructor.newInstance(new Object[]{new URL[]{pluginFile.toURI().toURL()}});
                break;
            }
        }
        if (pluginClassLoader == null) {
            throw new IllegalStateException(MessageFormat.format(
                    "Unable to create PluginClassLoader instance, no suitable constructors found in class {0} constructors {1}",
                    pluginClassLoaderClass, Arrays.toString(pluginClassLoaderClass.getDeclaredConstructors())
            ));
        }
        return (Plugin)
                pluginClassLoader
                        .loadClass(pluginDescription.getMain())
                        .getDeclaredConstructor()
                        .newInstance();
    }
}