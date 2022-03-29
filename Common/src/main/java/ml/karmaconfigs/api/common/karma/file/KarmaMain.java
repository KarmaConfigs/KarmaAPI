package ml.karmaconfigs.api.common.karma.file;

import ml.karmaconfigs.api.common.karma.file.element.KarmaArray;
import ml.karmaconfigs.api.common.karma.file.element.KarmaElement;
import ml.karmaconfigs.api.common.karma.file.element.KarmaKeyArray;
import ml.karmaconfigs.api.common.karma.file.element.KarmaObject;
import ml.karmaconfigs.api.common.karma.file.error.KarmaFormatException;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import ml.karmaconfigs.api.common.utils.string.OptionsBuilder;
import ml.karmaconfigs.api.common.utils.string.RandomString;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.string.util.TextContent;
import ml.karmaconfigs.api.common.utils.string.util.TextType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The KarmaFile, that contains all the file data
 *
 * @author KarmaDev
 * @since 1.3.2-SNAPSHOT
 */
public final class KarmaMain {

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
    }

    /**
     * Initialize the file
     *
     * @param doc the file that must be read
     */
    public KarmaMain(final Path doc) {
        document = doc;
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
                } catch (Throwable ignored) {
                }
            }

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
                                        char cont = line.charAt((nextIndex + 1 != size ? (nextIndex + 1) : nextIndex));
                                        char cont1 = line.charAt((nextIndex + 2 != size ? (nextIndex + 2) : nextIndex));

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
                            Matcher matcher = pattern.matcher(line);
                            if (!matcher.find()) {
                                throw new KarmaFormatException(document, "Error, couldn't find valid key format -> or <-> at ( " + line + " )", indexes.getOrDefault(line, -1));
                            }
                            int start = matcher.start();
                            int end = matcher.end();
                            String match = line.substring(start + 2, end - 2).replaceAll("\\s", "");
                            if (match.equalsIgnoreCase("<-")) {
                                rec = true;
                            }

                            String[] dt = line.split((rec ? "<->" : "->"));

                            String tmpName = dt[0].replaceAll("\\s", "");
                            if (!tmpName.startsWith("'") && !tmpName.endsWith("'"))
                                throw new KarmaFormatException(document, "Error, invalid key format, it must be 'x' where x is any value", indexes.getOrDefault(line, -1));

                            String name = StringUtils.replaceLast(dt[0].replaceFirst("'", ""), "'", "");
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

                                                    name = StringUtils.replaceLast(StringUtils.replaceLast(dt[0].replaceFirst("'", ""), "'", ""), "<", "");
                                                    key = name.replaceAll("\\s", "");
                                                    value = new StringBuilder();
                                                    for (int y = 1; y < dt.length; y++) {
                                                        value.append(dt[y]).append((y != dt.length - 1 ? "->" : ""));
                                                    }

                                                    KarmaKeyArray ka = (KarmaKeyArray) array;

                                                    String v = value.toString().replaceFirst(" ", "");
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
                                                            if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {
                                                                boolean bool = Boolean.parseBoolean(v);
                                                                KarmaObject obj = new KarmaObject(bool);

                                                                ka.add(key, obj, true);
                                                            } else {
                                                                if (v.contains(",")) {
                                                                    Number number = Double.parseDouble(v.replace(",", "."));
                                                                    KarmaObject obj = new KarmaObject(number);

                                                                    ka.add(key, obj, true);
                                                                } else {
                                                                    if (v.contains(".")) {
                                                                        Number number = Float.parseFloat(v);
                                                                        KarmaObject obj = new KarmaObject(number);

                                                                        ka.add(key, obj, true);
                                                                    } else {
                                                                        Number number = Integer.parseInt(v);
                                                                        KarmaObject obj = new KarmaObject(number);

                                                                        ka.add(key, obj, true);
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

                                                        tmpName = dt[0].replaceAll("\\s", "");
                                                        if (!tmpName.startsWith("'") && !tmpName.endsWith("'"))
                                                            throw new KarmaFormatException(document, "Error, invalid key format, it must be 'x' where x is any value", indexes.getOrDefault(line, -1));

                                                        name = StringUtils.replaceLast(StringUtils.replaceLast(dt[0].replaceFirst("'", ""), "'", ""), "<", "");
                                                        key = name.replaceAll("\\s", "");
                                                        value = new StringBuilder();
                                                        for (int y = 1; y < dt.length; y++) {
                                                            value.append(dt[y]).append((y != dt.length - 1 ? "->" : ""));
                                                        }

                                                        KarmaKeyArray ka = (KarmaKeyArray) array;

                                                        String v = value.toString().replaceFirst(" ", "");
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
                                                                if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {
                                                                    boolean bool = Boolean.parseBoolean(v);
                                                                    KarmaObject obj = new KarmaObject(bool);

                                                                    ka.add(key, obj, false);
                                                                } else {
                                                                    if (v.contains(",")) {
                                                                        Number number = Double.parseDouble(v.replace(",", "."));
                                                                        KarmaObject obj = new KarmaObject(number);

                                                                        ka.add(key, obj, false);
                                                                    } else {
                                                                        if (v.contains(".")) {
                                                                            Number number = Float.parseFloat(v);
                                                                            KarmaObject obj = new KarmaObject(number);

                                                                            ka.add(key, obj, false);
                                                                        } else {
                                                                            Number number = Integer.parseInt(v);
                                                                            KarmaObject obj = new KarmaObject(number);

                                                                            ka.add(key, obj, false);
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

                                                        String v = value.toString();
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
                                                                    if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {
                                                                        boolean bool = Boolean.parseBoolean(v);
                                                                        KarmaObject obj = new KarmaObject(bool);

                                                                        ka.add(obj);
                                                                    } else {
                                                                        if (v.contains(",")) {
                                                                            Number number = Double.parseDouble(v.replace(",", "."));
                                                                            KarmaObject obj = new KarmaObject(number);

                                                                            ka.add(obj);
                                                                        } else {
                                                                            if (v.contains(".")) {
                                                                                Number number = Float.parseFloat(v);
                                                                                KarmaObject obj = new KarmaObject(number);

                                                                                ka.add(obj);
                                                                            } else {
                                                                                Number number = Integer.parseInt(v);
                                                                                KarmaObject obj = new KarmaObject(number);

                                                                                ka.add(obj);
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
                                    String v = value.toString().replaceFirst(" ", "");
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
                                        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {
                                            boolean bool = Boolean.parseBoolean(v);
                                            KarmaObject obj = new KarmaObject(bool);

                                            content.put(key, obj);
                                            if (rec)
                                                reverse.put(obj, key);
                                        } else {
                                            if (v.contains(",")) {
                                                Number number = Double.parseDouble(v.replace(",", "."));
                                                KarmaObject obj = new KarmaObject(number);

                                                content.put(key, obj);
                                                if (rec)
                                                    reverse.put(obj, key);
                                            } else {
                                                if (v.contains(".")) {
                                                    Number number = Float.parseFloat(v);
                                                    KarmaObject obj = new KarmaObject(number);

                                                    content.put(key, obj);
                                                    if (rec)
                                                        reverse.put(obj, key);
                                                } else {
                                                    Number number = Integer.parseInt(v);
                                                    KarmaObject obj = new KarmaObject(number);

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

        content.put(tmpKey, element);
    }

    /**
     * Save the file
     *
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public boolean save() throws KarmaFormatException {
        try {
            List<String> updated = new ArrayList<>();

            BufferedReader reader = Files.newBufferedReader(document, StandardCharsets.UTF_8);
            BufferedWriter writer = Files.newBufferedWriter(document, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            String line;
            StringBuilder section = new StringBuilder();

            boolean closed = false;
            int index = 1;
            int last_section = 0;
            while ((line = reader.readLine()) != null) {
                if (!closed) {
                    Pattern keyMatcher = Pattern.compile("'.*' .?->");
                    Matcher matcher = keyMatcher.matcher(line);

                    if (matcher.find()) {
                        if (section.toString().startsWith("main")) {
                            int start = matcher.start();
                            int end = matcher.end();

                            String space = line.substring(0, end);
                            String result = line.substring(start, end);
                            boolean recursive = line.endsWith("<->");
                            String name = result.substring(1, result.length() - (recursive ? 5 : 4));

                            String key = section + "." + name;
                            String value = line.replaceFirst(line.substring(0, start) + result + " ", "");
                            KarmaElement element = content.getOrDefault(key, null);
                            if (element != null) {
                                if (element.isString()) {
                                    writer.write(space + "'" + name + "' " + (recursive ? "<-> " : "-> ") + "\"" + element.getObjet().getString() + "\"");
                                } else {
                                    if (element.isBoolean()) {
                                        writer.write(space + "'" + name + "' " + (recursive ? "<-> " : "-> ") + element.getObjet().getBoolean());
                                    } else {
                                        if (element.isNumber()) {
                                            Number number = element.getObjet().getNumber();

                                            writer.write(space + "'" + name + "' " + (recursive ? "<-> " : "-> ") + number);
                                        }
                                    }
                                }
                                if (value.startsWith("'")) {
                                    if (value.endsWith("'")) {
                                        value = value.substring(1, value.length() - 1);
                                    } else {
                                        throw new KarmaFormatException(document, "Error, couldn't save file because a string value is not correctly defined", index);
                                    }

                                    writer.write(space + "'" + name + "' " + (recursive ? "<-> " : "-> ") + value);
                                } else {
                                    if (value.startsWith("\"")) {
                                        if (value.endsWith("\"")) {
                                            value = value.substring(1, value.length() - 1);
                                        } else {
                                            throw new KarmaFormatException(document, "Error, couldn't save file because a string value is not correctly defined", index);
                                        }
                                    } else {
                                        if (value.startsWith("{")) {

                                        }
                                    }
                                }
                            }
                        } else {
                            throw new KarmaFormatException(document, "Error, couldn't save file because the main section is not defined", index);
                        }
                    } else {
                        Pattern sectMatcher = Pattern.compile("(^\\*\\(\".*\")|^\\*(\\()");
                        matcher = sectMatcher.matcher(line);

                        if (matcher.find()) {
                            int start = matcher.start();
                            int end = matcher.end();

                            if (line.contains("\"")) {
                                if (section.length() > 0) {
                                    section.append(".").append(line, start + 2, end - 1);
                                } else {
                                    section = new StringBuilder(line.substring(start + 2, end));
                                }
                                last_section = index;
                            } else {
                                if (!section.toString().equals("main")) {
                                    section = new StringBuilder("main");
                                } else {
                                    throw new KarmaFormatException(document, "Error, couldn't save file because the main section has been defined two or more times", index);
                                }
                            }
                        } else {
                            Pattern comment = Pattern.compile("(\\*\\(.[^)*]*\\)\\*)|(\\*\\(\\n[^)*]*\\)\\*)|(\\*/.[^\\n]*)");
                            matcher = comment.matcher(line);

                            if (matcher.find()) {
                                String tmp = matcher.replaceAll("");
                                if (tmp.equals(")")) {
                                    if (section.toString().contains("\\.")) {
                                        String[] data = section.toString().split("\\.");
                                        String last = data[data.length - 1];

                                        section = new StringBuilder(StringUtils.replaceLast(section.toString(), "." + last, ""));
                                    } else {
                                        closed = true;
                                    }
                                }
                            }

                            updated.add(line);
                        }
                    }
                } else {
                    //Yes, I don't want extra data in the file, it could cause errors. PLEASE RESPECT THAT
                    if (!line.replaceAll("\\s", "").isEmpty())
                        throw new KarmaFormatException(document, "Error, couldn't save file because expected document end but text found", index);
                }

                index++;
            }

            if (section.toString().equals("main")) {

            } else {
                throw new KarmaFormatException(document, "Error, couldn't save file because one or more sections didn't close properly", last_section);
            }

            return true;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Save the file
     *
     * @param name the target file name
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public boolean save(final String name) throws KarmaFormatException {
        return true;
    }

    /**
     * Save the file
     *
     * @param target the target file
     * @throws KarmaFormatException if the file could not be parsed correctly
     */
    public boolean save(final Path target) throws KarmaFormatException {
        return true;
    }

    public void wad() {
        try {
            List<String> lines = PathUtilities.readAllLines(document);
            List<String> updated = new ArrayList<>();

            boolean waiting_close = false;
            String waiting = null;
            for (String key : content.keySet()) {
                for (String str : lines) {
                    if (!waiting_close) {
                        String original = key;
                        String[] data = key.split("\\.");
                        key = data[data.length - 1];

                        if (str.contains("'" + key + "'")) {
                            KarmaElement element = content.getOrDefault(original, null);
                            if (element != null) {
                                Pattern pattern = Pattern.compile("'.*' .?-> .");
                                Matcher match = pattern.matcher(str);
                                if (match.find()) {
                                    int end = match.end(); //We only care about this one...
                                    char character = str.charAt(end);

                                    //Key still exists, otherwise has been removed and won't be stored
                                    if (element instanceof KarmaObject) {
                                        KarmaObject object = (KarmaObject) element;

                                        if (character == '{') {
                                            //It was a list, we must "remove" the list first...
                                            waiting_close = true;

                                            if (element.isString()) {
                                                waiting = str.substring(0, end - 1) + "'" + object.getString() + "'";
                                            } else {
                                                waiting = str.substring(0, end - 1) + (object.isNumber() ? object.getNumber() : object.getBoolean());
                                            }
                                        } else {
                                            if (element.isString()) {
                                                updated.add(str.substring(0, end - 1) + "'" + object.getString() + "'");
                                            } else {
                                                updated.add(str.substring(0, end - 1) + (object.isNumber() ? object.getNumber() : object.getBoolean()));
                                            }
                                        }
                                    } else {
                                        StringBuilder waitBuilder = new StringBuilder(str.substring(0, end - 1) + " {\n");

                                        if (element instanceof KarmaArray) {
                                            KarmaArray array = (KarmaArray) element;
                                            array.forEach((sub) -> {
                                                KarmaObject object = sub.getObjet();
                                                if (object.isString()) {
                                                    waitBuilder.append("'").append(object.getString()).append("'").append("\n");
                                                } else {
                                                    waitBuilder.append((object.isNumber() ? object.getNumber() : object.getBoolean())).append("\n");
                                                }
                                            });
                                        } else {
                                            KarmaKeyArray array = (KarmaKeyArray) element;
                                            array.getKeys().forEach((sub) -> {
                                                KarmaObject object = array.get(sub).getObjet();
                                                if (object.isString()) {
                                                    waitBuilder.append("'").append(sub).append("'").append(" ").append((array.isRecursive(sub) ? "<->" : "->")).append(" ").append("'").append(object.getString()).append("'").append("\n");
                                                } else {
                                                    waitBuilder.append("'").append(sub).append("'").append(" ").append((array.isRecursive(sub) ? "<->" : "->")).append(" ").append((object.isNumber() ? object.getNumber() : object.getBoolean())).append("\n");
                                                }
                                            });
                                        }
                                        waiting = waitBuilder.toString();

                                        if (character == '{') {
                                            waiting_close = true;
                                        } else {
                                            updated.add(waiting);
                                            waiting = null;
                                        }
                                    }
                                }
                            }
                        } else {
                            updated.add(str);
                        }
                    } else {
                        Pattern pattern = Pattern.compile("(\\*\\(.[^)*]*\\)\\*)|(\\*\\(\\n[^)*]*\\)\\*)|(\\*/.[^\\n]*)");
                        Matcher matcher = pattern.matcher(str);
                        if (matcher.find()) {
                            str = matcher.replaceAll("");
                        }

                        if (str.replaceAll("\\s", "").startsWith("}") || str.replaceAll("\\s", "").endsWith("}")) {
                            waiting_close = false;
                            updated.add(waiting);
                            waiting = null;
                        }
                    }
                }
            }

            Files.write(document, StringUtils.listToString(updated, false).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
