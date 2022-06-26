package ml.karmaconfigs.api.common.karma.file;

import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.file.element.KarmaArray;
import ml.karmaconfigs.api.common.karma.file.element.KarmaElement;
import ml.karmaconfigs.api.common.karma.file.element.KarmaKeyArray;
import ml.karmaconfigs.api.common.karma.file.element.KarmaObject;
import ml.karmaconfigs.api.common.karma.file.error.KarmaFormatException;
import ml.karmaconfigs.api.common.karmafile.AsyncKarmaFile;
import ml.karmaconfigs.api.common.karmafile.KarmaFile;
import ml.karmaconfigs.api.common.karmafile.Key;
import ml.karmaconfigs.api.common.timer.scheduler.LateScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.worker.AsyncLateScheduler;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import ml.karmaconfigs.api.common.utils.string.OptionsBuilder;
import ml.karmaconfigs.api.common.utils.string.RandomString;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.string.util.TextContent;
import ml.karmaconfigs.api.common.utils.string.util.TextType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The KarmaFile, that contains all the file data
 *
 * @author KarmaDev
 * @since 1.3.2-SNAPSHOT
 */
public class KarmaMain {

    private final Path document;

    private final Map<String, KarmaElement> content = new LinkedHashMap<>();
    private final Map<KarmaElement, String> reverse = new LinkedHashMap<>();
    private final Map<String, Integer> indexes = new HashMap<>();

    private String raw = "";
    private InputStream internal = null;

