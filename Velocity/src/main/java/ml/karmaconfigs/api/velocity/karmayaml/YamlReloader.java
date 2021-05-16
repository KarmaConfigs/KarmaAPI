package ml.karmaconfigs.api.velocity.karmayaml;

import com.velocitypowered.api.plugin.PluginContainer;
import ml.karmaconfigs.api.bungee.Configuration;
import ml.karmaconfigs.api.bungee.YamlConfiguration;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.velocity.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
@SuppressWarnings("unused")
public final class YamlReloader {

    private final File file;
    private final String fileName;
    private final PluginContainer main;

    /**
     * Initialize the yaml reloader
     *
     * @param main     a java plugin instance
     * @param file     the file
     * @param fileName the template file name
     * @throws NotYamlError if the file extension is not .yml
     * @throws IOException if something goes wrong while copying defaults
     */
    public YamlReloader(@NotNull final PluginContainer main, @NotNull final File file, @NotNull final String fileName) throws NotYamlError, IOException {
        Util util = new Util(main);
        util.initialize();

        this.main = main;

        if (file.getName().contains(".")) {
            String[] nameDat = file.getName().split("\\.");
            String ext = nameDat[nameDat.length - 1];
            if (ext.equals("yml")) {
                try {
                    FileCopy copy = new FileCopy(this.main, fileName);
                    copy.copy(file);
                } catch (Throwable ex) {
                    throw new IOException(ex);
                }

                this.fileName = fileName;
                this.file = FileUtilities.getFixedFile(file);
                Configuration yamlString = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
                YamlConfiguration.getProvider(YamlConfiguration.class).save(yamlString, file);
                return;
            }
        }
        throw new NotYamlError(file);
    }

    /**
     * Execute a second file copy
     * to keep comments
     * <p>
     * In bungeecord, you don't have to
     * load from string, since you cant
     * BungeeCord YamlConfiguration API
     * will save automatically the file
     * <p>
     * What this method does is restore comments
     * after saving file using YamlConfiguration,
     * since YamlConfiguration doesn't actually support
     * comments and removes them when saving
     *
     * @return the updated file
     */
    @Nullable
    public final Configuration reloadAndCopy() {
        try {
            FileCopy copy = new FileCopy(main, fileName);
            copy.copy(file);

            return YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (Throwable ex) {
            return null;
        }
    }
}