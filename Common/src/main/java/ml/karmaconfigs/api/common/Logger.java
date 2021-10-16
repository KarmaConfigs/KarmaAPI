package ml.karmaconfigs.api.common;

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

import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.enums.LogCalendar;
import ml.karmaconfigs.api.common.utils.enums.LogExtension;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Karma logger
 */
public final class Logger implements Serializable {

    /**
     * A map that contains source => calendar type
     */
    private static final Map<KarmaSource, LogCalendar> calendar_type = new HashMap<>();
    /**
     * A map that contains source => log file extension type
     */
    private static final Map<KarmaSource, LogExtension> ext_type = new HashMap<>();
    /**
     * A map that contains source => log header
     */
    private static final Map<KarmaSource, String> header = new HashMap<>();

    /**
     * The logger source
     */
    private final KarmaSource source;

    /**
     * Initialize the logger
     *
     * @param s the logger source
     */
    public Logger(final @NotNull KarmaSource s) {
        this.source = s;
        header.put(this.source, "# System information<br>\n<br>\n" +
                StringUtils.formatString("Os name: {0}<br>\n", JavaVM.osName()) +
                StringUtils.formatString("Os version: {0}<br>\n", JavaVM.osVersion()) +
                StringUtils.formatString("Os model: {0}<br>\n", JavaVM.osModel()) +
                StringUtils.formatString("Os arch: {0}<br>\n", JavaVM.osArchitecture()) +
                StringUtils.formatString("Os max memory: {0}<br>\n", JavaVM.osMaxMemory()) +
                StringUtils.formatString("Os free memory: {0}<br>\n", JavaVM.osFreeMemory()) +
                "\n# VM information<br>\n<br>\n" +
                StringUtils.formatString("Architecture: {0}<br>\n", JavaVM.jvmArchitecture()) +
                StringUtils.formatString("Max memory: {0}<br>\n", JavaVM.jvmMax()) +
                StringUtils.formatString("Free memory: {0}<br>\n", JavaVM.jvmAvailable()) +
                StringUtils.formatString("Processors: {0}<br>\n", JavaVM.jvmProcessors()) +
                "\n# API Information<br>\n" +
                StringUtils.formatString("API Version: {0}<br>\n", KarmaAPI.getVersion()) +
                StringUtils.formatString("API Compiler: {0}<br>\n", KarmaAPI.getCompilerVersion()) +
                StringUtils.formatString("API Date: {0}<br>\n", KarmaAPI.getBuildDate()) +
                "\n# Source information<br>\n" +
                StringUtils.formatString("Name: {0}<br>\n", this.source.name()) +
                StringUtils.formatString("Version: {0}<br>\n", this.source.version()) +
                StringUtils.formatString("Description: {0}<br>\n", this.source.description().replace("\n", "<br>")) +
                StringUtils.formatString("Author(s): {0}<br>\n", this.source.authors(true, "<br>- ")) +
                StringUtils.formatString("Update URL: {0}<br>\n", this.source.updateURL()) +
                "\n# Beginning of log<br><br>\n\n");
    }

    /**
     * Set the logger calendar type
     *
     * @param calendar the logger calendar
     * @return this instance
     */
    public Logger calendar(LogCalendar calendar) {
        calendar_type.put(this.source, calendar);
        return this;
    }

    /**
     * Set the logger extension type
     *
     * @param extension the logger extension
     * @return this instance
     */
    public Logger extension(LogExtension extension) {
        ext_type.put(this.source, extension);
        return this;
    }

    /**
     * Schedule a new log
     *
     * @param level the log level
     * @param info the log info
     * @param replaces the log replaces
     */
    public void scheduleLog(final @NotNull Level level, final @NotNull String info, final @NotNull Object... replaces) {
        APISource.asyncScheduler().queue(() -> {
            LogExtension extension = ext_type.getOrDefault(this.source, LogExtension.MARKDOWN);
            Calendar calendar = ((LogCalendar) calendar_type.getOrDefault(this.source, LogCalendar.GREGORIAN)).getType();
            String time = String.format("%02d",
                    calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    String.format("%02d", calendar.get(Calendar.MINUTE)) + ":" +
                    String.format("%02d", calendar.get(Calendar.SECOND));
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            String day = String.valueOf(calendar.get(Calendar.DATE));
            Path log = APISource.getSource().getDataPath().resolve("logs").resolve(year).resolve(month).resolve(day + "." + extension.fileExtension());
            PathUtilities.create(log);
            try {
                List<String> lines = removeHeader(Files.readAllLines(log));
                BufferedWriter writer = Files.newBufferedWriter(log, StandardCharsets.UTF_8);
                writer.write(header.get(this.source));
                for (String line : lines)
                    writer.write(line + "\n");
                writer.write(StringUtils.formatString("[ {0} - {1} ] {2}<br>", level.name(), time, StringUtils.formatString(info, replaces)));
                writer.flush();
                writer.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Schedule a new log
     *
     * @param level the log level
     * @param info the log info
     */
    public void scheduleLog(final @NotNull Level level, final @NotNull Throwable info) {
        APISource.asyncScheduler().queue(() -> {
            LogExtension extension = ext_type.getOrDefault(this.source, LogExtension.MARKDOWN);
            Calendar calendar = ((LogCalendar) calendar_type.getOrDefault(this.source, LogCalendar.GREGORIAN)).getType();
            String time = String.format("%02d",
                    calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    String.format("%02d", calendar.get(Calendar.MINUTE)) + ":" +
                    String.format("%02d", calendar.get(Calendar.SECOND));
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            String day = String.valueOf(calendar.get(Calendar.DATE));
            Path log = APISource.getSource().getDataPath().resolve("logs").resolve(year).resolve(month).resolve(day + "." + extension.fileExtension());
            PathUtilities.create(log);
            try {
                List<String> lines = removeHeader(Files.readAllLines(log));
                BufferedWriter writer = Files.newBufferedWriter(log, StandardCharsets.UTF_8);
                writer.write(header.get(this.source));
                for (String line : lines)
                    writer.write(line + "\n");
                Throwable prefix = new Throwable(info);
                writer.write(StringUtils.formatString("[ {0} - {1} ] {2}\n", level.name(), time, prefix.fillInStackTrace()));
                writer.write("```java\n");
                for (StackTraceElement element : info.getStackTrace())
                    writer.write(element + "\n");
                writer.write("```");
                writer.flush();
                writer.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Remove the log file header
     *
     * @param lines the file lines
     * @return the log file lines without header
     */
    private List<String> removeHeader(final List<String> lines) {
        List<String> copy = new ArrayList<>();
        boolean begone = false;
        for (String line : lines) {
            if (begone) {
                copy.add(line);
                continue;
            }
            if (line.startsWith("# Beginning of log<br><br>"))
                begone = true;
        }
        if (copy.size() > 1 &&
                StringUtils.isNullOrEmpty(copy.get(0)) && StringUtils.isNullOrEmpty(copy.get(1)))
            copy.remove(0);
        return copy;
    }
}