    /**
     * Initialize the file
     *
     * @throws IOException if the temporal file could not be
     * created
     */
    public KarmaMain() throws IOException {
        OptionsBuilder builder = RandomString.createBuilder()
                .withContent(TextContent.NUMBERS_AND_LETTERS)
                .withSize(16)
                .withType(TextType.RANDOM_SIZE);
        String random = StringUtils.generateString(builder).create();
        document = Files.createTempFile(random, "-kf");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> PathUtilities.destroy(document)));
    }

    /**
     * Initialize the file
     *
     * @param doc the file that must be read
     * @throws IllegalStateException if something goes wrong
     */
    public KarmaMain(final KarmaSource source, final Path doc) throws RuntimeException {
        if (source.migrateLegacyKarmaFile()) {
            List<String> lines = PathUtilities.readAllLines(doc);

            if (!lines.isEmpty()) {
                String first = lines.get(0);

                if (!first.startsWith("(") && !first.startsWith("(\"main\"")) {
                    //KarmaConfig config = new KarmaConfig(); Cannot use KarmaConfig as it depends on KarmaMain. Recursion causes crash

                    //Must be migrated
                    //if (config.debug(Level.WARNING)) {
                        source.console().send("Preparing to migrate from legacy karma file {0} to modern format", Level.WARNING, PathUtilities.getPrettyPath(doc));
                    //}

                    try {
                        @SuppressWarnings("deprecation")
                        KarmaMain migrated = KarmaMain.fromLegacy(source, new KarmaFile(doc));
                        if (migrated.save(doc)) {
                            //if (config.debug(Level.OK)) {
                                source.console().send("Migrated successfully from legacy karma file {0}", Level.OK, PathUtilities.getPrettyPath(doc));
                            //}
                        } else {
                            //if (config.log(Level.WARNING)) {
                                source.logger().scheduleLog(Level.WARNING, "Failed when migrating from legacy karma file {0} to modern karma main file", PathUtilities.getPrettyPath(doc));
                            //}
                            //if (config.debug(Level.GRAVE)) {
                                source.console().send("Failed to migrate from legacy karma file {0}", Level.GRAVE, PathUtilities.getPrettyPath(doc));
                            //}
                        }
                    } catch (Throwable ex) {
                        //if (config.log(Level.GRAVE)) {
                            source.logger().scheduleLog(Level.GRAVE, ex);
                            source.logger().scheduleLog(Level.INFO, "Failed when migrating from legacy karma file {0} to modern karma main file", PathUtilities.getPrettyPath(doc));
                        //}

                        //if (config.debug(Level.GRAVE)) {
                            source.console().send("Failed to migrate from legacy karma file {0}", Level.GRAVE, PathUtilities.getPrettyPath(doc));
                        //}
                    }
                }
            }
        }

        document = doc;
        preCache();
    }

    /**
     * Initialize the file
     *
     * @param source the source file
     * @param name the file name
     * @param path the file path
     */
    public KarmaMain(final KarmaSource source, final String name, final String... path) {
        Path main = source.getDataPath();
        for (String str : path)
            main = main.resolve(str);

        document = main.resolve(name);

        if (source.migrateLegacyKarmaFile()) {
            List<String> lines = PathUtilities.readAllLines(document);

            if (!lines.isEmpty()) {
                String first = lines.get(0);

                if (!first.startsWith("(") && !first.startsWith("(\"main\"")) {
                    //KarmaConfig config = new KarmaConfig();

                    //Must be migrated
                    //if (config.debug(Level.WARNING)) {
                        source.console().send("Preparing to migrate from legacy karma file {0} to modern format", Level.WARNING, PathUtilities.getPrettyPath(document));
                    //}

                    try {
                        @SuppressWarnings("deprecation")
                        KarmaMain migrated = KarmaMain.fromLegacy(source, new KarmaFile(document));
                        if (migrated.save(document)) {
                            //if (config.debug(Level.OK)) {
                                source.console().send("Migrated successfully from legacy karma file {0}", Level.OK, PathUtilities.getPrettyPath(document));
                            //}
                        } else {
                            //if (config.log(Level.WARNING)) {
                                source.logger().scheduleLog(Level.WARNING, "Failed when migrating from legacy karma file {0} to modern karma main file", PathUtilities.getPrettyPath(document));
                            //}
                            //if (config.debug(Level.GRAVE)) {
                                source.console().send("Failed to migrate from legacy karma file {0}", Level.GRAVE, PathUtilities.getPrettyPath(document));
                            //}
                        }
                    } catch (Throwable ex) {
                        //if (config.log(Level.GRAVE)) {
                            source.logger().scheduleLog(Level.GRAVE, ex);
                            source.logger().scheduleLog(Level.INFO, "Failed when migrating from legacy karma file {0} to modern karma main file", PathUtilities.getPrettyPath(document));
                        //}

                        //if (config.debug(Level.GRAVE)) {
                            source.console().send("Failed to migrate from legacy karma file {0}", Level.GRAVE, PathUtilities.getPrettyPath(document));
                        //}
                    }
                }
            }
        }

        preCache();
    }

    /**
     * Initialize the file
     *
     * @param doc the file that must be read
     * @throws IOException if the temporal file could not
     * be created
     */
    public KarmaMain(final InputStream doc) throws IOException {
        OptionsBuilder builder = RandomString.createBuilder()
                .withContent(TextContent.NUMBERS_AND_LETTERS)
                .withSize(16)
                .withType(TextType.RANDOM_SIZE);

        String random = StringUtils.generateString(builder).create();
        document = Files.createTempFile(random, "-kf");
        Files.copy(doc, document, StandardCopyOption.REPLACE_EXISTING);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> PathUtilities.destroy(document)));

        preCache();
    }

    /**
     * Initialize the file
     *
     * @param raw the raw karma main data
     * @throws IOException if the temporal file could not
     * be created
     */
    public KarmaMain(final String raw) throws IOException {
        OptionsBuilder builder = RandomString.createBuilder()
                .withContent(TextContent.NUMBERS_AND_LETTERS)
                .withSize(16)
                .withType(TextType.RANDOM_SIZE);
        String random = StringUtils.generateString(builder).create();
        document = Files.createTempFile(random, "-kf");
        Files.write(document, raw.getBytes(StandardCharsets.UTF_8));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            PathUtilities.destroy(document);
        }));

        preCache();
    }

    /**
     * Set the internal file to read from when generating defaults
     * or saving
     *
     * @param in the internal file
     * @return this instance
     */
    public KarmaMain internal(final InputStream in) {
        internal = in;
        return this;
    }

    /**
     * Pre cache all the file data to retrieve
     * it faster
     *
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public void preCache() throws KarmaFormatException {
        if (StringUtils.isNullOrEmpty(raw)) {
            List<String> lines = PathUtilities.readAllLines(document);
            if (lines.isEmpty() && internal != null) {
                try {
                    Files.copy(internal, document, StandardCopyOption.REPLACE_EXISTING);
                    lines = PathUtilities.readAllLines(document);
                } catch (Throwable ignored) {
                }
            }

            if (!lines.isEmpty()) {
                String fileLines = StringUtils.listToString(lines, false);
                Pattern blockComment = Pattern.compile("\\*\\((?:.|[\\n\\r])*?\\)\\*|\\*/.*");
                Set<String> comments = new LinkedHashSet<>();
                Matcher commentMatcher = blockComment.matcher(fileLines);
                String remove_string = StringUtils.generateString(RandomString.createBuilder().withSize(32)).create();
                while (commentMatcher.find()) {
                    int start = commentMatcher.start();
                    int end = commentMatcher.end();

                    comments.add(fileLines.substring(start, end));
                }

                for (String comment : comments) {
                    fileLines = fileLines.replace(comment, remove_string);
                }

                String[] data = fileLines.split("\n");
                StringBuilder builder = new StringBuilder();
                for (String fl : data) {
                    if (!fl.replaceAll("\\s", "").equals(remove_string)) {
                        builder.append(fl).append("\n");
                    }
                }
                String result = builder.toString();
                lines = new ArrayList<>(Arrays.asList(StringUtils.replaceLast(result, "\n", "").split("\n")));

                StringBuilder rawBuilder = new StringBuilder();

                boolean underComment = false;
                boolean jump = true;
                boolean parsedFirst = false;

                String breaking = null;
                int index = 0;

                Set<String> added = new HashSet<>();
                for (String line : lines) {
                    if (!line.replaceAll("\\s", "").startsWith("*/")) {
                        int size = line.length();
                        indexes.put(line, ++index);

                        if (breaking != null) {
                            throw new KarmaFormatException(document, breaking, index);
                        }

                        boolean string = false;
                        for (int i = 0; i < size; i++) {
                            char current = line.charAt(i);
                            int nextIndex = (i + 1 != size ? i + 1 : i);
                            char next = line.charAt(nextIndex);

                            if (!underComment) {
                                jump = true;

                                if (current == '*') {
                                    if (next == '(') {
                                        underComment = true;
                                        jump = false;
                                    } else {
                                        i = size;
                                        continue;
                                    }
                                }

                                if (!underComment) {
                                    if (current == '(') {
                                        if (next != '"') {
                                            if (!parsedFirst) {
                                                parsedFirst = true;
                                                added.add("main");
                                                indexes.put("main", index);
                                            } else {
                                                breaking = "Error, found invalid section definition at " + line + ", it must be (\"x\" where 'x' is any value!";
                                            }
                                        } else {
                                            StringBuilder secName = new StringBuilder();
                                            boolean broke = false;
                                            for (int x = (nextIndex + 1); x < size; x++) {
                                                char tmp = line.charAt(x);
                                                if (tmp == '"') {
                                                    broke = true;
                                                    break;
                                                }

                                                secName.append(tmp);
                                            }

                                            if (broke) {
                                                String section = secName.toString();
                                                if (!added.contains(section)) {
                                                    added.add(section);
                                                    indexes.put(section, index);
                                                } else {
                                                    breaking = "Error, found repeated section definition " + section;
                                                }
                                            } else {
                                                breaking = "Error, found invalid section definition at " + line + ", it must be (\"x\" where 'x' is any value!";
                                            }
                                        }
                                    }

                                    if (current == '"')
                                        string = !string;

                                    if (!string) {
                                        char prev = line.charAt((i != 0 ? (i - 1) : 0));
                                        char prev1 = line.charAt((i > 2 ? i - 2 : 0));
                                        if (current == '-' && next == '>') {
                                            char cont = line.charAt((nextIndex + 1 != (size - 1) ? (nextIndex + 1) : nextIndex));
                                            char cont1 = line.charAt((nextIndex + 2 != (size - 1) ? (nextIndex + 2) : nextIndex));

                                            boolean error = false;
                                            if (Character.isSpaceChar(prev)) {
                                                if (!Character.isSpaceChar(prev1)) {
                                                    if (Character.isSpaceChar(cont)) {
                                                        if (!Character.isSpaceChar(cont1)) {
                                                            rawBuilder.append(current);
                                                        } else {
                                                            error = true;
                                                        }
                                                    } else {
                                                        error = true;
                                                    }
                                                } else {
                                                    error = true;
                                                }
                                            } else {
                                                if (prev == '<') {
                                                    error = !Character.isSpaceChar(prev1);
                                                } else {
                                                    error = true;
                                                }
                                            }

                                            if (error) {
                                                breaking = "Error, found invalid key -> value definition at " + line + ". It must be 'Key' -> \"Value\"";
                                            }
                                        } else {
                                            if (prev == '-' && prev1 == '<')
                                                rawBuilder.append(prev);

                                            rawBuilder.append(current);
                                        }
                                    } else {
                                        rawBuilder.append(current);
                                    }
                                }
                            } else {
                                underComment = current != ')' && next != '*';
                                if (!underComment) {
                                    i++;
                                    jump = true;
                                }
                            }
                        }

                        if (jump) {
                            rawBuilder.append("\n");
                        }
                    }
                }

                //We need raw text to parse easily the data...
                raw = rawBuilder.toString();
                String[] tmp = raw.split("\n");
                for (int i = 0; i < tmp.length; i++) {
                    if (!StringUtils.isNullOrEmpty(tmp[i])) {
                        raw = rawBuilder.substring(i);
                        break;
                    }
                }

                data = raw.split("\n");
                String main = data[0];
                if (main.equals("(") || main.equals("(\"main\"")) {
                    String parent = "main";
                    for (int i = 1; i < data.length; i++) {
                        String line = data[i];
                        if (line.replaceAll("\\s", "").startsWith("(")) {
                            String name = line.replaceAll("\\s", "");
                            name = name.replaceFirst("\\(", "");
                            name = name.replaceFirst("\"", "");
                            name = name.substring(0, name.length() - 1);

                            parent = parent + "." + name;
                        } else {
                            if (line.contains("->")) {
                                boolean rec = false;
                                Pattern pattern = Pattern.compile("' .?-> ");
                                Pattern badPattern = Pattern.compile("\" .?-> ");

                                Matcher matcher = pattern.matcher(line);
                                Matcher badMatcher = badPattern.matcher(line);

                                if (!matcher.find() && !badMatcher.find()) {
                                    throw new KarmaFormatException(document, "Error, couldn't find valid key format -> or <-> at ( " + line + " )", indexes.getOrDefault(line, -1));
                                }

                                int start;
                                int end;
                                boolean bad = false;
                                try {
                                    start = matcher.start();
                                    end = matcher.end();
                                } catch (Throwable ex) {
                                    start = badMatcher.start();
                                    end = badMatcher.end();
                                    bad = true;
                                }

                                String target = String.valueOf((bad ? '"' : '\''));

                                String match = line.substring(start + 2, end - 2).replaceAll("\\s", "");
                                if (match.equalsIgnoreCase("<-")) {
                                    rec = true;
                                }

                                String[] dt = line.split((rec ? "<->" : "->"));

                                String tmpName = dt[0].replaceAll("\\s", "");
                                if (!tmpName.startsWith(target) && !tmpName.endsWith(target))
                                    throw new KarmaFormatException(document, "Error, invalid key format, it must be 'x' where x is any value", indexes.getOrDefault(line, -1));

                                String name = StringUtils.replaceLast(dt[0].replaceFirst(target, ""), target, "");
                                String key = parent + "." + name.replaceAll("\\s", "");
                                StringBuilder value = new StringBuilder();
                                for (int x = 1; x < dt.length; x++) {
                                    value.append(dt[x]).append((x != dt.length - 1 ? (rec ? "<->" : "->") : ""));
                                }

                                if (!StringUtils.isNullOrEmpty(value)) {
                                    if (value.toString().replaceAll("\\s", "").startsWith("{")) {
                                        boolean keyed = false;
                                        boolean simple = false;
                                        KarmaElement array = new KarmaArray();
                                        int subIndex = 0;

                                        String parentKey = key;
                                        for (int x = (i + 1); x < data.length; x++, subIndex++) {
                                            line = data[x];

                                            if (!line.replaceAll("\\s", "").endsWith("}")) {
                                                if (line.contains("<->")) {
                                                    if (!(array instanceof KarmaKeyArray))
                                                        array = new KarmaKeyArray();

                                                    if (!simple) {
                                                        keyed = true;

                                                        dt = line.split("->");

                                                        tmpName = dt[0].replaceAll("\\s", "");
                                                        if (!tmpName.startsWith("'") && !tmpName.endsWith("'"))
                                                            throw new KarmaFormatException(document, "Error, invalid key format, it must be 'x' where x is any value", indexes.getOrDefault(line, -1));

                                                        int i1 = dt[0].indexOf("'");
                                                        int i2 = dt[0].lastIndexOf("'");

                                                        name = StringUtils.replaceLast(dt[0], "<", "");
                                                        key = name.substring(i1 + 1, i2);
                                                        value = new StringBuilder();
                                                        for (int y = 1; y < dt.length; y++) {
                                                            value.append(dt[y]).append((y != dt.length - 1 ? "->" : ""));
                                                        }

                                                        KarmaKeyArray ka = (KarmaKeyArray) array;

                                                        String v = value.toString().replaceFirst("\t", "").replaceFirst(" ", "");
                                                        if (!StringUtils.isNullOrEmpty(v)) {
                                                            if (v.startsWith("\"") || v.startsWith("'")) {
                                                                if (!v.endsWith((v.startsWith("\"") ? "\"" : "'"))) {
                                                                    throw new KarmaFormatException(document, "Error, invalid text format. It seems that you mixed quotes or forgot to close string", indexes.getOrDefault(line, -1));
                                                                } else {
                                                                    v = v.substring(1, v.length() - 1);
                                                                    KarmaObject obj = new KarmaObject(v);

                                                                    ka.add(key, obj, true);
                                                                }
                                                            } else {
                                                                if (v.replaceAll("\\s", "").equalsIgnoreCase("true") || v.replaceAll("\\s", "").equalsIgnoreCase("false")) {
                                                                    boolean bool = Boolean.parseBoolean(v.replaceAll("\\s", ""));
                                                                    KarmaObject obj = new KarmaObject(bool);

                                                                    ka.add(key, obj, true);
                                                                } else {
                                                                    if (v.contains(",")) {
                                                                        Number number = Double.parseDouble(v.replaceAll("\\s", "").replace(",", "."));
                                                                        KarmaObject obj = new KarmaObject(number);

                                                                        ka.add(key, obj, true);
                                                                    } else {
                                                                        if (v.contains(".")) {
                                                                            Number number = Float.parseFloat(v.replaceAll("\\s", ""));
                                                                            KarmaObject obj = new KarmaObject(number);

                                                                            ka.add(key, obj, true);
                                                                        } else {
                                                                            try {
                                                                                Number number = Integer.parseInt(v.replaceAll("\\s", ""));
                                                                                KarmaObject obj = new KarmaObject(number);

                                                                                ka.add(key, obj, true);
                                                                            } catch (NumberFormatException e) {
                                                                                KarmaObject obj = new KarmaObject(v);

                                                                                ka.add(key, obj, true);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        throw new KarmaFormatException(document, "Error, invalid list format. It seems that you mixed a simple list and a keyed list", indexes.getOrDefault(line, -1));
                                                    }
                                                } else {
                                                    if (line.contains("->")) {
                                                        if (!(array instanceof KarmaKeyArray))
                                                            array = new KarmaKeyArray();

                                                        if (!simple) {
                                                            keyed = true;
                                                            dt = line.split("->");

                                                            tmpName = dt[0].replaceAll("\t", "").replaceAll("\\s", "");
                                                            if (!tmpName.startsWith("'") && !tmpName.endsWith("'"))
                                                                throw new KarmaFormatException(document, "Error, invalid key format, it must be 'x' where x is any value", indexes.getOrDefault(line, -1));

                                                            int i1 = dt[0].indexOf("'");
                                                            int i2 = dt[0].lastIndexOf("'");

                                                            name = StringUtils.replaceLast(dt[0], "<", "");
                                                            key = name.substring(i1 + 1, i2);
                                                            value = new StringBuilder();
                                                            for (int y = 1; y < dt.length; y++) {
                                                                value.append(dt[y]).append((y != dt.length - 1 ? "->" : ""));
                                                            }

                                                            KarmaKeyArray ka = (KarmaKeyArray) array;

                                                            String v = value.toString().replaceFirst("\t", "").replaceFirst(" ", "");
                                                            if (!StringUtils.isNullOrEmpty(v)) {
                                                                if (v.startsWith("\"") || v.startsWith("'")) {
                                                                    if (!v.endsWith((v.startsWith("\"") ? "\"" : "'"))) {
                                                                        throw new KarmaFormatException(document, "Error, invalid text format. It seems that you mixed quotes or forgot to close string", indexes.getOrDefault(line, -1));
                                                                    } else {
                                                                        v = v.substring(1, v.length() - 1);
                                                                        KarmaObject obj = new KarmaObject(v);

                                                                        ka.add(key, obj, false);
                                                                    }
                                                                } else {
                                                                    if (v.replaceAll("\\s", "").equalsIgnoreCase("true") || v.replaceAll("\\s", "").equalsIgnoreCase("false")) {
                                                                        boolean bool = Boolean.parseBoolean(v.replaceAll("\\s", ""));
                                                                        KarmaObject obj = new KarmaObject(bool);

                                                                        ka.add(key, obj, false);
                                                                    } else {
                                                                        if (v.contains(",")) {
                                                                            Number number = Double.parseDouble(v.replaceAll("\\s", "").replace(",", "."));
                                                                            KarmaObject obj = new KarmaObject(number);

                                                                            ka.add(key, obj, false);
                                                                        } else {
                                                                            if (v.contains(".")) {
                                                                                Number number = Float.parseFloat(v.replaceAll("\\s", ""));
                                                                                KarmaObject obj = new KarmaObject(number);

                                                                                ka.add(key, obj, false);
                                                                            } else {
                                                                                try {
                                                                                    Number number = Integer.parseInt(v.replaceAll("\\s", ""));
                                                                                    KarmaObject obj = new KarmaObject(number);

                                                                                    ka.add(key, obj, false);
                                                                                } catch (NumberFormatException e) {
                                                                                    KarmaObject obj = new KarmaObject(v);

                                                                                    ka.add(key, obj, false);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            throw new KarmaFormatException(document, "Error, invalid list format. It seems that you mixed a simple list and a keyed list", indexes.getOrDefault(line, -1));
                                                        }
                                                    } else {
                                                        if (!keyed) {
                                                            if (!(array instanceof KarmaArray))
                                                                array = new KarmaArray();

                                                            simple = true;

                                                            value = new StringBuilder();
                                                            boolean parsing = false;
                                                            for (int y = 1; y < line.length(); y++) {
                                                                char character = line.charAt(y);
                                                                if (!parsing) {
                                                                    if (!Character.isSpaceChar(character)) {
                                                                        parsing = true;
                                                                    }
                                                                }

                                                                if (parsing) {
                                                                    value.append(character);
                                                                }
                                                            }

                                                            KarmaArray ka = (KarmaArray) array;

                                                            String v = value.toString().replaceFirst("\t", "");
                                                            if (!StringUtils.isNullOrEmpty(v)) {
                                                                if (!v.equals("{")) {
                                                                    if (v.startsWith("\"") || v.startsWith("'")) {
                                                                        if (!v.endsWith((v.startsWith("\"") ? "\"" : "'"))) {
                                                                            throw new KarmaFormatException(document, "Error, invalid text format. It seems that you mixed quotes or forgot to close string", indexes.getOrDefault(line, -1));
                                                                        } else {
                                                                            v = v.substring(1, v.length() - 1);
                                                                            KarmaObject obj = new KarmaObject(v);

                                                                            ka.add(obj);
                                                                        }
                                                                    } else {
                                                                        if (v.replaceAll("\\s", "").equalsIgnoreCase("true") || v.replaceAll("\\s", "").equalsIgnoreCase("false")) {
                                                                            boolean bool = Boolean.parseBoolean(v.replaceAll("\\s", ""));
                                                                            KarmaObject obj = new KarmaObject(bool);

                                                                            ka.add(obj);
                                                                        } else {
                                                                            if (v.contains(",")) {
                                                                                Number number = Double.parseDouble(v.replaceAll("\\s", "").replace(",", "."));
                                                                                KarmaObject obj = new KarmaObject(number);

                                                                                ka.add(obj);
                                                                            } else {
                                                                                if (v.contains(".")) {
                                                                                    Number number = Float.parseFloat(v.replaceAll("\\s", ""));
                                                                                    KarmaObject obj = new KarmaObject(number);

                                                                                    ka.add(obj);
                                                                                } else {
                                                                                    try {
                                                                                        Number number = Integer.parseInt(v.replaceAll("\\s", ""));
                                                                                        KarmaObject obj = new KarmaObject(number);

                                                                                        ka.add(obj);
                                                                                    } catch (NumberFormatException e) {
                                                                                        KarmaObject obj = new KarmaObject(v);

                                                                                        ka.add(obj);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            throw new KarmaFormatException(document, "Error, invalid list format. It seems that you mixed a simple list and a keyed list", indexes.getOrDefault(line, -1));
                                                        }
                                                    }
                                                }
                                            } else {
                                                i = x;
                                                break;
                                            }
                                        }

                                        content.put(parentKey, array);
                                        if (rec)
                                            reverse.put(array, parentKey);
                                    } else {
                                        String v = value.toString().replaceFirst("\t", "").replaceFirst(" ", "");
                                        if (v.startsWith("\"") || v.startsWith("'")) {
                                            if (!v.endsWith((v.startsWith("\"") ? "\"" : "'"))) {
                                                throw new KarmaFormatException(document, "Error, invalid text format. It seems that you mixed quotes or forgot to close string", indexes.getOrDefault(line, -1));
                                            } else {
                                                v = v.substring(1, v.length() - 1);
                                                KarmaObject obj = new KarmaObject(v);

                                                content.put(key, obj);
                                                if (rec)
                                                    reverse.put(obj, key);
                                            }
                                        } else {
                                            if (v.replaceAll("\\s", "").equalsIgnoreCase("true") || v.replaceAll("\\s", "").equalsIgnoreCase("false")) {
                                                boolean bool = Boolean.parseBoolean(v.replaceAll("\\s", ""));
                                                KarmaObject obj = new KarmaObject(bool);

                                                content.put(key, obj);
                                                if (rec)
                                                    reverse.put(obj, key);
                                            } else {
                                                if (v.contains(",")) {
                                                    Number number = Double.parseDouble(v.replaceAll("\\s", "").replace(",", "."));
                                                    KarmaObject obj = new KarmaObject(number);

                                                    content.put(key, obj);
                                                    if (rec)
                                                        reverse.put(obj, key);
                                                } else {
                                                    if (v.contains(".")) {
                                                        Number number = Float.parseFloat(v.replaceAll("\\s", ""));
                                                        KarmaObject obj = new KarmaObject(number);

                                                        content.put(key, obj);
                                                        if (rec)
                                                            reverse.put(obj, key);
                                                    } else {
                                                        try {
                                                            Number number = Integer.parseInt(v.replaceAll("\\s", ""));
                                                            KarmaObject obj = new KarmaObject(number);

                                                            content.put(key, obj);
                                                            if (rec)
                                                                reverse.put(obj, key);
                                                        } catch (NumberFormatException e) {
                                                            KarmaObject obj = new KarmaObject(v);

                                                            content.put(key, obj);
                                                            if (rec)
                                                                reverse.put(obj, key);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    KarmaObject obj = new KarmaObject("");

                                    content.put(key, obj);
                                    if (rec)
                                        reverse.put(obj, key);
                                }
                            } else {
                                if (line.replaceAll("\\s", "").endsWith(")")) {
                                    if (parent.contains(".")) {
                                        String[] pData = parent.split("\\.");
                                        parent = StringUtils.replaceLast(parent, "." + pData[pData.length - 1], "");
                                        if (pData.length == 1)
                                            parent = pData[0];
                                    }
                                }
                            }
                        }
                    }

                    if (!parent.equals("main")) {
                        String[] pData = parent.split("\\.");
                        throw new KarmaFormatException(document, "Error, non closed section path ( " + parent + " )", indexes.getOrDefault(pData[pData.length - 1], -1));
                    }
                } else {
                    throw new KarmaFormatException(document, "Error, found invalid main section name at " + main + "; it must be \"main\" or empty!", indexes.getOrDefault(main, -1));
                }
            }
        }
    }

    /**
     * Export the default file, reading from internal.
     *
     * PLEASE NOTE:
     * Executing this method will replace all the file contents without
     * performing any type of check first
     *
     * @return if the file could be exported
     */
    public boolean exportDefaults() {
        if (internal != null) {
            try {
                PathUtilities.create(document);
                Files.copy(internal, document, StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (Throwable ignored) {
            }
        }

        return false;
    }

    /**
     * Get the file content as a raw string without comments
     *
     * @return the raw contents
     */
    public String getRaw() {
        if (StringUtils.isNullOrEmpty(raw))
            preCache();

        return raw;
    }

    /**
     * Get all the main section keys
     *
     * @return the file keys
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public Set<String> getKeys() throws KarmaFormatException {
        return new LinkedHashSet<>(content.keySet());
    }

    /**
     * Get a value
     *
     * @param key the value key
     * @return the value
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public KarmaElement get(final String key) throws KarmaFormatException {
        String tmpKey = key;
        if (!tmpKey.startsWith("main."))
            tmpKey = "main." + key;

        return content.getOrDefault(tmpKey, null);
    }

    /**
     * Get a value
     *
     * @param key the value key
     * @param def the default value
     * @return the value
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public KarmaElement get(final String key, final KarmaElement def) {
        String tmpKey = key;
        if (!tmpKey.startsWith("main."))
            tmpKey = "main." + key;

        return content.getOrDefault(tmpKey, def);
    }

    /**
     * Get a key
     *
     * @param element the key value
     * @return the key
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public String get(final KarmaElement element) {
        return reverse.getOrDefault(element, "").replaceFirst("main\\.", "");
    }

    /**
     * Get a key
     *
     * @param element the key value
     * @param def the default key
     * @return the key
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public String get(final KarmaElement element, final String def) {
        return reverse.getOrDefault(element, def).replaceFirst("main\\.", "");
    }

    /**
     * Get if a key element is recursive
     *
     * @param key the key
     * @return if the key element is recursive
     */
    public boolean isRecursive(final String key) {
        String tmpKey = key;
        if (!tmpKey.startsWith("main."))
            tmpKey = "main." + key;

        KarmaElement element = content.getOrDefault(tmpKey, null);
        if (element != null) {
            //Return true if the key element can be retrieved with the key and vice versa
            String tmp = reverse.getOrDefault(element, null);
            return tmp.equals(tmpKey);
        }

        return false;
    }

    /**
     * Get if a element is recursive
     *
     * @param element the element
     * @return if the element is recursive
     */
    public boolean isRecursive(final KarmaElement element) {
        String key = reverse.getOrDefault(element, null);
        if (key != null) {
            //Return true if the element key can be retrieved with the element and vice versa
            KarmaElement tmp = content.getOrDefault(key, null);
            return tmp == element;
        }

        return false;
    }

    /**
     * Get if a key is set
     *
     * @param key the key to find
     * @return if the key is set
     */
    public boolean isSet(final String key) {
        String tmpKey = key;
        if (!tmpKey.startsWith("main."))
            tmpKey = "main." + key;

        KarmaElement result = content.getOrDefault(tmpKey, null);
        return result != null && result.isValid();
    }

    /**
     * Get if the document file exists
     *
     * @return if the document file exists
     */
    public boolean exists() {
        return Files.exists(document);
    }

    /**
     * Set a value
     *
     * @param key the value key
     * @param element the value
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public void set(final String key, final KarmaElement element) {
        String tmpKey = key;
        if (!tmpKey.startsWith("main."))
            tmpKey = "main." + tmpKey;

        if (element != null) {
            content.put(tmpKey, element);
        } else {
            content.remove(tmpKey);
        }
    }

    /**
     * Create the document if it doesn't
     * exist
     */
    public void create() {
        PathUtilities.create(document);
    }

    /**
     * Save the file
     *
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public boolean save() {
        return save(document);
    }

    /**
     * Save the file
     *
     * @param flName the target file name
     * @param sub the file subdirectories
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public boolean save(final String flName, final String... sub) throws KarmaFormatException {
        return save(Paths.get(flName, sub));
    }

    /**
     * Save the file
     *
     * @param target the target file
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public boolean save(final Path target) throws KarmaFormatException {
        if (!exists())
            create();

        KarmaSource source = APISource.getOriginal(false);
        try {
            source.console().debug("Saving file {0}", Level.INFO, PathUtilities.getPrettyPath(target));

            List<String> write = new ArrayList<>();
            List<String> lines = PathUtilities.readAllLines(document);
            Set<String> wrote_keys = new HashSet<>();

            if (!lines.isEmpty()) {
                source.console().debug("File is not empty", Level.INFO);

                write.add("(\"main\"");
                Pattern keyMatcher = Pattern.compile("'.*' .?->");
                Pattern badKeyMatcher = Pattern.compile("\".*\" .?->");

                StringBuilder section = new StringBuilder();

                int index = 1;
                boolean bigComment = false;
                boolean readingList = false;
                for (String line : lines) {
                    String noSpace = line.replaceAll("\\s", "");
                    if (noSpace.equals("\n") || StringUtils.isNullOrEmpty(noSpace)) {
                        write.add(line);
                        continue;
                    }

                    Matcher matcher = keyMatcher.matcher(line);
                    Matcher badMatcher = badKeyMatcher.matcher(line);

                    if (matcher.find() || badMatcher.find() && !readingList) {
                        source.console().debug("Found key!", Level.INFO);

                        if (section.toString().isEmpty())
                            section.append("main");

                        int start;
                        int end;
                        try {
                            start = matcher.start();
                            end = matcher.end();
                        } catch (Throwable ex) {
                            start = badMatcher.start();
                            end = badMatcher.end();
                        }


                        String space = line.substring(0, start);
                        String result = line.substring(start, end);

                        source.console().debug("The path is: {0} ( From line: {1} )", Level.INFO, result, line);

                        boolean recursive = line.endsWith("<->");
                        String name = result.substring(1, result.length() - (recursive ? 5 : 4));

                        source.console().debug("Key name is: {0}", Level.INFO, name);

                        String key = section + "." + name;
                        String value = line.replaceFirst(line.substring(0, start) + result + " ", "");

                        KarmaElement element = content.getOrDefault(key, null);
                        if (element == null && internal != null) {
                            KarmaMain tmp = new KarmaMain(internal);
                            element = tmp.content.getOrDefault(key, null);
                        }

                        if (element != null) {
                            wrote_keys.add(key);

                            source.console().debug("Key {0} has a known value: {1}", Level.INFO, key, element);

                            if (element.isString() || element.isBoolean() || element.isNumber()) {
                                source.console().debug("Wrote!", Level.INFO);
                                write.add(space + "'" + name + "' " + (recursive ? "<-> " : "-> ") + element);
                            } else {
                                if (!readingList) {
                                    if (value.startsWith("{")) {
                                        readingList = true;
                                        write.add(space + "'" + name + "' " + (recursive ? "<-> " : "-> ") + "{");

                                        if (element.isKeyArray()) {
                                            source.console().debug("Writing map", Level.INFO);

                                            KarmaKeyArray kA = element.getKeyArray();
                                            kA.getKeys().forEach((k) -> {
                                                KarmaElement kAElement = kA.get(k);
                                                boolean rec = kA.isRecursive(k) || kA.isRecursive(kAElement);

                                                write.add(space + "\t'" + k + "' " + (rec ? "<-> " : "-> ") + kAElement.getObjet());
                                            });
                                        } else {
                                            source.console().debug("Writing list", Level.INFO);

                                            KarmaArray a = element.getArray();
                                            for (KarmaElement sub : a) {
                                                write.add(space + "\t'" + sub.getObjet().textValue() + "'");
                                            }
                                        }
                                    }
                                } else {
                                    if (line.endsWith("}")) {
                                        readingList = false;
                                        write.add(line);
                                    }
                                }
                            }
                        } else {
                            APISource.getOriginal(false).logger().scheduleLog(Level.WARNING,
                                    "An error occurred while saving file {0}. Required key {1} is not defined{2}. The file will be try to be saved anyway",
                                    PathUtilities.getPrettyPath(document),
                                    key,
                                    (internal == null ? " ( setting internal file may fix the issue )" : " ( internal file does not contain the key neither )"));

                            APISource.getOriginal(false).console().send("An error occurred while saving file {0} because the key {1} is not defined{2}. The file will be try to be saved anyway",
                                    Level.WARNING,
                                    PathUtilities.getPrettyPath(document),
                                    key,
                                    (internal == null ? " ( setting internal file may fix the issue )" : " ( internal file does not contain the key neither )"));
                        }
                    } else {
                        if (readingList) {
                            if (line.endsWith("}")) {
                                readingList = false;
                                write.add(line);
                            }
                        } else {
                            Pattern sectMatcher = Pattern.compile("\\(\".*\"");

                            matcher = sectMatcher.matcher(line);

                            if (line.replaceAll("\\s", "").equals(")") && !section.toString().equals("main")) {
                                write.add(line);

                                String current = section.toString();
                                String[] data = current.split("\\.");
                                if (data.length >= 1)
                                    current = current.replace("." + data[data.length - 1], "");

                                section = new StringBuilder(current);
                                continue;
                            }

                            if (matcher.find()) {
                                int start = matcher.start();
                                int end = matcher.end();

                                if (line.contains("\"")) {
                                    String name = line.substring(start + 2, end - 1);

                                    if (section.length() > 0) {
                                        section.append(".").append(name);
                                    } else {
                                        section = new StringBuilder(name);
                                    }

                                    if (!name.equalsIgnoreCase("main")) {
                                        write.add(line);
                                    }
                                } else {
                                    if (!section.toString().equals("main")) {
                                        section = new StringBuilder("main");
                                    } else {
                                        throw new KarmaFormatException(document, "Error, couldn't save file because the main section has been defined two or more times", index);
                                    }
                                }
                            } else {
                                if (!bigComment) {
                                    Pattern comment = Pattern.compile("(\\*\\(.[^)*]*\\)\\*)|(\\*\\(\\n[^)*]*\\)\\*)|(\\*/.[^\\n]*)");
                                    matcher = comment.matcher(line);

                                    if (matcher.find()) {
                                        write.add(line);
                                    } else {
                                        bigComment = line.replaceAll("\\s", "").startsWith("*(");
                                        if (bigComment) {
                                            write.add(line);
                                        }
                                    }
                                } else {
                                    write.add(line);
                                    bigComment = !line.endsWith(")*");
                                }
                            }
                        }
                    }
                }

                Map<String, Map<String, KarmaElement>> sections = new LinkedHashMap<>();
                for (String key : content.keySet()) {
                    if (key.contains(".")) {
                        String[] data = key.split("\\.");
                        if (data.length > 2) {
                            String realKey = data[data.length - 1];
                            String realPath = key.replace("." + realKey, "");

                            Map<String, KarmaElement> values = sections.getOrDefault(realPath, new LinkedHashMap<>());
                            values.put(realKey, content.get(key));

                            sections.put(realPath, values);
                        } else {
                            Map<String, KarmaElement> values = sections.getOrDefault("main", new LinkedHashMap<>());
                            values.put(data[1], content.get(key));

                            sections.put("main", values);
                        }
                    }
                }

                int i2;
                int sectionIndex = 0;
                for (String s : sections.keySet()) {
                    boolean wrote = false;
                    sectionIndex++;

                    if (!s.equals("main")) {
                        if (!wrote_keys.contains(s)) {
                            wrote = true;

                            if (s.contains(".")) {
                                String[] data = s.split("\\.");

                                i2 = 1;
                                StringBuilder realKeyBuilder = new StringBuilder("main");
                                for (String sub : data) {
                                    if (!sub.equals("main")) {
                                        realKeyBuilder.append(".").append(sub);

                                        StringBuilder b = new StringBuilder();
                                        for (int i = 0; i < i2; i++)
                                            b.append("\t");

                                        write.add(b + "(\"" + sub + "\"");
                                        Map<String, KarmaElement> values = sections.getOrDefault(realKeyBuilder.toString(), new LinkedHashMap<>());

                                        source.console().debug("Section: {0}", Level.INFO, sub);

                                        for (String key : values.keySet()) {
                                            KarmaElement value = values.get(key);
                                            if (value.isArray()) {
                                                source.console().debug("Writing list {0}", Level.INFO, key);

                                                write.add(b + "\t'" + key + "' -> {");
                                                KarmaArray array = value.getArray();

                                                array.forEach((element) -> {
                                                    write.add(b + "\t\t'" + element.getObjet().textValue() + "'");
                                                });
                                                write.add(b + "\t}");
                                            } else {
                                                if (value.isKeyArray()) {
                                                    source.console().debug("Writing map {0}", Level.INFO, key);

                                                    write.add(b + "\t'" + key + "' -> {");
                                                    KarmaKeyArray array = value.getKeyArray();

                                                    array.getKeys().forEach((k) -> {
                                                        KarmaElement val = array.get(k);
                                                        if (array.isRecursive(k)) {
                                                            write.add(b + "\t\t'" + k + "' <-> " + val.toString());
                                                        } else {
                                                            write.add(b + "\t\t'" + k + "' -> " + val.toString());
                                                        }
                                                    });
                                                    write.add(b + "\t}");
                                                } else {
                                                    source.console().debug("Writing key {0} with value: {1}", Level.INFO, key, values.get(key));

                                                    write.add(b + "\t'" + key + "' -> " + values.get(key).toString());
                                                }
                                            }
                                        }
                                        write.add(b + ")");

                                        i2++;
                                    }
                                }
                            } else {
                                source.console().debug("Section: {0}", Level.INFO, s);

                                write.add("\t(\"" + s + "\"");
                                Map<String, KarmaElement> values = sections.getOrDefault(s, new LinkedHashMap<>());

                                for (String key : values.keySet()) {
                                    KarmaElement value = values.get(key);

                                    if (value.isArray()) {
                                        source.console().debug("Writing list {0}", Level.INFO, key);

                                        write.add("\t\t'" + key + "' -> {");
                                        KarmaArray array = value.getArray();

                                        array.forEach((element) -> {
                                            write.add("\t\t\t'" + element.getObjet().textValue() + "'");
                                        });
                                        write.add("\t\t}");
                                    } else {
                                        if (value.isKeyArray()) {
                                            source.console().debug("Writing map {0}", Level.INFO, key);

                                            write.add("\t\t'" + key + "' -> {");
                                            KarmaKeyArray array = value.getKeyArray();

                                            array.getKeys().forEach((k) -> {
                                                KarmaElement val = array.get(k);
                                                if (array.isRecursive(k)) {
                                                    write.add("\t\t\t'" + k + "' <-> " + val.toString());
                                                } else {
                                                    write.add("\t\t\t'" + k + "' -> " + val.toString());
                                                }
                                            });
                                            write.add("\t\t}");
                                        } else {
                                            source.console().debug("Writing key {0} with value: {1}", Level.INFO, key, values.get(key));

                                            write.add("\t\t'" + key + "' -> " + values.get(key).toString());
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        source.console().debug("Section: main", Level.INFO);

                        Map<String, KarmaElement> values = sections.getOrDefault(s, new LinkedHashMap<>());

                        for (String key : values.keySet()) {
                            if (!wrote_keys.contains("main." + key)) {
                                wrote = true;

                                write.add("");
                                KarmaElement value = values.get(key);

                                if (value.isArray()) {
                                    source.console().debug("Writing list {0}", Level.INFO, key);

                                    write.add("\t'" + key + "' -> {");
                                    KarmaArray array = value.getArray();

                                    array.forEach((element) -> {
                                        write.add("\t\t'" + element.getObjet().textValue() + "'");
                                    });
                                    write.add("\t}");
                                } else {
                                    if (value.isKeyArray()) {
                                        source.console().debug("Writing map {0}", Level.INFO, key);

                                        write.add("\t'" + key + "' -> {");
                                        KarmaKeyArray array = value.getKeyArray();

                                        array.getKeys().forEach((k) -> {
                                            KarmaElement val = array.get(k);
                                            if (array.isRecursive(k)) {
                                                write.add("\t\t'" + k + "' <-> " + val.toString());
                                            } else {
                                                write.add("\t\t'" + k + "' -> " + val.toString());
                                            }
                                        });
                                        write.add("\t}");
                                    } else {
                                        source.console().debug("Writing key {0} with value: {1}", Level.INFO, key, values.get(key));

                                        write.add("\t'" + key + "' -> " + values.get(key).toString());
                                    }
                                }
                            }
                        }
                    }

                    if (wrote) {
                        if (sectionIndex != sections.size()) {
                            write.add("");
                        }
                    }
                }

                write.add(")");

                String firstLine = write.get(0);
                if (!firstLine.equals("(") && !firstLine.equals("(\"main\"")) {
                    write.add(0, "(\"main\"");
                }
            } else {
                source.console().debug("File is empty. Storing only set paths", Level.INFO);

                //Basically the file is new and we must set the values
                Map<String, Map<String, KarmaElement>> sections = new LinkedHashMap<>();
                for (String key : content.keySet()) {
                    if (key.contains(".")) {
                        String[] data = key.split("\\.");
                        if (data.length > 2) {
                            String realKey = data[data.length - 1];
                            String realPath = key.replace("." + realKey, "");

                            Map<String, KarmaElement> values = sections.getOrDefault(realPath, new LinkedHashMap<>());
                            values.put(realKey, content.get(key));

                            sections.put(realPath, values);
                        } else {
                            Map<String, KarmaElement> values = sections.getOrDefault("main", new LinkedHashMap<>());
                            values.put(data[1], content.get(key));

                            sections.put("main", values);
                        }
                    }
                }

                int index;
                int sectionIndex = 0;
                for (String section : sections.keySet()) {
                    sectionIndex++;

                    if (!section.equals("main")) {
                        if (section.contains(".")) {
                            String[] data = section.split("\\.");

                            index = 1;
                            StringBuilder realKeyBuilder = new StringBuilder("main");
                            for (String sub : data) {
                                if (!sub.equals("main")) {
                                    realKeyBuilder.append(".").append(sub);

                                    StringBuilder b = new StringBuilder();
                                    for (int i = 0; i < index; i++)
                                        b.append("\t");

                                    write.add(b + "(\"" + sub + "\"");
                                    Map<String, KarmaElement> values = sections.getOrDefault(realKeyBuilder.toString(), new LinkedHashMap<>());

                                    source.console().debug("Section: {0}", Level.INFO, sub);

                                    for (String key : values.keySet()) {
                                        KarmaElement value = values.get(key);
                                        if (value.isArray()) {
                                            source.console().debug("Writing list {0}", Level.INFO, key);

                                            write.add(b + "\t'" + key + "' -> {");
                                            KarmaArray array = value.getArray();

                                            array.forEach((element) -> {
                                                write.add(b + "\t\t'" + element.getObjet().textValue() + "'");
                                            });
                                            write.add(b + "\t}");
                                        } else {
                                            if (value.isKeyArray()) {
                                                source.console().debug("Writing map {0}", Level.INFO, key);

                                                write.add(b + "\t'" + key + "' -> {");
                                                KarmaKeyArray array = value.getKeyArray();

                                                array.getKeys().forEach((k) -> {
                                                    KarmaElement val = array.get(k);
                                                    if (array.isRecursive(k)) {
                                                        write.add(b + "\t\t'" + k + "' <-> " + val.toString());
                                                    } else {
                                                        write.add(b + "\t\t'" + k + "' -> " + val.toString());
                                                    }
                                                });
                                                write.add(b + "\t}");
                                            } else {
                                                source.console().debug("Writing key {0} with value: {1}", Level.INFO, key, values.get(key));

                                                write.add(b + "\t'" + key + "' -> " + values.get(key).toString());
                                            }
                                        }
                                    }
                                    write.add(b + ")");

                                    index++;
                                }
                            }
                        } else {
                            source.console().debug("Section: {0}", Level.INFO, section);

                            write.add("\t(\"" + section + "\"");
                            Map<String, KarmaElement> values = sections.getOrDefault(section, new LinkedHashMap<>());

                            for (String key : values.keySet()) {
                                KarmaElement value = values.get(key);

                                if (value.isArray()) {
                                    source.console().debug("Writing list {0}", Level.INFO, key);

                                    write.add("\t\t'" + key + "' -> {");
                                    KarmaArray array = value.getArray();

                                    array.forEach((element) -> {
                                        write.add("\t\t\t'" + element.getObjet().textValue() + "'");
                                    });
                                    write.add("\t\t}");
                                } else {
                                    if (value.isKeyArray()) {
                                        source.console().debug("Writing map {0}", Level.INFO, key);

                                        write.add("\t\t'" + key + "' -> {");
                                        KarmaKeyArray array = value.getKeyArray();

                                        array.getKeys().forEach((k) -> {
                                            KarmaElement val = array.get(k);
                                            if (array.isRecursive(k)) {
                                                write.add("\t\t\t'" + k + "' <-> " + val.toString());
                                            } else {
                                                write.add("\t\t\t'" + k + "' -> " + val.toString());
                                            }
                                        });
                                        write.add("\t\t}");
                                    } else {
                                        source.console().debug("Writing key {0} with value: {1}", Level.INFO, key, values.get(key));

                                        write.add("\t\t'" + key + "' -> " + values.get(key).toString());
                                    }
                                }
                            }
                        }
                    } else {
                        source.console().debug("Section: main", Level.INFO);

                        write.add("(\"main\"");
                        Map<String, KarmaElement> values = sections.getOrDefault(section, new LinkedHashMap<>());

                        for (String key : values.keySet()) {
                            write.add("");
                            KarmaElement value = values.get(key);

                            if (value.isArray()) {
                                source.console().debug("Writing list {0}", Level.INFO, key);

                                write.add("\t'" + key + "' -> {");
                                KarmaArray array = value.getArray();

                                array.forEach((element) -> {
                                    write.add("\t\t'" + element.getObjet().textValue() + "'");
                                });
                                write.add("\t}");
                            } else {
                                if (value.isKeyArray()) {
                                    source.console().debug("Writing map {0}", Level.INFO, key);

                                    write.add("\t'" + key + "' -> {");
                                    KarmaKeyArray array = value.getKeyArray();

                                    array.getKeys().forEach((k) -> {
                                        KarmaElement val = array.get(k);
                                        if (array.isRecursive(k)) {
                                            write.add("\t\t'" + k + "' <-> " + val.toString());
                                        } else {
                                            write.add("\t\t'" + k + "' -> " + val.toString());
                                        }
                                    });
                                    write.add("\t}");
                                } else {
                                    source.console().debug("Writing key {0} with value: {1}", Level.INFO, key, values.get(key));

                                    write.add("\t'" + key + "' -> " + values.get(key).toString());
                                }
                            }
                        }
                    }

                    if (sectionIndex != sections.size()) {
                        write.add("");
                    }
                }

                write.add(")");

                String firstLine = write.get(0);
                if (!firstLine.equals("(") && !firstLine.equals("(\"main\"")) {
                    write.add(0, "(\"main\"");
                }
            }

            PathUtilities.create(target);
            Files.write(target, StringUtils.listToString(write, false).getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Get the document
     *
     * @return the file document
     */
    public Path getDocument() {
        return document;
    }

    /**
     * Validate the file
     *
     * @throws IOException if something goes wrong
     */
    public void validate() throws IOException {
        if (internal != null) {
            if (!exists()) {
                create();
                Files.copy(internal, document, StandardCopyOption.REPLACE_EXISTING);
            }

            if (raw.isEmpty()) {
                Files.copy(internal, document, StandardCopyOption.REPLACE_EXISTING);
            } else {
                clearCache();
                preCache(); //We must update the cache

                KarmaMain tmp = new KarmaMain(internal);

                List<String> write = new ArrayList<>();
                write.add("(\"main\"");

                List<String> lines = PathUtilities.readAllLines(tmp.document);

                Pattern keyMatcher = Pattern.compile("'.*' .?->");
                Pattern badKeyMatcher = Pattern.compile("\".*\" .?->");

                StringBuilder section = new StringBuilder();

                int index = 1;
                boolean bigComment = false;
                boolean readingList = false;
                for (String line : lines) {
                    String noSpace = line.replaceAll("\\s", "");
                    if (noSpace.equals("\n") || StringUtils.isNullOrEmpty(noSpace)) {
                        write.add(line);
                        continue;
                    }

                    Matcher matcher = keyMatcher.matcher(line);
                    Matcher badMatcher = badKeyMatcher.matcher(line);

                    if (matcher.find() || badMatcher.find()) {
                        if (section.toString().isEmpty())
                            section.append("main");

                        int start;
                        int end;
                        try {
                            start = matcher.start();
                            end = matcher.end();
                        } catch (Throwable ex) {
                            start = badMatcher.start();
                            end = badMatcher.end();
                        }

                        String space = line.substring(0, start);
                        String result = line.substring(start, end);
                        boolean recursive = line.endsWith("<->");
                        String name = result.substring(1, result.length() - (recursive ? 5 : 4));

                        String key = section + "." + name;
                        String value = line.replaceFirst(line.substring(0, start) + result + " ", "");

                        KarmaElement element = content.getOrDefault(key, null);
                        if (element == null) {
                            element = tmp.get(key, null);
                        } else {
                            KarmaElement original = tmp.get(key, null);
                            if (original.isString() && !element.isString() ||
                                    original.isNumber() && !element.isNumber() ||
                                    original.isBoolean() && !element.isBoolean() ||
                                    original.isArray() && !element.isArray() ||
                                    original.isKeyArray() && !element.isKeyArray()) {
                                element = original;
                            }
                        }

                        if (element != null) {
                            if (element.isString() || element.isBoolean() || element.isNumber()) {
                                String val = element.getObjet().textValue();
                                if (val.equals("true") || val.equals("false")) {
                                    if (!element.isBoolean())
                                        element = new KarmaObject(Boolean.parseBoolean(val));
                                } else {
                                    Number num = parseNumber(val);
                                    if (num != null && !element.isNumber())
                                        element = new KarmaObject(num);
                                }

                                write.add(space + "'" + name + "' " + (recursive ? "<-> " : "-> ") + element);
                            }

                            if (!readingList) {
                                if (value.startsWith("{")) {
                                    readingList = true;
                                    write.add(space + "'" + name + "' " + (recursive ? "<->" : "->") + " {");

                                    if (element.isKeyArray()) {
                                        KarmaKeyArray kA = element.getKeyArray();
                                        kA.getKeys().forEach((k) -> {
                                            KarmaElement kAElement = kA.get(k);
                                            boolean rec = kA.isRecursive(k) || kA.isRecursive(kAElement);

                                            write.add(space + "\t'" + k + "' " + (rec ? "<-> " : "-> ") + kAElement);
                                        });
                                    } else {
                                        KarmaArray a = element.getArray();
                                        for (KarmaElement sub : a) {
                                            write.add(space + "\t'" + sub.getObjet().textValue() + "'");
                                        }
                                    }
                                    //write.add(line);
                                }
                            } else {
                                if (line.endsWith("}")) {
                                    readingList = false;
                                }
                                write.add(line);
                            }
                        }
                    } else {
                        if (readingList) {
                            if (line.endsWith("}")) {
                                readingList = false;
                                write.add(line);
                            }
                        } else {
                            Pattern sectMatcher = Pattern.compile("\\(\".*\"");

                            matcher = sectMatcher.matcher(line);

                            if (line.replaceAll("\\s", "").equals(")") && !section.toString().equals("main")) {
                                write.add(line);

                                String current = section.toString();
                                String[] data = current.split("\\.");
                                if (data.length >= 1)
                                    current = current.replace("." + data[data.length - 1], "");

                                section = new StringBuilder(current);
                                continue;
                            }

                            if (matcher.find()) {
                                int start = matcher.start();
                                int end = matcher.end();

                                if (line.contains("\"")) {
                                    String name = line.substring(start + 2, end - 1);

                                    if (section.length() > 0) {
                                        section.append(".").append(name);
                                    } else {
                                        section = new StringBuilder(name);
                                    }

                                    if (!name.equalsIgnoreCase("main")) {
                                        write.add(line);
                                    }
                                } else {
                                    if (!section.toString().equals("main")) {
                                        section = new StringBuilder("main");
                                    } else {
                                        throw new KarmaFormatException(document, "Error, couldn't save file because the main section has been defined two or more times", index);
                                    }
                                }
                            } else {
                                if (!bigComment) {
                                    Pattern comment = Pattern.compile("(\\*\\(.[^)*]*\\)\\*)|(\\*\\(\\n[^)*]*\\)\\*)|(\\*/.[^\\n]*)");
                                    matcher = comment.matcher(line);

                                    if (matcher.find()) {
                                        write.add(line);
                                    } else {
                                        bigComment = line.replaceAll("\\s", "").startsWith("*(");
                                        if (bigComment) {
                                            write.add(line);
                                        }
                                    }
                                } else {
                                    write.add(line);
                                    bigComment = !line.endsWith(")*");
                                }
                            }
                        }
                    }
                }
                write.add(")");

                PathUtilities.create(document);
                Files.write(document, StringUtils.listToString(write, false).getBytes(StandardCharsets.UTF_8));

                clearCache();
                preCache();
            }
        }
    }

    /**
     * Clear raw cache
     *
     * ONLY RECOMMENDED DOING WHEN RELOADING THE FILE
     */
    public void clearCache() {
        raw = "";

        content.clear();
        reverse.clear();
        indexes.clear();
    }

    /**
     * Delete the document
     */
    public void delete() {
        PathUtilities.destroy(document);
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return raw;
    }

    /**
     * Load data from legacy karma file
     *
     * @param source the source that is converting from legacy
     * @param kf the legacy karma file
     * @return the karma file
     * @throws IOException if something goes wrong
     */
    @SuppressWarnings("deprecation")
    public static KarmaMain fromLegacy(final KarmaSource source, final KarmaFile kf) throws IOException {
        Set<Key> keys = kf.getKeys(false);

        OptionsBuilder builder = RandomString.createBuilder()
                .withContent(TextContent.NUMBERS_AND_LETTERS)
                .withSize(16)
                .withType(TextType.RANDOM_SIZE);
        String random = StringUtils.generateString(builder).create();
        Path document = Files.createTempFile(random, "-kf");

        List<String> write = new ArrayList<>();
        List<String> wrote_keys = new ArrayList<>();

        write.add("(\"main\"");

        for (Key k : keys) {
            String path = k.getPath().replace(".", "_").toLowerCase();
            if (!wrote_keys.contains(path)) {
                Object value = k.getValue();

                wrote_keys.add(path);

                if (value instanceof Object[]) {
                    Object[] objects = (Object[]) value;
                    write.add("\t'" + path + "' -> {");
                    for (Object obj : objects) {
                        write.add("\t\t'" + obj + "'");
                    }
                    write.add("\t}");
                } else {
                    if (value instanceof Iterable<?>) {
                        Iterable<?> collection = (Iterable<?>) value;

                        write.add("\t'" + path + "' -> {");
                        collection.forEach((v) -> {
                            write.add("\t\t'" + v + "'");
                        });
                        write.add("\t}");
                    } else {
                        if (value instanceof String) {
                            write.add("\t'" + path + "' -> \"" + value + "\"");
                        } else {
                            write.add("\t'" + path + "' -> " + value);
                        }
                    }
                }
            }
        }

        write.add(")");
        Files.write(document, StringUtils.listToString(write, false).getBytes(StandardCharsets.UTF_8));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> PathUtilities.destroy(document)));

        return new KarmaMain(source, document);
    }

    /**
     * Load data from legacy async karma file
     *
     * @param source the source that is converting from legacy
     * @param kf the legacy karma file
     * @return the karma file
     */
    @SuppressWarnings("deprecation")
    public static LateScheduler<KarmaMain> fromLegacy(final KarmaSource source, final AsyncKarmaFile kf) {
        LateScheduler<KarmaMain> result = new AsyncLateScheduler<>();

        kf.getKeys(false).whenComplete((keys) -> {
            try {
                OptionsBuilder builder = RandomString.createBuilder()
                        .withContent(TextContent.NUMBERS_AND_LETTERS)
                        .withSize(16)
                        .withType(TextType.RANDOM_SIZE);
                String random = StringUtils.generateString(builder).create();
                Path document = Files.createTempFile(random, "-kf");

                List<String> write = new ArrayList<>();
                List<String> wrote_keys = new ArrayList<>();

                write.add("(\"main\"");

                for (Key k : keys) {
                    String path = k.getPath().replace(".", "_").toLowerCase();
                    if (!wrote_keys.contains(path)) {
                        Object value = k.getValue();

                        wrote_keys.add(path);

                        if (value instanceof Object[]) {
                            Object[] objects = (Object[]) value;
                            write.add("\t'" + path + "' -> {");
                            for (Object obj : objects) {
                                write.add("\t\t'" + obj + "'");
                            }
                            write.add("\t}");
                        } else {
                            if (value instanceof Iterable<?>) {
                                Iterable<?> collection = (Iterable<?>) value;

                                write.add("\t'" + path + "' -> {");
                                collection.forEach((v) -> {
                                    write.add("\t\t'" + v + "'");
                                });
                                write.add("\t}");
                            } else {
                                if (value instanceof String) {
                                    write.add("\t'" + path + "' -> \"" + value + "\"");
                                } else {
                                    write.add("\t'" + path + "' -> " + value);
                                }
                            }
                        }
                    }
                }

                write.add(")");
                Files.write(document, StringUtils.listToString(write, false).getBytes(StandardCharsets.UTF_8));

                Runtime.getRuntime().addShutdownHook(new Thread(() -> PathUtilities.destroy(document)));

                result.complete(new KarmaMain(source, document));
            } catch (Throwable ex) {
                result.complete(null, ex);
            }
        });

        return result;
    }

    /**
     * Migrate a legacy KarmaFile
     *
     * @param source the source that is performing the migration
     * @param legacy the legacy KarmaFile
     * @return the migrated file
     * @throws IOException if something goes wrong
     */
    @SuppressWarnings("deprecation")
    public static KarmaMain migrate(final KarmaSource source, final KarmaFile legacy) throws IOException {
        Path document = legacy.getFile().toPath();
        KarmaMain migrated = KarmaMain.fromLegacy(source, legacy);

        if (migrated.save(document)) {
            source.console().send("Migrated successfully from legacy karma file {0}", Level.OK, PathUtilities.getPrettyPath(document));
        } else {
            source.logger().scheduleLog(Level.WARNING, "Failed when migrating from legacy karma file {0} to modern karma main file", PathUtilities.getPrettyPath(document));
            source.console().send("Failed to migrate from legacy karma file {0}", Level.GRAVE, PathUtilities.getPrettyPath(document));
        }

        return migrated;
    }

    /**
     * Migrate a legacy KarmaFile asynchronous
     *
     * @param source the source that is performing the migration
     * @param legacy the legacy KarmaFile
     * @return the migrated file
     * @throws IOException if something goes wrong
     */
    @SuppressWarnings("deprecation")
    public static KarmaMain migrate(final KarmaSource source, final AsyncKarmaFile legacy) throws IOException {
        //We don't want async
        Path document = legacy.getFile().toPath();
        KarmaMain migrated = KarmaMain.fromLegacy(source, legacy.synchronize());

        if (migrated.save(document)) {
            source.console().send("Migrated successfully from legacy karma file {0}", Level.OK, PathUtilities.getPrettyPath(document));
        } else {
            source.logger().scheduleLog(Level.WARNING, "Failed when migrating from legacy karma file {0} to modern karma main file", PathUtilities.getPrettyPath(document));
            source.console().send("Failed to migrate from legacy karma file {0}", Level.GRAVE, PathUtilities.getPrettyPath(document));
        }

        return migrated;
    }

    /**
     * Parse a string into a number
     *
     * @param string the string
     * @return the number or null if not a number
     */
    private Number parseNumber(final String string) {
        Number number = null;
        try {
            number = Double.parseDouble(string);
        } catch (NumberFormatException db) {
            try {
                number = Float.parseFloat(string);
            } catch (NumberFormatException fl) {
                try {
                    number = Long.parseLong(string);
                } catch (NumberFormatException lo) {
                    try {
                        number = Short.parseShort(string);
                    } catch (NumberFormatException sh) {
                        try {
                            number = Byte.parseByte(string);
                        } catch (NumberFormatException by) {
                            try {
                                number = Integer.parseInt(string);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        }

        return number;
    }

    /**
     * Get KarmaAPI configuration
     *
     * @return the KarmaAPI configuration
     */
    public static KarmaMain getConfiguration() {
        KarmaMain main = new KarmaMain(APISource.getOriginal(false), "config.kf")
                .internal(KarmaMain.class.getResourceAsStream("/config.kf"));

        try {
            main.validate();
        } catch (Throwable ignored) {}

        return main;
    }
}
