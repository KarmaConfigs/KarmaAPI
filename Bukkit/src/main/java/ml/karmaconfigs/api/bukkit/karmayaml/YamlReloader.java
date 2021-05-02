package ml.karmaconfigs.api.bukkit.karmayaml;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class YamlReloader {

    private static String yamlString;
    private final File file;
    private final String fileName;
    private final JavaPlugin Main;

    /**
     * Initialize the yaml reloader
     *
     * @param main     a java plugin instance
     * @param file     the file
     * @param fileName the template file name
     * @throws NotYamlError if the file extension is not .yml
     * @throws IOException if something goes wrong while copying defaults
     */
    public YamlReloader(final JavaPlugin main, final File file, final String fileName) throws NotYamlError, IOException {
        Main = main;

        if (file.getName().contains(".")) {
            String[] nameDat = file.getName().split("\\.");
            String ext = nameDat[nameDat.length - 1];
            if (ext.equals("yml")) {
                try {
                    FileCopy copy = new FileCopy(Main, fileName);
                    copy.copy(file);
                } catch (Throwable ex) {
                    throw new IOException(ex);
                }
                this.fileName = fileName;
                this.file = file;
                yamlString = YamlConfiguration.loadConfiguration(file).saveToString();
                return;
            }
        }
        throw new NotYamlError(file);
    }

    /**
     * Read the file and copy
     * it
     *
     * @return if the file could be reloaded and coppied
     */
    public final boolean reloadAndCopy() {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        try {
            fileConfiguration.load(file);
            fileConfiguration.save(file);

            yamlString = fileConfiguration.saveToString();

            FileCopy copy = new FileCopy(Main, fileName);
            copy.copy(file);

            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Get the file yaml as string
     *
     * @return a String
     */
    public final String getYamlString() {
        return yamlString;
    }
}
