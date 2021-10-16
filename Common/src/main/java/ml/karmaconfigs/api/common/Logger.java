package ml.karmaconfigs.api.common;

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

public final class Logger implements Serializable {
    private static final Map<KarmaSource, LogCalendar> calendar_type = new HashMap<>();

    private static final Map<KarmaSource, LogExtension> ext_type = new HashMap<>();

    private static final Map<KarmaSource, String> header = new HashMap<>();

    private final KarmaSource source;

    public Logger(@NotNull KarmaSource s) {
        this.source = s;
        header.put(this.source, "# System information<br>\n<br>\n" +

                StringUtils.formatString("Os name: {0}<br>\n", new Object[]{JavaVM.osName()}) + StringUtils.formatString("Os version: {0}<br>\n", new Object[]{JavaVM.osVersion()}) + StringUtils.formatString("Os model: {0}<br>\n", new Object[]{JavaVM.osModel()}) + StringUtils.formatString("Os arch: {0}<br>\n", new Object[]{JavaVM.osArchitecture()}) + StringUtils.formatString("Os max memory: {0}<br>\n", new Object[]{JavaVM.osMaxMemory()}) + StringUtils.formatString("Os free memory: {0}<br>\n", new Object[]{JavaVM.osFreeMemory()}) + "\n# VM information<br>\n<br>\n" +

                StringUtils.formatString("Architecture: {0}<br>\n", new Object[]{JavaVM.jvmArchitecture()}) + StringUtils.formatString("Memory: {0}<br>\n", new Object[]{JavaVM.jvmAllocated()}) + "\n# API Information<br>\n" +

                StringUtils.formatString("API Version: {0}<br>\n", new Object[]{KarmaAPI.getVersion()}) + StringUtils.formatString("API Compiler: {0}<br>\n", new Object[]{KarmaAPI.getCompilerVersion()}) + StringUtils.formatString("API Date: {0}<br>\n", new Object[]{KarmaAPI.getBuildDate()}) + "\n# Source information<br>\n" +

                StringUtils.formatString("Name: {0}<br>\n", new Object[]{this.source.name()}) + StringUtils.formatString("Version: {0}<br>\n", new Object[]{this.source.version()}) + StringUtils.formatString("Description: {0}<br>\n", new Object[]{this.source.description().replace("\n", "<br>")}) + StringUtils.formatString("Author(s): {0}<br>\n", new Object[]{this.source.authors(true, "<br>- ")}) + StringUtils.formatString("Update URL: {0}<br>\n", new Object[]{this.source.updateURL()}) + "\n# Beginning of log<br><br>\n\n");
    }

    public Logger calendar(LogCalendar calendar) {
        calendar_type.put(this.source, calendar);
        return this;
    }

    public Logger extension(LogExtension extension) {
        ext_type.put(this.source, extension);
        return this;
    }

    public void scheduleLog(@NotNull Level level, @NotNull String info, @NotNull Object... replaces) {
        APISource.asyncScheduler().queue(() -> {
            LogExtension extension = ext_type.getOrDefault(this.source, LogExtension.MARKDOWN);
            Calendar calendar = ((LogCalendar) calendar_type.getOrDefault(this.source, LogCalendar.GREGORIAN)).getType();
            String time = String.format("%02d", new Object[]{Integer.valueOf(calendar.get(11))}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(calendar.get(12))}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(calendar.get(13))});
            String year = String.valueOf(calendar.get(1));
            String month = calendar.getDisplayName(2, 2, Locale.getDefault());
            String day = String.valueOf(calendar.get(5));
            Path log = APISource.getSource().getDataPath().resolve("logs").resolve(year).resolve(month).resolve(day + "." + extension.fileExtension());
            PathUtilities.create(log);
            try {
                List<String> lines = removeHeader(Files.readAllLines(log));
                BufferedWriter writer = Files.newBufferedWriter(log, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
                writer.write(header.get(this.source));
                for (String line : lines)
                    writer.write(line + "\n");
                writer.write(StringUtils.formatString("[ {0} - {1} ] {2}<br>", new Object[]{level.name(), time, StringUtils.formatString(info, replaces)}));
                writer.flush();
                writer.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    public void scheduleLog(@NotNull Level level, @NotNull Throwable info) {
        APISource.asyncScheduler().queue(() -> {
            LogExtension extension = ext_type.getOrDefault(this.source, LogExtension.MARKDOWN);
            Calendar calendar = ((LogCalendar) calendar_type.getOrDefault(this.source, LogCalendar.GREGORIAN)).getType();
            String time = String.format("%02d", new Object[]{Integer.valueOf(calendar.get(11))}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(calendar.get(12))}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(calendar.get(13))});
            String year = String.valueOf(calendar.get(1));
            String month = calendar.getDisplayName(2, 2, Locale.getDefault());
            String day = String.valueOf(calendar.get(5));
            Path log = APISource.getSource().getDataPath().resolve("logs").resolve(year).resolve(month).resolve(day + "." + extension.fileExtension());
            PathUtilities.create(log);
            try {
                List<String> lines = removeHeader(Files.readAllLines(log));
                BufferedWriter writer = Files.newBufferedWriter(log, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0]);
                writer.write(header.get(this.source));
                for (String line : lines)
                    writer.write(line + "\n");
                Throwable prefix = new Throwable(info);
                writer.write(StringUtils.formatString("[ {0} - {1} ] {2}\n", new Object[]{level.name(), time, prefix.fillInStackTrace()}));
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

    private List<String> removeHeader(List<String> lines) {
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
