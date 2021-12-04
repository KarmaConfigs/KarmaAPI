package ml.karmaconfigs.api.common.utils;

import ml.karmaconfigs.api.common.Logger;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.enums.LogCalendar;
import ml.karmaconfigs.api.common.utils.enums.LogExtension;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class KarmaLogger implements AutoCloseable {

    private final static Map<String, KarmaLogger> loggers = new ConcurrentHashMap<>();

    private final KarmaSource source;
    private final boolean flush;
    private final boolean printErrors;
    private final boolean printMessages;

    public KarmaLogger(final KarmaSource owner) {
        KarmaLogger stored = loggers.getOrDefault(owner.name().toLowerCase(), null);
        if (stored == null) {
            source = owner;

            flush = true;
            printErrors = false;
            printMessages = false;

            loggers.put(owner.name().toLowerCase(), this);
        } else {
            source = stored.source;
            flush = stored.flush;

            printErrors = stored.printErrors;
            printMessages = stored.printMessages;
        }
    }

    public KarmaLogger(final KarmaSource owner, final boolean autoFlush, final boolean errors, final boolean messages) {
        KarmaLogger stored = loggers.getOrDefault(owner.name().toLowerCase(), null);
        if (stored == null) {
            source = owner;
            flush = autoFlush;

            printErrors = errors;
            printMessages = messages;

            loggers.put(owner.name().toLowerCase(), this);
        } else {
            source = stored.source;
            flush = stored.flush;

            printErrors = stored.printErrors;
            printMessages = stored.printMessages;
        }
    }

    /**
     * Run the log function on a new
     * thread
     *
     * @param level the log level
     * @param info the info to log
     * @param replaces the info replaces
     */
    public abstract void scheduleLog(final Level level, final CharSequence info, final Object... replaces);

    /**
     * Run the log function on a new
     * thread
     *
     * @param level the log level
     * @param error the error to log
     */
    public abstract void scheduleLog(final Level level, final Throwable error);

    /**
     * Run the log function on a new
     * thread
     *
     * @param level the log level
     * @param print print info to console
     * @param info the info to log
     * @param replaces the info replaces
     */
    public abstract void scheduleLogOption(final Level level, final boolean print, final CharSequence info, final Object... replaces);

    /**
     * Run the log function on a new
     * thread
     *
     * @param level the log level
     * @param print print info to console
     * @param error the error to log
     */
    public abstract void scheduleLogOption(final Level level, final boolean print, final Throwable error);

    /**
     * Run the log function on the main
     * known thread
     *
     * @param level the log level
     * @param info the info to log
     * @param replaces the info replaces
     */
    public abstract void syncedLog(final Level level, final CharSequence info, final Object... replaces);

    /**
     * Run the log function on the main
     * known thread
     *
     * @param level the log level
     * @param error the error to log
     */
    public abstract void syncedLog(final Level level, final Throwable error);

    /**
     * Run the log function on the main
     * known thread
     *
     * @param level the log level
     * @param print print info to console
     * @param info the info to log
     * @param replaces the info replaces
     */
    public abstract void syncedLogOption(final Level level, final boolean print, final CharSequence info, final Object... replaces);

    /**
     * Run the log function on the main
     * known thread
     *
     * @param level the log level
     * @param print print info to console
     * @param error the error to log
     */
    public abstract void syncedLogOption(final Level level, final boolean print, final Throwable error);

    /**
     * Clear the log file
     */
    public abstract void clearLog();

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
    @ApiStatus.Experimental
    public abstract boolean flush();

    /**
     * This means remove the logger
     * name attachment so you can
     * create a new logger instance
     * with new logger options
     */
    @Override
    public final void close() {
        loggers.remove(source.name().toLowerCase());
    }

    /**
     * Get the today's logger file
     *
     * @param type the log extension file type
     * @return the today's logger file
     */
    protected Path getLoggerFile(final LogExtension type) {
        Calendar calendar = Calendar.getInstance();

        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String day = String.valueOf(calendar.get(Calendar.DATE));
        Path log = source.getDataPath().resolve("logs").resolve(year).resolve(month).resolve(day + "." + type.fileExtension());
        PathUtilities.create(log);

        return log;
    }

    /**
     * Get the now's time
     *
     * @param type the calendar type ( gregorian or US )
     * @return the now's time
     */
    protected String fetchTime(final LogCalendar type) {
        Calendar calendar = type.getType();
        return String.format("%02d",
                calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format("%02d", calendar.get(Calendar.MINUTE)) + ":" +
                String.format("%02d", calendar.get(Calendar.SECOND));
    }

    /**
     * Get if the logger prints info
     *
     * @return if the logger prints info
     */
    protected final boolean printInfo() {
        return printMessages;
    }

    /**
     * Get if the logger prints errors
     *
     * @return if the logger prints errors
     */
    protected final boolean printError() {
        return printErrors;
    }

    /**
     * Get the requested logger or KarmaAPI's logger
     * if there's not any logger with the provided name
     *
     * @param name the logger name
     * @return the logger
     */
    @Nullable
    public static KarmaLogger getLogger(final String name) {
        return loggers.getOrDefault(name, loggers.get("karmaapi"));
    }
}
