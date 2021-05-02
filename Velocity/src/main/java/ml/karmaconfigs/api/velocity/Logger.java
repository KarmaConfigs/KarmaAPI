package ml.karmaconfigs.api.velocity;

import com.velocitypowered.api.plugin.Plugin;
import ml.karmaconfigs.api.common.Level;
import ml.karmaconfigs.api.common.LogExtension;
import ml.karmaconfigs.api.common.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public class Logger {

    private final static HashMap<Plugin, Calendar> calendar_type = new HashMap<>();
    private final static HashMap<Plugin, LogExtension> ext_type = new HashMap<>();
    private final static HashMap<Plugin, Integer> max_size = new HashMap<>();

    private final Plugin plugin;

    /**
     * Initialize the logger
     * for the specified plugin
     *
     * @param p the plugin
     */
    public Logger(@NotNull final Plugin p) {
        plugin = p;
    }

    /**
     * Set the calendar type of the logs
     *
     * @param calendar the calendar type
     */
    public final void setCalendarType(final Calendar calendar) {
        calendar_type.put(plugin, calendar);
    }

    /**
     * Set the logs extension type ( RECOMMENDED MARKDOWN )
     *
     * @param extension the extension type
     */
    public final void setLogsExtension(final LogExtension extension) { ext_type.put(plugin, extension); };

    /**
     * Set the log file max size
     *
     * MINIMUM IS 10
     *
     * @param max the file log max size
     */
    public final void setMaxSize(final int max) {
        if (max > 10)
            max_size.put(plugin, max);
    }

    /**
     * Log the specified info with the
     * specified level, the format will be:
     * <br>
     * --------------------- LEVEL ----------------------<br>
     * <br>
     * [12:54:12] This is the message I wrote in info
     * <br>
     *
     * @param level the log level
     * @param info  the info
     */
    public final void scheduleLog(@NotNull final Level level, @NotNull final String info) {
        Util util = new Util(plugin);
        util.initialize();

        AsyncScheduler scheduler = new AsyncScheduler();
        scheduler.addTask(() -> {
            Calendar calendar = calendar_type.getOrDefault(plugin, GregorianCalendar.getInstance());

            String time = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "] ";

            File logFolder = new File(util.getDataFolder(), "logs");
            File logYearFolder = new File(logFolder, String.valueOf(calendar.get(Calendar.YEAR)));
            File logMonthFolder = new File(logYearFolder, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            File logFile = new File(logMonthFolder, calendar.get(Calendar.DAY_OF_MONTH) + (logsAmount() > 0 ? "_" + logsAmount() : "") + (ext_type.getOrDefault(plugin, LogExtension.MARKDOWN).equals(LogExtension.MARKDOWN) ? ".md" : ".log"));
            if (!logMonthFolder.exists() && logMonthFolder.mkdirs()) {
                String path = logMonthFolder.getAbsolutePath().replaceAll("\\\\", "/");
                KarmaConsoleSender.send("Created directory {0}", Level.INFO, path);
            }

            try {
                long kbSize = Files.size(logFile.toPath()) / 1024;
                if (kbSize >= max_size.getOrDefault(plugin, 10)) {
                    int amount = logsAmount();
                    amount++;
                    logFile = new File(logMonthFolder, calendar.get(Calendar.DAY_OF_MONTH) + "_" + amount + (ext_type.getOrDefault(plugin, LogExtension.MARKDOWN).equals(LogExtension.MARKDOWN) ? ".md" : ".log"));
                }
            } catch (Throwable ignored) {}

            InputStream inLog = null;
            InputStreamReader inReader = null;
            BufferedReader reader = null;
            try {
                switch (ext_type.getOrDefault(plugin, LogExtension.MARKDOWN)) {
                    case MARKDOWN:
                        boolean created = false;
                        if (!logFile.exists() && logFile.createNewFile()) {
                            String path = logFile.getAbsolutePath().replaceAll("\\\\", "/");
                            KarmaConsoleSender.send("Created log file {0}", Level.INFO, path);
                            created = true;
                        }

                        if (logFile.exists()) {
                            inLog = new FileInputStream(logFile);
                            inReader = new InputStreamReader(inLog, StandardCharsets.UTF_8);
                            reader = new BufferedReader(inReader);

                            String os = System.getProperty("os.name");
                            os = os.substring(0, 1).toUpperCase() + os.substring(1).toLowerCase();

                            String arch = System.getenv("PROCESSOR_ARCHITECTURE");
                            String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

                            String realArch = arch != null && arch.endsWith("64")
                                    || wow64Arch != null && wow64Arch.endsWith("64")
                                    ? "64" : "32";
                            String jvm_arch = System.getProperty("os.arch");

                            String version = System.getProperty("os.version");

                            String model = System.getProperty("sun.arch.data.model");

                            ArrayList<String> lines = new ArrayList<>();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith("Plugin version:")) {
                                    String log_version = line.replace("Plugin version: ", "").replace("<br>", "");
                                    String plugin_version = plugin.version();

                                    if (!log_version.equals(plugin_version))
                                        line = "Plugin version: " + plugin_version + "<br>";
                                }
                                lines.add(line + "\n");
                            }

                            lines.add("### " + level.name() + "\n");
                            lines.add(time + info + "<br>\n");

                            FileWriter writer = new FileWriter(logFile);
                            if (created) {
                                writer.write("# System info<br>\n");
                                writer.write("Operative system: " + os + "<br>\n");
                                writer.write("OS version: " + version.replace(jvm_arch, "") + "<br>\n");
                                writer.write("JVM architecture: " + jvm_arch + "<br>\n");
                                writer.write("Architecture: " + realArch + "<br>\n");
                                writer.write("Model: " + model + "<br>\n");
                                writer.write("# " + plugin.name() + " info<br>\n");
                                writer.write("Server version: Velocity<br>\n");
                                writer.write("Plugin version: " + plugin.version() + "<br>\n");
                                writer.write("Plugin authors: \n\n");
                                for (String author : plugin.authors()) {
                                    writer.write("      - " + author + "\n");
                                }
                                writer.write("Plugin description: " + plugin.description() + "<br>\n");
                            }
                            for (String str : lines) {
                                writer.write(str + (!str.endsWith("\n") ? "\n" : ""));
                            }

                            writer.flush();
                            writer.close();
                        }
                        break;
                    case LOG:
                    default:
                        if (!logFile.exists() && logFile.createNewFile()) {
                            String path = logFile.getAbsolutePath().replaceAll("\\\\", "/");
                            KarmaConsoleSender.send("Created log file {0}", Level.INFO, path);
                        }

                        if (logFile.exists()) {
                            inLog = new FileInputStream(logFile);
                            inReader = new InputStreamReader(inLog, StandardCharsets.UTF_8);
                            reader = new BufferedReader(inReader);

                            ArrayList<String> lines = new ArrayList<>();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                lines.add(line);
                            }

                            if (lines.isEmpty()) {
                                lines.add("----------------------------- " + level.name() + " -----------------------------\n");
                            } else {
                                lines.add("\n----------------------------- " + level.name() + " -----------------------------\n");
                            }

                            lines.add(time + info);

                            FileWriter writer = new FileWriter(logFile);
                            for (String str : lines) {
                                writer.write(str + "\n");
                            }

                            writer.flush();
                            writer.close();
                        }
                        break;
                }
            } catch (Throwable ignored) {
            } finally {
                try {
                    if (inLog != null) {
                        inLog.close();
                        if (inReader != null) {
                            inReader.close();
                            if (reader != null) {
                                reader.close();
                            }
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        });
    }

    /**
     * Log the specified info with the
     * specified level, the format will be:
     * <br>
     * --------------------- LEVEL ----------------------<br>
     * <br>
     * [12:54:12] This is the message I wrote in info
     * <br>
     *
     * @param level the log level
     * @param tmpInfo  the info
     * @param replaces the info replaces
     */
    public final void scheduleLog(@NotNull final Level level, @NotNull final String tmpInfo, @NotNull final Object... replaces) {
        Util util = new Util(plugin);
        util.initialize();

        AsyncScheduler scheduler = new AsyncScheduler();
        String info = StringUtils.formatString(tmpInfo, replaces);
        scheduler.addTask(() -> {
            Calendar calendar = calendar_type.getOrDefault(plugin, GregorianCalendar.getInstance());

            String time = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "] ";

            File logFolder = new File(util.getDataFolder(), "logs");
            File logYearFolder = new File(logFolder, String.valueOf(calendar.get(Calendar.YEAR)));
            File logMonthFolder = new File(logYearFolder, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            File logFile = new File(logMonthFolder, calendar.get(Calendar.DAY_OF_MONTH) + (logsAmount() > 0 ? "_" + logsAmount() : "") + (ext_type.getOrDefault(plugin, LogExtension.MARKDOWN).equals(LogExtension.MARKDOWN) ? ".md" : ".log"));
            if (!logMonthFolder.exists() && logMonthFolder.mkdirs()) {
                String path = logMonthFolder.getAbsolutePath().replaceAll("\\\\", "/");
                KarmaConsoleSender.send("Created directory {0}", Level.INFO, path);
            }

            try {
                long kbSize = Files.size(logFile.toPath()) / 1024;
                if (kbSize >= max_size.getOrDefault(plugin, 10)) {
                    int amount = logsAmount();
                    amount++;
                    logFile = new File(logMonthFolder, calendar.get(Calendar.DAY_OF_MONTH) + "_" + amount + (ext_type.getOrDefault(plugin, LogExtension.MARKDOWN).equals(LogExtension.MARKDOWN) ? ".md" : ".log"));
                }
            } catch (Throwable ignored) {}

            InputStream inLog = null;
            InputStreamReader inReader = null;
            BufferedReader reader = null;
            try {
                switch (ext_type.getOrDefault(plugin, LogExtension.MARKDOWN)) {
                    case MARKDOWN:
                        boolean created = false;
                        if (!logFile.exists() && logFile.createNewFile()) {
                            String path = logFile.getAbsolutePath().replaceAll("\\\\", "/");
                            KarmaConsoleSender.send("Created log file {0}", Level.INFO, path);
                            created = true;
                        }

                        if (logFile.exists()) {
                            inLog = new FileInputStream(logFile);
                            inReader = new InputStreamReader(inLog, StandardCharsets.UTF_8);
                            reader = new BufferedReader(inReader);

                            String os = System.getProperty("os.name");
                            os = os.substring(0, 1).toUpperCase() + os.substring(1).toLowerCase();

                            String arch = System.getenv("PROCESSOR_ARCHITECTURE");
                            String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

                            String realArch = arch != null && arch.endsWith("64")
                                    || wow64Arch != null && wow64Arch.endsWith("64")
                                    ? "64" : "32";
                            String jvm_arch = System.getProperty("os.arch");

                            String version = System.getProperty("os.version");

                            String model = System.getProperty("sun.arch.data.model");

                            ArrayList<String> lines = new ArrayList<>();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith("Plugin version:")) {
                                    String log_version = line.replace("Plugin version: ", "").replace("<br>", "");
                                    String plugin_version = plugin.version();

                                    if (!log_version.equals(plugin_version))
                                        line = "Plugin version: " + plugin_version + "<br>";
                                }
                                lines.add(line + "\n");
                            }

                            lines.add("### " + level.name() + "\n");
                            lines.add(time + info + "<br>\n");

                            FileWriter writer = new FileWriter(logFile);
                            if (created) {
                                writer.write("# System info<br>\n");
                                writer.write("Operative system: " + os + "<br>\n");
                                writer.write("OS version: " + version.replace(jvm_arch, "") + "<br>\n");
                                writer.write("JVM architecture: " + jvm_arch + "<br>\n");
                                writer.write("Architecture: " + realArch + "<br>\n");
                                writer.write("Model: " + model + "<br>\n");
                                writer.write("# " + plugin.name() + " info<br>\n");
                                writer.write("Server version: Velocity<br>\n");
                                writer.write("Plugin version: " + plugin.version() + "<br>\n");
                                writer.write("Plugin authors: \n\n");
                                for (String author : plugin.authors()) {
                                    writer.write("      - " + author + "\n");
                                }
                                writer.write("Plugin description: " + plugin.description() + "<br>\n");
                            }
                            for (String str : lines) {
                                writer.write(str + (!str.endsWith("\n") ? "\n" : ""));
                            }

                            writer.flush();
                            writer.close();
                        }
                        break;
                    case LOG:
                    default:
                        if (!logFile.exists() && logFile.createNewFile()) {
                            String path = logFile.getAbsolutePath().replaceAll("\\\\", "/");
                            KarmaConsoleSender.send("Created log file {0}", Level.INFO, path);
                        }

                        if (logFile.exists()) {
                            inLog = new FileInputStream(logFile);
                            inReader = new InputStreamReader(inLog, StandardCharsets.UTF_8);
                            reader = new BufferedReader(inReader);

                            ArrayList<String> lines = new ArrayList<>();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                lines.add(line);
                            }

                            if (lines.isEmpty()) {
                                lines.add("----------------------------- " + level.name() + " -----------------------------\n");
                            } else {
                                lines.add("\n----------------------------- " + level.name() + " -----------------------------\n");
                            }

                            lines.add(time + info);

                            FileWriter writer = new FileWriter(logFile);
                            for (String str : lines) {
                                writer.write(str + "\n");
                            }

                            writer.flush();
                            writer.close();
                        }
                        break;
                }
            } catch (Throwable ignored) {
            } finally {
                try {
                    if (inLog != null) {
                        inLog.close();
                        if (inReader != null) {
                            inReader.close();
                            if (reader != null) {
                                reader.close();
                            }
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        });
    }

    /**
     * Log the exception with the
     * specified level, the format will be:
     * <br>
     * --------------------- LEVEL ----------------------<br>
     * <br>
     * [12:54:12] Error in com.myname.mypackage.Main: NullPointerException
     * at java.lang.null (Java.14)
     * at com.myname.mypackage.Main (Main.256)
     * and 25 more...
     * <br>
     *
     * @param level the log level
     * @param info  the info
     */
    public final void scheduleLog(@NotNull final Level level, @NotNull final Throwable info) {
        Util util = new Util(plugin);
        util.initialize();

        AsyncScheduler scheduler = new AsyncScheduler();
        scheduler.addTask(() -> {
            Calendar calendar = calendar_type.getOrDefault(plugin, GregorianCalendar.getInstance());

            String time = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "] ";

            File logFolder = new File(util.getDataFolder(), "logs");
            File logYearFolder = new File(logFolder, String.valueOf(calendar.get(Calendar.YEAR)));
            File logMonthFolder = new File(logYearFolder, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            File logFile = new File(logMonthFolder, calendar.get(Calendar.DAY_OF_MONTH) + (logsAmount() > 0 ? "_" + logsAmount() : "") + (ext_type.getOrDefault(plugin, LogExtension.MARKDOWN).equals(LogExtension.MARKDOWN) ? ".md" : ".log"));
            if (!logMonthFolder.exists() && logMonthFolder.mkdirs()) {
                String path = logMonthFolder.getAbsolutePath().replaceAll("\\\\", "/");
                KarmaConsoleSender.send("Created directory {0}", Level.INFO, path);
            }

            try {
                long kbSize = Files.size(logFile.toPath()) / 1024;
                if (kbSize >= max_size.getOrDefault(plugin, 10)) {
                    int amount = logsAmount();
                    amount++;
                    logFile = new File(logMonthFolder, calendar.get(Calendar.DAY_OF_MONTH) + "_" + amount + (ext_type.getOrDefault(plugin, LogExtension.MARKDOWN).equals(LogExtension.MARKDOWN) ? ".md" : ".log"));
                }
            } catch (Throwable ignored) {}

            InputStream inLog = null;
            InputStreamReader inReader = null;
            BufferedReader reader = null;
            try {
                switch (ext_type.getOrDefault(plugin, LogExtension.MARKDOWN)) {
                    case MARKDOWN:
                        boolean created = false;
                        if (!logFile.exists() && logFile.createNewFile()) {
                            String path = logFile.getAbsolutePath().replaceAll("\\\\", "/");
                            KarmaConsoleSender.send("Created log file {0}", Level.INFO, path);
                            created = true;
                        }

                        if (logFile.exists()) {
                            inLog = new FileInputStream(logFile);
                            inReader = new InputStreamReader(inLog, StandardCharsets.UTF_8);
                            reader = new BufferedReader(inReader);

                            String os = System.getProperty("os.name");
                            os = os.substring(0, 1).toUpperCase() + os.substring(1).toLowerCase();

                            String arch = System.getenv("PROCESSOR_ARCHITECTURE");
                            String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

                            String realArch = arch != null && arch.endsWith("64")
                                    || wow64Arch != null && wow64Arch.endsWith("64")
                                    ? "64" : "32";
                            String jvm_arch = System.getProperty("os.arch");

                            String version = System.getProperty("os.version");

                            String model = System.getProperty("sun.arch.data.model");

                            ArrayList<String> lines = new ArrayList<>();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith("Plugin version:")) {
                                    String log_version = line.replace("Plugin version: ", "").replace("<br>", "");
                                    String plugin_version = plugin.version();

                                    if (!log_version.equals(plugin_version))
                                        line = "Plugin version: " + plugin_version + "<br>";
                                }
                                lines.add(line + "\n");
                            }

                            Throwable throwable_prefix = new Throwable(info);
                            String reason = String.valueOf(throwable_prefix.fillInStackTrace());

                            String prefix = time + reason + ": ";
                            lines.add("### " + level.name() + "\n");
                            lines.add(prefix + "<br>\n");

                            lines.add("```java\n");
                            for (StackTraceElement element : info.getStackTrace()) {
                                String data = String.valueOf(element);
                                lines.add(data + "\n");
                            }
                            lines.add("```\n");

                            FileWriter writer = new FileWriter(logFile);
                            if (created) {
                                writer.write("# System info<br>\n");
                                writer.write("Operative system: " + os + "<br>\n");
                                writer.write("OS version: " + version.replace(jvm_arch, "") + "<br>\n");
                                writer.write("JVM architecture: " + jvm_arch + "<br>\n");
                                writer.write("Architecture: " + realArch + "<br>\n");
                                writer.write("Model: " + model + "<br>\n");
                                writer.write("# " + plugin.name() + " info<br>\n");
                                writer.write("Server version: Velocity<br>\n");
                                writer.write("Plugin version: " + plugin.version() + "<br>\n");
                                writer.write("Plugin authors: \n\n");
                                for (String author : plugin.authors()) {
                                    writer.write("      - " + author + "\n");
                                }
                                writer.write("Plugin description: " + plugin.description() + "<br>\n");
                            }
                            for (String str : lines) {
                                writer.write(str + (!str.endsWith("\n") ? "\n" : ""));
                            }

                            writer.flush();
                            writer.close();
                        }
                        break;
                    case LOG:
                    default:
                        if (!logFile.exists() && logFile.createNewFile()) {
                            String path = logFile.getAbsolutePath().replaceAll("\\\\", "/");
                            KarmaConsoleSender.send("Created log file {0}", Level.INFO, path);
                        }

                        if (logFile.exists()) {
                            inLog = new FileInputStream(logFile);
                            inReader = new InputStreamReader(inLog, StandardCharsets.UTF_8);
                            reader = new BufferedReader(inReader);

                            ArrayList<String> lines = new ArrayList<>();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                lines.add(line);
                            }

                            Throwable throwable_prefix = new Throwable(info);
                            String reason = String.valueOf(throwable_prefix.fillInStackTrace());

                            String prefix = time + reason + ": ";
                            if (lines.isEmpty()) {
                                lines.add("----------------------------- " + level.name() + " -----------------------------\n");
                            } else {
                                lines.add("\n----------------------------- " + level.name() + " -----------------------------\n");
                            }
                            lines.add(prefix);
                            for (StackTraceElement element : info.getStackTrace()) {
                                String data = "                      " + element;
                                lines.add(data);
                            }

                            FileWriter writer = new FileWriter(logFile);
                            for (String str : lines) {
                                writer.write(str + "\n");
                            }

                            writer.flush();
                            writer.close();
                        }
                        break;
                }
            } catch (Throwable ignored) {
            } finally {
                try {
                    if (inLog != null) {
                        inLog.close();
                        if (inReader != null) {
                            inReader.close();
                            if (reader != null) {
                                reader.close();
                            }
                        }
                    }
                } catch (Throwable ignored) {}
            }
        });
    }

    /**
     * Get the same name logs amount
     *
     * @return the amount of same log file name
     */
    private int logsAmount() {
        Util util = new Util(plugin);
        util.initialize();

        int amount = 0;

        Calendar calendar = calendar_type.getOrDefault(plugin, GregorianCalendar.getInstance());

        File logFolder = new File(util.getDataFolder(), "logs");
        File logYearFolder = new File(logFolder, String.valueOf(calendar.get(Calendar.YEAR)));
        File logMonthFolder = new File(logYearFolder, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        if (logMonthFolder.exists()) {
            File[] files = logMonthFolder.listFiles();
            if (files != null) {
                String expecting = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

                for (File file : files) {
                    if (file.getName().startsWith(expecting + "_"))
                        amount++;
                }
            }
        }

        return amount;
    }
}

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
final class AsyncScheduler {

    private final static HashMap<Integer, Runnable> tasks = new HashMap<>();
    private static int completed = 0;
    private static Timer scheduler = null;

    /**
     * Initialize the AsyncScheduler
     */
    public AsyncScheduler() {
        if (scheduler == null) {
            scheduler = new Timer();

            new Thread(() -> {
                scheduler.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                int next = completed + 1;

                                if (tasks.containsKey(next) && tasks.get(next) != null) {
                                    Runnable runnable = tasks.get(next);
                                    runnable.run();

                                    completed++;
                                }
                            }
                        }, 0, TimeUnit.SECONDS.convert(1, TimeUnit.MILLISECONDS));
            }).start();
        }
    }

    /**
     * Add a task to the task list
     *
     * @param task the task
     */
    public final void addTask(Runnable task) {
        int amount = tasks.size();
        int index = amount + 1;
        tasks.put(index, task);
    }
}