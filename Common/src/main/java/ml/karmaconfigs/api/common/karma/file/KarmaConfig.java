package ml.karmaconfigs.api.common.karma.file;

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

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.file.element.KarmaArray;
import ml.karmaconfigs.api.common.karma.file.element.KarmaElement;
import ml.karmaconfigs.api.common.karma.file.element.KarmaObject;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static ml.karmaconfigs.api.common.karma.KarmaAPI.source;

/**
 * Karma configuration
 */
public final class KarmaConfig {

    public final KarmaMain mn;

    /**
     * Initialize the configuration
     */
    public KarmaConfig() {
        KarmaMain tmp = null;
        try {
            Path dst = source(false).getDataPath().resolve("config.kf");
            InputStream internal = KarmaConfig.class.getResourceAsStream("/config.kf");

            if (!Files.exists(dst)) {
                if (internal != null) {
                    Files.createDirectories(dst.getParent());
                    Files.createFile(dst);
                    Files.copy(internal, dst, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            if (Files.size(dst) <= 100) {
                if (internal != null) {
                    Files.copy(internal, dst, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            tmp = new KarmaMain(dst);
            tmp.preCache();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        mn = tmp;
    }

    /**
     * Get the console default prefix
     *
     * @param source the source for the prefix
     * @param lvl the level for the prefix
     * @return the console level prefix
     */
    public String consolePrefix(final KarmaSource source, final Level lvl) {
        String placeholder;

        switch (lvl) {
            case OK:
                placeholder = "&b[ &3%project% &b| &2OK &b] >> &9";
                if (mn != null) {
                    KarmaElement element = mn.get("ok_prefix", null);
                    if (element != null && element.isString()) {
                        String rs = element.getObjet().getString();
                        if (!StringUtils.isNullOrEmpty(rs))
                            placeholder = rs;
                    }
                }
                break;
            case INFO:
                placeholder = "&b[ &3%project% &b| &7INFO &b] >> &9";
                if (mn != null) {
                    KarmaElement element = mn.get("info_prefix", null);
                    if (element != null && element.isString()) {
                        String rs = element.getObjet().getString();
                        if (!StringUtils.isNullOrEmpty(rs))
                            placeholder = rs;
                    }
                }
                break;
            case WARNING:
                placeholder = "&b[ &3%project% &b| &6WARNING &b] >> &9";
                if (mn != null) {
                    KarmaElement element = mn.get("warning_prefix", null);
                    if (element != null && element.isString()) {
                        String rs = element.getObjet().getString();
                        if (!StringUtils.isNullOrEmpty(rs))
                            placeholder = rs;
                    }
                }
                break;
            case GRAVE:
            default:
                placeholder = "&b[ &3%project% &b| &cGRAVE &b] >> &9";
                if (mn != null) {
                    KarmaElement element = mn.get("grave_prefix", null);
                    if (element != null && element.isString()) {
                        String rs = element.getObjet().getString();
                        if (!StringUtils.isNullOrEmpty(rs))
                            placeholder = rs;
                    }
                }
                break;
        }

        return placeholder
                .replace("%project%", source.name())
                .replace("%version%", source.version())
                .replace("%author%", source.authors(false, " ; "))
                .replace("%level%", lvl.name().toUpperCase());
    }

    /**
     * Get if the specified level should be debugged in
     * {@link ml.karmaconfigs.api.common.karmafile.karmayaml.FileCopy}
     *
     * @param lvl the level
     * @return if the level is allowed to debug
     */
    public boolean fileDebug(final Level lvl) {
        if (mn != null) {
            KarmaElement element = mn.get("file_copy_debug", new KarmaObject(false));

            if (element.isBoolean()) {
                boolean result = element.getObjet().getBoolean();

                if (result) {
                    KarmaElement levels = mn.get("file_copy_levels", new KarmaArray(
                            new KarmaObject("WARNING"),
                            new KarmaObject("INFO")
                    ));

                    if (levels.isArray()) {
                        KarmaArray array = levels.toLowerCase().getArray();
                        return array.contains(new KarmaObject(lvl.name().toLowerCase()));
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get if the specified level should be debugged in
     * {@link ml.karmaconfigs.api.common.utils.file.FileUtilities} or {@link ml.karmaconfigs.api.common.utils.file.PathUtilities}
     *
     * @param lvl the level
     * @return if the level is allowed to debug
     */
    public boolean utilDebug(final Level lvl) {
        if (mn != null) {
            KarmaElement element = mn.get("file_util_debug", new KarmaObject(false));

            if (element.isBoolean()) {
                boolean result = element.getObjet().getBoolean();

                if (result) {
                    KarmaElement levels = mn.get("file_util_levels", new KarmaArray(
                            new KarmaObject("OK"),
                            new KarmaObject("INFO"),
                            new KarmaObject("WARNING"),
                            new KarmaObject("GRAVE")
                    ));

                    if (levels.isArray()) {
                        KarmaArray array = levels.toLowerCase().getArray();
                        return array.contains(new KarmaObject(lvl.name().toLowerCase()));
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get if the specified level should be debugged in
     * any part of the API.
     * {@link KarmaConfig#fileDebug(Level)} and {@link KarmaConfig#utilDebug(Level)}
     * should not be affected by this
     *
     * @param lvl the level
     * @return if the level is allowed to debug
     */
    public boolean debug(final Level lvl) {
        if (mn != null) {
            KarmaElement element = mn.get("debug", new KarmaObject(true));
            if (element.isBoolean()) {
                boolean result = element.getObjet().getBoolean();
                if (result) {
                    KarmaElement levels = mn.get("debug_levels", new KarmaArray(
                            new KarmaObject("OK"),
                            new KarmaObject("INFO"),
                            new KarmaObject("WARNING"),
                            new KarmaObject("GRAVE")));

                    if (levels.isArray()) {
                        KarmaArray array = levels.toLowerCase().getArray();
                        return array.contains(new KarmaObject(lvl.name()).toLowerCase());
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get if the specified level should be logged in
     * any part of the API.
     *
     * @param lvl the level
     * @return if the level is allowed to log
     */
    public boolean log(final Level lvl) {
        if (mn != null) {
            KarmaElement element = mn.get("logging", new KarmaObject(true));
            if (element.isBoolean()) {
                boolean result = element.getObjet().getBoolean();
                if (result) {
                    KarmaElement levels = mn.get("logging_levels", new KarmaArray(
                            new KarmaObject("OK"),
                            new KarmaObject("INFO"),
                            new KarmaObject("WARNING"),
                            new KarmaObject("GRAVE")));

                    if (levels.isArray()) {
                        KarmaArray array = levels.toLowerCase().getArray();
                        return array.contains(new KarmaObject(lvl.name()).toLowerCase());
                    }
                }
            }
        }

        return true;
    }
}
