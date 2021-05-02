package ml.karmaconfigs.api.common;

import ml.karmaconfigs.api.common.exception.NoJarException;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.common.utils.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class JarInjector {

    private final static Map<Object, Set<File>> injected = new HashMap<>();
    private final File jarFile;

    /**
     * Initialize the injector class with
     * the specified file
     *
     * @param file the file
     * @throws NoJarException if the file to inject
     * is not a jar file
     */
    public JarInjector(@NotNull final File file) throws NoJarException {
        if (!file.isDirectory()) {
            String extension = FileUtilities.getExtension(file);
            if (extension.equals("jar")) {
                jarFile = file;
            } else {
                throw new NoJarException(file);
            }
        } else {
            throw new NoJarException(file);
        }
    }

    /**
     * Downloads the specified URL to the specified file location. Maximum size
     * allowed is <code>Long.MAX_VALUE</code> bytes.
     *
     * @param url location to read
     */
    public final void download(final String url) {
        InputStream is = null;
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        try {
            if (!jarFile.getParentFile().exists())
                Files.createDirectories(jarFile.getParentFile().toPath());

            if (!jarFile.exists())
                Files.createFile(jarFile.toPath());

            TrustManager[] trustManagers = new TrustManager[]{new NvbTrustManager()};
            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustManagers, null);

            // Set connections to use lenient TrustManager and HostnameVerifier
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NvbHostnameVerifier());

            URL url_obj = new URL(url);
            URLConnection connection = url_obj.openConnection();

            if (connection.getContentLengthLong() != jarFile.length()) {
                is = url_obj.openStream();
                rbc = Channels.newChannel(is);
                fos = new FileOutputStream(jarFile);

                ReflectionUtil.tryBroadcast("&3Downloading dependency &f{0}", jarFile.getName());

                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } else {
                ReflectionUtil.tryBroadcast("&3Dependency &f{0} &3already downloaded!", jarFile.getName());
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            ReflectionUtil.tryBroadcast("&cAn error occurred while downloading dependency &f{0}", jarFile.getName());
        } finally {
            try {
                if (rbc != null) {
                    rbc.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
                ReflectionUtil.tryBroadcast("&aDownloaded dependency &f{0}", jarFile.getName());
            } catch (Throwable ignored) {}
        }
    }

    /**
     * Simple <code>TrustManager</code> that allows unsigned certificates.
     */
    private static final class NvbTrustManager implements TrustManager, X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) { }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) { }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    /**
     * Simple <code>HostnameVerifier</code> that allows any hostname and session.
     */
    private static final class NvbHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * Inject the jar into the plugin
     *
     * @param plugin the plugin
     * @return if the jar file could be injected
     */
    public final boolean inject(@NotNull final Object plugin) {
        if (isDownloaded()) {
            Set<File> jarFiles = injected.getOrDefault(plugin, new HashSet<>());

            if (!jarFiles.contains(jarFile)) {
                try {
                    ReflectionUtil.tryBroadcast("&aInjecting dependency &f{0}&e into plugin &f{1}", jarFile.getName(), ReflectionUtil.getName(plugin));

                    // Get the ClassLoader class
                    URLClassLoader cl = (URLClassLoader) plugin.getClass().getClassLoader();
                    Class<?> clazz = URLClassLoader.class;

                    // Get the protected addURL method from the parent URLClassLoader class
                    Method method = clazz.getDeclaredMethod("addURL", URL.class);

                    // Run projected addURL method to add JAR to classpath
                    method.setAccessible(true);
                    method.invoke(cl, jarFile.toURI().toURL());

                    jarFiles.add(jarFile);

                    injected.put(plugin, jarFiles);
                    return true;
                } catch (Throwable ex) {
                    ReflectionUtil.scheduleLog(plugin, Level.GRAVE, ex);
                    ReflectionUtil.scheduleLog(plugin, Level.GRAVE, "Couldn't inject dependency " + jarFile.getName() + " using KarmaAPI");
                    ReflectionUtil.tryBroadcast("&cCouldn't inject dependency &f{0}&c into &f{1}", jarFile.getName(), ReflectionUtil.getName(plugin));
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Check if the file is downloaded
     *
     * @return a boolean
     */
    public final boolean isDownloaded() {
        return jarFile.exists();
    }


}
