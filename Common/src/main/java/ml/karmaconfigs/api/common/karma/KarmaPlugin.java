package ml.karmaconfigs.api.common.karma;

import ml.karmaconfigs.api.common.utils.ReflectionUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * This file is part of KarmaAPI, licensed under the MIT License.
 *
 *  Copyright (c) karma (KarmaDev) <karmaconfigs@gmail.com>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

/**
 * Create a new KarmaPlugin
 *
 * @deprecated Please use {@link KarmaSource}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Deprecated
public @interface KarmaPlugin {

    /**
     * Get the plugin name
     *
     * @return the set plugin name
     */
    String source_name() default "";

    /**
     * Get the plugin version
     *
     * @return the set plugin version
     */
    String source_version() default "";

    /**
     * Get the plugin update url
     *
     * @return the set plugin update url
     */
    String source_update_url() default "";

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

                return karma_plugin.source_name();
            }

            ReflectionUtil.tryBroadcast("&cNo @KarmaPlugin annotation found in &f{0}&c for source name", plugin.getClass());
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

                return karma_plugin.source_version();
            }

            ReflectionUtil.tryBroadcast("&cNo @KarmaPlugin annotation found in &f{0}&c for source version", plugin.getClass());
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

                return karma_plugin.source_update_url();
            }

            ReflectionUtil.tryBroadcast("&cNo @KarmaPlugin annotation found in &f{0}&c for source URL", clazz);
            return "";
        }
    }
}
