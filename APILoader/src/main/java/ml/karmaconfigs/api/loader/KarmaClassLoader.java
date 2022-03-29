package ml.karmaconfigs.api.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Karma class loader
 */
public class KarmaClassLoader extends URLClassLoader {

    static {
        registerAsParallelCapable();
    }

    /**
     * Constructs a new URLClassLoader for the given URLs. The URLs will be
     * searched in the order specified for classes and resources after first
     * searching in the specified parent class loader. Any URL that ends with
     * a '/' is assumed to refer to a directory. Otherwise, the URL is assumed
     * to refer to a JAR file which will be downloaded and opened as needed.
     *
     * <p>If there is a security manager, this method first
     * calls the security manager's {@code checkCreateClassLoader} method
     * to ensure creation of a class loader is allowed.
     *
     * @param urls   the URLs from which to load classes and resources
     * @param parent the parent class loader for delegation
     * @throws SecurityException    if a security manager exists and its
     *                              {@code checkCreateClassLoader} method doesn't allow
     *                              creation of a class loader.
     * @throws NullPointerException if {@code urls} is {@code null}.
     * @see SecurityManager#checkCreateClassLoader
     */
    public KarmaClassLoader(final URL[] urls, final ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * Appends the specified URL to the list of URLs to search for
     * classes and resources.
     * <p>
     * If the URL specified is {@code null} or is already in the
     * list of URLs, or if this loader is closed, then invoking this
     * method has no effect.
     *
     * @param url the URL to be added to the search path of URLs
     */
    @Override
    public final void addURL(final URL url) {
        super.addURL(url);
    }

    /**
     * Loads the class with the specified <a href="#name">binary name</a>.
     * This method searches for classes in the same manner as the {@link
     * #loadClass(String, boolean)} method.  It is invoked by the Java virtual
     * machine to resolve class references.  Invoking this method is equivalent
     * to invoking {@link #loadClass(String, boolean) <tt>loadClass(name,
     * false)</tt>}.
     *
     * @param name The <a href="#name">binary name</a> of the class
     * @return The resulting <tt>Class</tt> object
     */
    @Override
    public Class<?> loadClass(String name) {
        try {
            return super.loadClass(name);
        } catch (Throwable ex) {
            return null;
        }
    }

    public void invokeLoader() {
        Class<?> clazz = loadClass("ml.karmaconfigs.api.loader.util.Loader");
        if (clazz != null) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor(URL[].class);
                Object instance = constructor.newInstance((Object) getURLs());

                Method load = instance.getClass().getDeclaredMethod("load");
                load.invoke(instance);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Error invoking loader...");
        System.exit(2);
    }
}
