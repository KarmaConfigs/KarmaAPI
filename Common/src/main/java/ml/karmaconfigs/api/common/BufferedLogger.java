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

import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.ConcurrentList;
import ml.karmaconfigs.api.common.utils.KarmaLogger;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.enums.LogCalendar;
import ml.karmaconfigs.api.common.utils.enums.LogExtension;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Karma logger
 */
public final class BufferedLogger extends KarmaLogger implements Serializable {

    /**
     * A map that contains source => calendar type
     */
    private static final Map<KarmaSource, LogCalendar> calendar_type = new ConcurrentHashMap<>();
    /**
     * A map that contains source => log file extension type
     */
    private static final Map<KarmaSource, LogExtension> ext_type = new ConcurrentHashMap<>();
    /**
     * A map that contains source => log header
     */
    private static final Map<KarmaSource, String> header = new ConcurrentHashMap<>();
    /**
     * A map that contains source => log contains header
     */
    private static final Map<KarmaSource, Boolean> header_added = new ConcurrentHashMap<>();
    /**
     * A map that contains source => log scheduled to print
     */
    private static final Map<KarmaSource, List<String>> buffer = new ConcurrentHashMap<>();

    /**
     * The logger source
     */
    private final KarmaSource source;

    /**
     * Initialize the logger
     *
     * @param s the logger source
     */
    public BufferedLogger(final @NotNull KarmaSource s) {
        super(s);
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
                StringUtils.formatString("Version: {0}<br>\n", JavaVM.javaVersion()) +
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

        List<String> stored_log = buffer.getOrDefault(source, new ConcurrentList<>());
        if (!header_added.getOrDefault(source, false)) {
            stored_log.add(header.get(this.source));
            header_added.put(source, true);

            //Basically the logger will always start with at least the header
            buffer.put(source, stored_log);
        }
    }

    /**
     * Set the logger calendar type
     *
     * @param calendar the logger calendar
     * @return this instance
     */
    @SuppressWarnings("unused")
    public BufferedLogger calendar(LogCalendar calendar) {
        calendar_type.put(this.source, calendar);
        return this;
    }

    /**
     * Set the logger extension type
     *
     * @param extension the logger extension
     * @return this instance
     */
    @SuppressWarnings("unused")
    public BufferedLogger extension(LogExtension extension) {
        ext_type.put(this.source, extension);
        return this;
    }

    /**
     * Run the log function on a new
     * thread
     *
     * @param level the log level
     * @param info the info to log
     * @param replaces the info replaces
     */
    @Override
    public void scheduleLog(final @NotNull Level level, final @NotNull CharSequence info, final @NotNull Object... replaces) {
        source.async().queue("asynchronous_log", () -> logInfo(level, printInfo(), info, replaces));
    }

    /**
     * Run the log function on a new
     * thread
     *
     * @param level the log level
     * @param error the error to log
     */
    @Override
    public void scheduleLog(final @NotNull Level level, final @NotNull Throwable error) {
        source.async().queue("asynchronous_log", () -> logError(level, printError(), error));
    }

    /**
     * Run the log function on a new
     * thread
     *
     * @param level the log level
     * @param print print info to console
     * @param info the info to log
     * @param replaces the info replaces
     */
    @Override
    public void scheduleLogOption(final Level level, final boolean print, final CharSequence info, final Object... replaces) {
        source.async().queue("asynchronous_log", () -> logInfo(level, print, info, replaces));
    }

    /**
     * Run the log function on a new
     * thread
     *
     * @param level the log level
     * @param print print info to console
     * @param error the error to log
     */
    @Override
    public void scheduleLogOption(final Level level, final boolean print, final Throwable error) {
        source.async().queue("asynchronous_log", () -> logError(level, print, error));
    }

    /**
     * Run the log function on the main
     * known thread
     *
     * @param level the log level
     * @param info the info to log
     * @param replaces the info replaces
     */
    @Override
    public void syncedLog(final Level level, final CharSequence info, final Object... replaces) {
        source.sync().queue("asynchronous_log", () -> logInfo(level, printInfo(), info, replaces));
    }

    /**
     * Run the log function on the main
     * known thread
     *
     * @param level the log level
     * @param error the error to log
     */
    @Override
    public void syncedLog(final Level level, final Throwable error) {
        source.sync().queue("synchronous_log", () -> logError(level, printError(), error));
    }

