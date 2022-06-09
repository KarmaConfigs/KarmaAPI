package ml.karmaconfigs.api.common.utils.file.serializer;

import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.scheduler.LateScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.worker.AsyncLateScheduler;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.file.PathFilter;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A tool to store a complete file in
 * a simple string.
 */
public final class FileSerializer implements Serializable {

    /**
     * If the file is a directory, this will contain all the
     * directory files recursively
     */
    private final transient Set<FileSerializer> subFiles = new LinkedHashSet<>();

    /**
     * This defines if the file is a directory
     */
    private final boolean directory;
    /**
     * This contains the file data
     */
    private final byte[] data;

    /**
     * The file
     */
    private final transient Path file;

    /**
     * The file name with extension
     */
    private String name;

    /**
     * Initialize a file serializer from a serialized file
     * serializer
     *
     * @param start the start source
     * @param main the main file serializer
     */
    FileSerializer(final File start, final FileSerializer main) {
        file = start.toPath().resolve(main.name);

        directory = main.directory;
        name = main.name;
        data = main.data;

        if (!Files.isDirectory(file))
            PathUtilities.create(file);
    }

    /**
     * Initialize a file serializer from a serialized file
     * serializer
     *
     * @param start the start source
     * @param main the main file serializer
     */
    FileSerializer(final Path start, final FileSerializer main) {
        file = start.resolve(main.name);
        directory = main.directory;
        name = main.name;
        data = main.data;

        if (!Files.isDirectory(file))
            PathUtilities.create(file);
    }

    /**
     * Initialize the file serializer
     *
     * @param target the target file
     * @throws IllegalStateException if the file is a compressed
     * file
     */
    public FileSerializer(final File target) throws IllegalStateException {
        if (FileUtilities.isCompressedFile(target)) {
            throw new IllegalStateException("FileSerializer cannot serialize compressed files. Please provide a folder or a file ( " + FileUtilities.getFileCompression(target) + " )");
        } else {
            if (FileUtilities.isValidFile(target) && target.exists()) {
                file = target.toPath();
                directory = target.isDirectory();
                name = FileUtilities.getName(target, true);
                if (!directory) {
                    data = FileUtilities.readFile(target);
                } else {
                    data = new byte[0];
                }
            } else {
                throw new IllegalStateException("Invalid or non existent file given. Cannot continue");
            }
        }
    }

    /**
     * Initialize the file serializer
     *
     * @param target the target file
     * @throws IllegalStateException if the file is a compressed
     * file
     */
    public FileSerializer(final Path target) throws IllegalStateException {
        if (PathUtilities.isCompressedFile(target)) {
            throw new IllegalStateException("FileSerializer cannot serialize compressed files. Please provide a folder or a file ( " + PathUtilities.getPathCompression(target) + " )");
        } else {
            if (PathUtilities.isValidPath(target) && Files.exists(target)) {
                file = target;
                directory = Files.isDirectory(target);
                name = PathUtilities.getName(file, true);
                if (!directory) {
                    data = PathUtilities.readPath(file);
                } else {
                    data = new byte[0];
                }
            } else {
                throw new IllegalStateException("Invalid or non existent file given. Cannot continue");
            }
        }
    }

    /**
     * Set the serializer relative path
     *
     * @param relative the relative path
     * @return this instance
     */
    public FileSerializer withPath(final String relative) {
        name = relative + (relative.endsWith(File.separator) ? name : File.separator + name);
        return this;
    }

    /**
     * Parse the file and sub files
     *
     * @param filter the parse filter
     *
     * @return a late scheduler for when
     * the action will be complete
     */
    public LateScheduler<Void> parse(final @Nullable PathFilter filter) {
        LateScheduler<Void> result = new AsyncLateScheduler<>();

        KarmaSource tmp = KarmaAPI.source(false);
        tmp.async().queue("async_file_serialize", () -> {
            if (directory) {
                processFile(file, filter, name);
            } else {
                String path = PathUtilities.getParentPath(file);
                String name = PathUtilities.getName(file, false);
                String extension = PathUtilities.getExtension(file);

                if (filter == null || filter.accept(path, name, extension)) {
                    FileSerializer serializer = new FileSerializer(file);
                    subFiles.add(serializer);
                }
            }

            result.complete(null, null);
        });

        return result;
    }

