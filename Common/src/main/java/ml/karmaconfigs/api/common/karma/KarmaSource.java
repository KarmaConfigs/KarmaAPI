package ml.karmaconfigs.api.common.karma;

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

import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.timer.scheduler.SimpleScheduler;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Karma API source
 */
public interface KarmaSource extends Serializable {

    /**
     * Karma source name
     *
     * @return the source name
     */
    String name();

    /**
     * Karma source version
     *
     * @return the source version
     */
    String version();

    /**
     * Karma source description
     *
     * @return the source description
     */
    String description();

    /**
     * Karma source authors
     *
     * @return the source authors
     */
    String[] authors();

    /**
     * Karma source update URL
     *
     * @return the source update URL
     */
    String updateURL();

    /**
     * Get the authors using a custom separator
     *
     * @param firstSeparator if the first object should have separator
     * @param separator the separator
     * @return the authors using the separator options
     */
    default String authors(final boolean firstSeparator, final String separator) {
        String[] authors = authors();
        StringBuilder builder = new StringBuilder();
        for (String author : authors)
            builder.append(separator).append(author);
        if (firstSeparator)
            return builder.toString();
        return builder.toString().replaceFirst(separator, "");
    }

    /**
     * Get the source out
     *
     * @return the source out
     */
    default Console out() {
        return new Console(this);
    }

    /**
     * Get the source file
     *
     * @return the source file
     */
    default File getSourceFile() {
        File mainJar = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        return FileUtilities.getFixedFile(mainJar);
    }

    /**
     * Get the source data path
     *
     * @return the source data path
     */
    default Path getDataPath() {
        File dataFolder, mainJar = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        File parent = mainJar.getParentFile();
        if (StringUtils.isNullOrEmpty(name())) {
            dataFolder = new File(parent, StringUtils.randomString(5, StringUtils.StringGen.ONLY_LETTERS, StringUtils.StringType.ALL_UPPER));
        } else {
            dataFolder = new File(parent, name());
        }
        return dataFolder.toPath();
    }

    /**
     * Stop all the source tasks
     */
    default void stopTasks() {
        SimpleScheduler.cancelFor(this);
    }

    /**
     * Get if the source is an instance of this
     *
     * @param source the source
     * @return if the source is this source
     */
    default boolean isSource(final KarmaSource source) {
        return (getClass().getName().equalsIgnoreCase(source.getClass().getName()) && name().equals(source.name()));
    }
}
