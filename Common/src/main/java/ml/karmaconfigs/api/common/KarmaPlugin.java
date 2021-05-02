package ml.karmaconfigs.api.common;

import ml.karmaconfigs.api.common.utils.ReflectionUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface KarmaPlugin {

    /**
     * Get the plugin name
     *
     * @return the set plugin name
     */
    String plugin_name() default "";

    /**
     * Get the plugin version
     *
     * @return the set plugin version
     */
    String plugin_version() default "";

    /**
     * Get the plugin update url
     *
     * @return the set plugin update url
     */
    String plugin_update_url() default "";

    /**
     * Private GSA code
     * <p>
     * The use of this code
     * without GSA team authorization
     * will be a violation of
     * terms of use determined
     * in <a href="https://karmaconfigs.ml/license/"> here </a>
     */
    interface getters {

        /**
         * Get the plugin name reading
         * from KarmaPlugin annotation in
         * the specified plugin
         *
         * @param plugin the plugin to read from
         * @return the set plugin name
         */
        static String getName(Object plugin) {
            if (plugin.getClass().isAnnotationPresent(KarmaPlugin.class)) {
                KarmaPlugin karma_plugin = plugin.getClass().getAnnotation(KarmaPlugin.class);

                if (!karma_plugin.plugin_name().isEmpty()) {
                    return karma_plugin.plugin_name();
                } else {
                    return ReflectionUtil.getName(plugin);
                }
            }

            ReflectionUtil.tryBroadcast("&cNo @KarmaPlugin annotation found in &f{0}&c for plugin name", plugin.getClass());
            return "";
        }

        /**
         * Get the plugin version reading
         * from KarmaPlugin annotation in
         * the specified plugin
         *
         * @param plugin the plugin to read from
         * @return the set plugin version
         */
        static String getVersion(Object plugin) {
            if (plugin.getClass().isAnnotationPresent(KarmaPlugin.class)) {
                KarmaPlugin karma_plugin = plugin.getClass().getAnnotation(KarmaPlugin.class);

                if (!karma_plugin.plugin_version().isEmpty()) {
                    return karma_plugin.plugin_version();
                } else {
                    return ReflectionUtil.getVersion(plugin);
                }
            }

            ReflectionUtil.tryBroadcast("&cNo @KarmaPlugin annotation found in &f{0}&c for plugin version", plugin.getClass());
            return "";
        }

        /**
         * Get the plugin update url reading
         * from KarmaPlugin annotation in
         * the specified class
         *
         * @param clazz the class to read from
         * @return the set plugin update url
         */
        static String getUpdateURL(Class<?> clazz) {
            if (clazz.isAnnotationPresent(KarmaPlugin.class)) {
                KarmaPlugin karma_plugin = clazz.getAnnotation(KarmaPlugin.class);

                if (!karma_plugin.plugin_update_url().isEmpty()) {
                    return karma_plugin.plugin_update_url();
                } else {
                    ReflectionUtil.tryBroadcast("&cNo plugin_update_url value in @KarmaPlugin annotation found in &f{0}", clazz);
                    return "";
                }
            }

            ReflectionUtil.tryBroadcast("&cNo @KarmaPlugin annotation found in &f{0}&c for update URL", clazz);
            return "";
        }
    }
}
