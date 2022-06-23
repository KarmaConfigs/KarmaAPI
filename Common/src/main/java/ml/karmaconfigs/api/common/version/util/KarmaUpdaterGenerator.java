package ml.karmaconfigs.api.common.version.util;

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
import ml.karmaconfigs.api.common.karma.file.KarmaMain;
import ml.karmaconfigs.api.common.karma.file.element.KarmaArray;
import ml.karmaconfigs.api.common.karma.file.element.KarmaElement;
import ml.karmaconfigs.api.common.karma.file.element.KarmaObject;
import ml.karmaconfigs.api.common.karmafile.KarmaFile;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Karma update file generator
 */
public final class KarmaUpdaterGenerator {

    /**
     * The update file source
     */
    private final KarmaSource source;

    /**
     * The update file lines
     */
    private final List<String> lines = Collections.synchronizedList(new ArrayList<>());

    /**
     * The update URL
     */
    private URL updateURL = null;

    /**
     * Initialize the karma update file generator
     *
     * @param owner the file source
     */
    public KarmaUpdaterGenerator(final KarmaSource owner) {
        this.source = owner;
    }

    /**
     * Add a line to the changelog
     *
     * @param changelog the changelog lines
     */
    public void addChangelog(final String... changelog) {
        this.lines.addAll(Arrays.asList(changelog));
    }

    /**
     * Add the lines to the changelog
     *
     * @param changelog the changelog lines
     */
    public void addChangelog(final List<String> changelog) {
        this.lines.addAll(changelog);
    }

    /**
     * Remove the changelog lines
     *
     * @param indexes the indexes to remove
     */
    public void removeChangelog(final int... indexes) {
        for (int index : indexes)
            this.lines.remove(index);
    }

    /**
     * Clear the changelog
     */
    public void clearChangelog() {
        this.lines.clear();
    }

    /**
     * Set the changelog
     *
     * @param changelog the changelog
     */
    public void setChangelog(final String... changelog) {
        this.lines.clear();
        this.lines.addAll(Arrays.asList(changelog));
    }

    /**
     * Set the changelog
     *
     * @param changelog the changelog
     */
    public void setChangelog(final List<String> changelog) {
        this.lines.clear();
        this.lines.addAll(changelog);
    }

    /**
     * Set the update URL
     *
     * @param url the url
     */
    public void setUpdateURL(final URL url) {
        this.updateURL = url;
    }

    /**
     * Generate the file
     *
     * @param name the file name
     * @return the generated file
     */
    @SuppressWarnings("deprecation")
    public KarmaFile generate(final String name) {
        File destination = FileUtilities.getFixedFile(new File(this.source.getDataPath().toFile(), name + ".kupdter"));
        KarmaFile file = new KarmaFile(destination);
        file.create();
        file.set("VERSION", this.source.version());
        file.set("UPDATE", (this.updateURL != null) ? this.updateURL.toString() : "");
        file.set("CHANGELOG", this.lines);
        return file;
    }

    /**
     * Generate the file
     *
     * @param name the file name
     * @return the generated file
     */
    public KarmaMain generateV2(final String name) {
        Path destination = PathUtilities.getFixedPath(source.getDataPath().resolve(name + ".kup"));
        KarmaMain file = new KarmaMain(source, destination)
                .internal(KarmaUpdaterGenerator.class.getResourceAsStream("/update_template.kup"));

        try {
            file.validate();
            file.set("version", new KarmaObject(source.version()));
            file.set("update_url", new KarmaObject((updateURL != null ? updateURL.toString() : "")));

            List<KarmaElement> elements = new ArrayList<>();
            for (String line : lines) {
                elements.add(new KarmaObject(line));
            }

            file.set("changelog", new KarmaArray(elements.toArray(new KarmaElement[0])));

            if (!file.save()) {
                throw new RuntimeException("Failed to save update file for " + source.name());
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return file;
    }
}