    /**
     * Serialize the file
     *
     * @return the serialized file
     */
    public String serialize() {
        StringBuilder builder = new StringBuilder();

        if (!subFiles.isEmpty()) {

            subFiles.forEach((sub) -> builder.append(StringUtils.serialize(sub)).append(";"));
        } else {
            builder.append(StringUtils.serialize(this));
        }

        String result = builder.toString();
        if (result.endsWith(";"))
            result = StringUtils.replaceLast(result, ";", "");

        return Base64.getEncoder().encodeToString(result.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Get the serialized file from the file
     * serializer
     *
     * @return the serialized file
     */
    public SerializedFile getFile() {
        return new SerializedFile(directory, name, data);
    }

    /**
     * Get the serialized files from the file
     * serializer
     *
     * @return the serialized file
     */
    public Set<SerializedFile> getFiles() {
        Set<SerializedFile> files = new HashSet<>();

        if (!directory) {
            SerializedFile local = new SerializedFile(false, name, data);
            files.add(local);
        }

        subFiles.forEach((sub) -> files.add(sub.getFile()));

        return files;
    }

    /**
     * Process all the files and sub files
     *
     * @param source the start directory
     * @param filter the filter
     */
    private void processFile(final Path source, final @Nullable PathFilter filter, final String dir) {
        try {
            if (Files.isDirectory(source)) {
                Files.list(source).forEachOrdered((file) -> {
                    if (Files.isDirectory(file)) {
                        String subDir = dir + File.separator + PathUtilities.getName(file, true);
                        processFile(file, filter, subDir);
                    } else {
                        String path = PathUtilities.getParentPath(file);
                        String name = PathUtilities.getName(file, false);
                        String extension = PathUtilities.getExtension(file);

                        if (filter == null || filter.accept(path, name, extension)) {
                            //System.out.println(file);
                            FileSerializer serializer = new FileSerializer(file).withPath(dir);
                            subFiles.add(serializer);

                            if (serializer.name.equalsIgnoreCase(name + File.separator) || serializer.name.equalsIgnoreCase(name))
                                subFiles.remove(serializer);
                        }
                    }
                });
            } else {
                String path = PathUtilities.getParentPath(source);
                String name = PathUtilities.getName(source, false);
                String extension = PathUtilities.getExtension(source);

                if (filter == null || filter.accept(path, name, extension)) {
                    FileSerializer serializer = new FileSerializer(file).withPath(dir);
                    subFiles.add(serializer);

                    if (serializer.name.equalsIgnoreCase(name + File.separator) || serializer.name.equalsIgnoreCase(name))
                        subFiles.remove(serializer);
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
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
        return getClass().getName() + "@" + Integer.toHexString(hashCode()) + "[isDirectory:" + directory + ";relativePath:" + name + ";content:" + Integer.toHexString(data.length) + "]";
    }

    /**
     * Load a file from a serialized string
     *
     * @param startSource the target folder where
     *                    to store serialized data
     * @param serialized the serialized string
     * @return the path
     */
    public static FileSerializer load(final File startSource, final String serialized) {
        FileUtilities.destroy(startSource);
        FileUtilities.createDirectory(startSource);

        String decoded = new String(Base64.getDecoder().decode(serialized.getBytes()), StandardCharsets.UTF_8);
        FileSerializer serializer = null;
        if (decoded.contains(";")) {
            String[] data = decoded.split(";");
            for (int i = 0; i < data.length; i++) {
                FileSerializer tmp = StringUtils.loadUnsafe(data[i]);
                if (tmp != null) {
                    if (i == 0) {
                        //Main serializer
                        serializer = new FileSerializer(startSource, tmp);
                    } else {
                        serializer.subFiles.add(tmp);
                    }
                } else {
                    break;
                }
            }
        } else {
            FileSerializer tmp = StringUtils.loadUnsafe(decoded);
            if (tmp != null) {
                serializer = new FileSerializer(startSource, tmp);
            }
        }

        return serializer;
    }

    /**
     * Load a file from a serialized string
     *
     * @param startSource the target folder where
     *                    to store serialized data
     * @param serialized the serialized string
     * @return the path
     */
    @Nullable
    public static FileSerializer load(final Path startSource, final String serialized) {
        PathUtilities.destroy(startSource);
        PathUtilities.createDirectory(startSource);

        String decoded = new String(Base64.getDecoder().decode(serialized.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        FileSerializer serializer = null;
        if (decoded.contains(";")) {
            String[] data = decoded.split(";");
            for (int i = 0; i < data.length; i++) {
                FileSerializer tmp = StringUtils.loadUnsafe(data[i]);
                if (tmp != null) {
                    if (i == 0) {
                        //Main serializer
                        serializer = new FileSerializer(startSource, tmp);
                    } else {
                        serializer.subFiles.add(tmp);
                    }
                } else {
                    break;
                }
            }
        } else {
            FileSerializer tmp = StringUtils.loadUnsafe(decoded);
            if (tmp != null) {
                serializer = new FileSerializer(startSource, tmp);
            }
        }

        return serializer;
    }
}