    /**
     * Run the log function on the main
     * known thread
     *
     * @param level the log level
     * @param print print info to console
     * @param info the info to log
     * @param replaces the info replaces
     */
    @Override
    public void syncedLogOption(final Level level, final boolean print, final CharSequence info, final Object... replaces) {
        source.sync().queue("synchronous_log", () -> logInfo(level, print, info, replaces));
    }

    /**
     * Run the log function on the main
     * known thread
     *
     * @param level the log level
     * @param print print info to console
     * @param error the error to log
     */
    @Override
    public void syncedLogOption(final Level level, final boolean print, final Throwable error) {
        source.sync().queue("synchronous_log", () -> logError(level, print, error));
    }

    /**
     * Log info
     *
     * @param level the info level
     * @param print print info to console
     * @param info the info
     * @param replaces the info replaces
     */
    private void logInfo(final Level level, final boolean print, final CharSequence info, final Object... replaces) {
            List<String> stored_log = buffer.getOrDefault(source, new ConcurrentList<>());
            String time = fetchTime(calendar_type.getOrDefault(source, LogCalendar.GREGORIAN));

            try {
                if (!header_added.getOrDefault(source, false)) {
                    stored_log.add(header.get(this.source));
                    header_added.put(source, true);
                }

                stored_log.add(StringUtils.formatString("[ {0} - {1} ] {2}<br>", level.name(), time, StringUtils.formatString(info, replaces)));

                buffer.put(source, stored_log);
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                if (print) {
                    source.console().send(info, level);
                }
            }
    }

    /**
     * Log error info
     *
     * @param level the error level
     * @param print print error info to console
     * @param error the error
     */
    private void logError(final Level level, final boolean print, final Throwable error) {
        List<String> stored_log = buffer.getOrDefault(source, new ConcurrentList<>());
        String time = fetchTime(calendar_type.getOrDefault(source, LogCalendar.GREGORIAN));

        try {
            if (!header_added.getOrDefault(source, false)) {
                stored_log.add(header.get(this.source));
                header_added.put(source, true);
            }

            Throwable prefix = new Throwable(error);
            stored_log.add(StringUtils.formatString("[ {0} - {1} ] {2}\n", level.name(), time, prefix.fillInStackTrace()));
            stored_log.add("```java\n");
            for (StackTraceElement element : error.getStackTrace())
                stored_log.add(element + "\n");
            stored_log.add("```");

            buffer.put(source, stored_log);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            Throwable prefix = new Throwable(error);

            if (print) {
                source.console().send("An internal error occurred ( {0} )", level, prefix.fillInStackTrace());
                for (StackTraceElement element : error.getStackTrace())
                    source.console().send(element.toString(), Level.INFO);
            }
        }
    }

    /**
     * Clear the log info
     */
    @Override
    public synchronized void clearLog() {
        buffer.remove(source);
    }

    /**
     * Flush the log data if the
     * log auto flush is turned off
     *
     * WARNING: This will replace all the log file
     * content, this should be used only for applications
     * that runs once -> generate a log file and then
     * switch log file. You can change the log file
     * by overriding {@link KarmaLogger#getLoggerFile(LogExtension)}
     *
     * DOES NOTHING ON {@link Logger}
     *
     * @return if the log could be flushed
     */
    @Override
    public boolean flush() {
        try {
            BufferedWriter writer = Files.newBufferedWriter(getLoggerFile(ext_type.getOrDefault(source, LogExtension.MARKDOWN)), StandardCharsets.UTF_8);
            List<String> lines = buffer.remove(source);
            for (String str : lines)
                writer.write(str + "\n");

            writer.flush();
            writer.close();

            header_added.put(source, false);
            return true;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Get the today's logger file
     *
     * @param type the log extension file type
     * @return the today's logger file
     */
    @Override
    protected Path getLoggerFile(LogExtension type) {
        Calendar calendar = Calendar.getInstance();

        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String day = String.valueOf(calendar.get(Calendar.DATE));
        Path log = source.getDataPath().resolve("logs").resolve(year).resolve(month).resolve(day + "." + type.fileExtension());

        if (Files.exists(log)) {
            Path target = source.getDataPath().resolve("logs").resolve(year).resolve(month).resolve(day).resolve(System.currentTimeMillis() + "." + type.fileExtension());
            if (!Files.exists(target))
                PathUtilities.create(target);

            try {
                Files.move(target, log, StandardCopyOption.REPLACE_EXISTING);
                PathUtilities.create(log);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return log;
    }
}
