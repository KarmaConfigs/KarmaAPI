package ml.karmaconfigs.api.common;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.AsyncScheduler;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.common.utils.StringUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.enums.LogExtension;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

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
 * KarmaSource logger manager, store data in a file
 */
public final class Logger implements Serializable {

    private final static HashMap<KarmaSource, Calendar> calendar_type = new HashMap<>();
    private final static HashMap<KarmaSource, LogExtension> ext_type = new HashMap<>();

    private final KarmaSource source;

    /**
     * Initialize the logger
     * for the specified source
     *
     * @param s the source
     */
    public Logger(@NotNull final KarmaSource s) {
        source = s;
    }

    /**
     * Set the calendar type of the logs
     *
     * @param calendar the calendar type
     */
    public final void setCalendarType(final Calendar calendar) {
        calendar_type.put(source, calendar);
    }

    /**
     * Set the logs extension type ( RECOMMENDED MARKDOWN )
     *
     * @param extension the extension type
     */
    public final void setLogsExtension(final LogExtension extension) { ext_type.put(source, extension); };

    /**
     * Set the log file max size
     *
     * MINIMUM IS 10
     *
     * @param max the file log max size
     *
     * @deprecated This does nothing now
     */
    @Deprecated
    public final void setMaxSize(final int max) {}

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
        AsyncScheduler scheduler = new AsyncScheduler();
        String info = StringUtils.formatString(tmpInfo, replaces);
        scheduler.addTask(() -> {
            Calendar calendar = calendar_type.getOrDefault(source, GregorianCalendar.getInstance());

            String time = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "] ";

            Path logFolder = source.getDataPath().resolve("logs").resolve(String.valueOf(calendar.get(Calendar.YEAR))).resolve(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            File logFile = logFolder.resolve(calendar.get(Calendar.DAY_OF_MONTH) + (ext_type.getOrDefault(source, LogExtension.MARKDOWN).equals(LogExtension.MARKDOWN) ? ".md" : ".log")).toFile();

            logFile = FileUtilities.getFixedFile(logFile);
            FileUtilities.create(logFile);

            InputStream inLog = null;
            InputStreamReader inReader = null;
            BufferedReader reader = null;
            try {
                switch (ext_type.getOrDefault(source, LogExtension.MARKDOWN)) {
                    case MARKDOWN:
                        boolean created = false;
                        if (!logFile.exists() && logFile.createNewFile()) {
                            String path = logFile.getAbsolutePath().replaceAll("\\\\", "/");
                            Console.send(source, "Created log file {0}", Level.INFO, path);
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
                                if (line.startsWith("Source version:")) {
                                    String log_version = line.replace("Source version: ", "").replace("<br>", "");

                                    if (!log_version.equals(source.version()))
                                        line = "Source version: " + source.version() + "<br>";
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
                                writer.write("# " + source.name() + " info<br>\n");
                                writer.write("Server version: Velocity<br>\n");
                                writer.write("Source version: " + source.version() + "<br>\n");
                                writer.write("Plugin authors: \n\n");
                                for (String author : source.authors()) {
                                    writer.write("      - " + author + "\n");
                                }
                                writer.write("Source description: " + source.description() + "<br>\n");
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
                            Console.send(source, "Created log file {0}", Level.INFO, path);
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
        AsyncScheduler scheduler = new AsyncScheduler();
        scheduler.addTask(() -> {
            Calendar calendar = calendar_type.getOrDefault(source, GregorianCalendar.getInstance());

            String time = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "] ";

            Path logFolder = source.getDataPath().resolve("logs").resolve(String.valueOf(calendar.get(Calendar.YEAR))).resolve(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            File logFile = logFolder.resolve(calendar.get(Calendar.DAY_OF_MONTH) + (ext_type.getOrDefault(source, LogExtension.MARKDOWN).equals(LogExtension.MARKDOWN) ? ".md" : ".log")).toFile();

            logFile = FileUtilities.getFixedFile(logFile);
            FileUtilities.create(logFile);

            InputStream inLog = null;
            InputStreamReader inReader = null;
            BufferedReader reader = null;
            try {
                switch (ext_type.getOrDefault(source, LogExtension.MARKDOWN)) {
                    case MARKDOWN:
                        boolean created = false;
                        if (!logFile.exists() && logFile.createNewFile()) {
                            String path = logFile.getAbsolutePath().replaceAll("\\\\", "/");
                            Console.send(source, "Created log file {0}", Level.INFO, path);
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
                                if (line.startsWith("Source version:")) {
                                    String log_version = line.replace("Source version: ", "").replace("<br>", "");

                                    if (!log_version.equals(source.version()))
                                        line = "Source version: " + source.version() + "<br>";
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
                                writer.write("# " + source.name() + " info<br>\n");
                                writer.write("Server version: Velocity<br>\n");
                                writer.write("Source version: " + source.version() + "<br>\n");
                                writer.write("Plugin authors: \n\n");
                                for (String author : source.authors()) {
                                    writer.write("      - " + author + "\n");
                                }
                                writer.write("Source description: " + source.description() + "<br>\n");
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
                            Console.send(source, "Created log file {0}", Level.INFO, path);
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
}
