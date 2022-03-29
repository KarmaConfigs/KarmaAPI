package ml.karmaconfigs.api.loader;

import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class APILoader {

    public final static KarmaSource source = new Source();

    public static void main(final String[] args) {
        APISource.addProvider(source);
        APISource.defineDefault(source);
        Path sources = source.getDataPath().resolve("sources");
        PathUtilities.createDirectory(sources);

        URL[] jars = new URL[0];
        try {
            Stream<Path> fetched = Files.list(sources);
            Set<Path> collection = fetched.collect(Collectors.toSet());

            for (Path file : collection) {
                try {
                    if (!Files.isDirectory(file) && PathUtilities.getExtension(file).equalsIgnoreCase("jar")) {
                        jars = addToArray(file.toUri().toURL(), jars);
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        System.out.println(Arrays.toString(jars));

        KarmaClassLoader karmaLoader = new KarmaClassLoader(jars, APILoader.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(karmaLoader);

        KarmaAPI.install();
        karmaLoader.invokeLoader();
    }

    private static URL[] addToArray(final URL url, URL[] urls) {
        URL[] result = new URL[urls.length + 1];
        System.arraycopy(urls, 0, result, 0, urls.length);

        result[urls.length] = url;
        return result;
    }
}
