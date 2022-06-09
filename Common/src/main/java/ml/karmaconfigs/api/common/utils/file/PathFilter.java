package ml.karmaconfigs.api.common.utils.file;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import java.nio.file.Path;

/**
 * KarmaAPI path filter.
 */
public interface PathFilter {

    /**
     * Accept the path
     *
     * @param path the file path
     * @param name the file name
     * @param extension the file extension ( dir = directory )
     * @return if the path is accepted
     */
    default boolean accept(final String path, final String name, final String extension) {
        return !StringUtils.isNullOrEmpty(path);
    }

    /**
     * Create a path to filter with the {@link PathFilter#accept(String, String, String)}
     *
     * @param source the source to start from
     * @param sub the subdirectories
     * @return the path
     */
    default String createPath(final KarmaSource source, final String... sub) {
        Path main = source.getDataPath();
        for (String str : sub)
            main = main.resolve(str);

        return PathUtilities.getPath(main);
    }
}
