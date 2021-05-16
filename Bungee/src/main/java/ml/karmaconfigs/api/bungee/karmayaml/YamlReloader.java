package ml.karmaconfigs.api.bungee.karmayaml;

import ml.karmaconfigs.api.common.utils.FileUtilities;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
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
    private final Plugin Main;

    /**
     * Initialize the yaml reloader
     *
     * @param main     a java plugin instance
     * @param file     the file
     * @param fileName the template file name
     * @throws NotYamlError if the file extension is not .yml
     * @throws IOException if something goes wrong while copying defaults
     */
    public YamlReloader(@NotNull final Plugin main, @NotNull final File file, @NotNull final String fileName) throws NotYamlError, IOException {
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
            FileCopy copy = new FileCopy(Main, fileName);
            copy.copy(file);

            return YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (Throwable ex) {
            return null;
        }
    }
}