package ml.karmaconfigs.api.bungee.karmayaml;

import ml.karmaconfigs.api.bungee.Console;
import ml.karmaconfigs.api.common.Level;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.ml/license/"> here </a>
 */
public final class YamlManager {

    private final File file;
    private Configuration managed;

    private final Plugin main;

    /**
     * Starts the file manager
     *
     * @param plugin the plugin owner
     * @param fileName the file name ( do not include .yml )
     * @param directory the file directory
     */
    public YamlManager(final Plugin plugin, final String fileName, final String... directory) {
        main = plugin;

        StringBuilder dirBuilder = new StringBuilder();
        if (directory.length > 0) {
            for (String dir : directory)
                dirBuilder.append(File.separator).append(dir);

            file = new File(plugin.getDataFolder() + dirBuilder.toString(), fileName + ".yml");
        } else {
            file = new File(plugin.getDataFolder(), fileName + ".yml");
        }

        try {
            if (!file.getParentFile().exists()) {
                Files.createDirectory(file.getParentFile().toPath());
            }
            if (!file.exists()) {
                Files.createFile(file.toPath());
            }

            managed = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (Throwable ex) {
            Console.send(plugin, "Error while creating/loading file " + fileName, Level.GRAVE);
        }
    }

    /**
     * Set a path with no info
     *
     * @param path the path
     */
    public final void set(String path) {
        managed.set(path, "");
    }

    /**
     * Set a path value as object
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Object value) {
        managed.set(path, value);
    }

    /**
     * Set a path value as object
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, String value) {
        managed.set(path, value);
    }

    /**
     * Set a path value as a string list
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, List<String> value) {
        managed.set(path, value);
    }

    /**
     * Set a path value as boolean
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Boolean value) {
        managed.set(path, value);
    }

    /**
     * Set a path value as integer
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Integer value) {
        managed.set(path, value);
    }

    /**
     * Set a path value as double
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Double value) {
        managed.set(path, value);
    }

    /**
     * Set a path value as float
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Float value) {
        managed.set(path, value);
    }

    /**
     * Removes a path
     *
     * @param path the path
     */
    public final void unset(String path) {
        managed.set(path, null);
    }

    /**
     * Check if the path is
     * empty
     *
     * @param path the path
     * @return if the config value of the path is empty
     */
    public final boolean isEmpty(String path) {
        if (isSet(path)) {
            return get(path).toString().isEmpty();
        } else {
            return true;
        }
    }

    /**
     * Check if the path is
     * set
     *
     * @param path the path
     * @return if the config path is set
     */
    public final boolean isSet(String path) {
        return get(path) != null;
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return the object of the specified file path
     */
    public final Object get(String path) {
        try {
            return managed.get(path);
        } catch (Throwable ex) {
            return "";
        }
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return the string of the specified file path
     */
    public final String getString(String path) {
        try {
            return managed.getString(path, "");
        } catch (Throwable ex) {
            return "";
        }
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return the list of the specified file path
     */
    public final List<String> getList(String path) {
        try {
            return managed.getStringList(path);
        } catch (Throwable ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return the boolean of the specified file path
     */
    public final Boolean getBoolean(String path) {
        try {
            return managed.getBoolean(path);
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return the integer of the specified file path
     */
    public final int getInt(String path) {
        try {
            return managed.getInt(path);
        } catch (Throwable ex) {
            return 0;
        }
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return the double of the specified file path
     */
    public final double getDouble(String path) {
        try {
            return managed.getDouble(path);
        } catch (Throwable ex) {
            return 0D;
        }
    }

    /**
     * Gets the value of a path
     *
     * @param path the path
     * @return the float of the specified file path
     */
    public final float getFloat(String path) {
        try {
            return (float) managed.getDouble(path);
        } catch (Throwable ex) {
            return 0F;
        }
    }

    /**
     * Gets the managed file
     *
     * @return the managed file
     */
    public final File getFile() {
        return file;
    }

    /**
     * Gets the managed file configuration
     *
     * @return YamlConfiguration format file configuration
     */
    public final Configuration getBungeeManager() {
        return managed;
    }

    /**
     * Tries to delete the managed file
     */
    public final void delete() {
        if (file.delete()) {
            Console.send(main, "The file {0} have been removed", Level.INFO, file.getName());
        } else {
            Console.send(main, "The file {0} couldn't be removed", Level.WARNING, file.getName());
        }
    }

    /**
     * Tries to save the managed file values
     *
     * @param internal_name the internal file name
     *                      to store with comments
     */
    public final void save(final String internal_name) {
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(managed, file);
            if (internal_name != null && !internal_name.replaceAll("\\s", "").isEmpty()) {
                YamlReloader reloader = new YamlReloader(main, file, internal_name);
                managed = reloader.reloadAndCopy();
            }
        } catch (Throwable ex) {
            Console.send(main, "Error while saving file " + file.getName().replace(".yml", ""), Level.GRAVE);
        }
    }
}
