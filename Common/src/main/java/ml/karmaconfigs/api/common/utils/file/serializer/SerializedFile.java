package ml.karmaconfigs.api.common.utils.file.serializer;

import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;

/**
 * Serialized file
 */
public final class SerializedFile {

    private final boolean directory;
    private final String name;
    private final byte[] data;

    /**
     * Initialize the serialized file
     *
     * @param dir the directory
     * @param n the file name
     * @param content the file contents
     */
    public SerializedFile(final boolean dir, final String n, final byte[] content) {
        directory = dir;
        name = n;
        data = content;
    }


    /**
     * Get if the file is a directory
     *
     * @return the file
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * Get the file name
     *
     * @return the file name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the file data
     *
     * @return the file data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Re-write the data into the file
     *
     * @param destination the destination folder
     */
    public void restore(final File destination) {
        if (!directory) {
            try {
                File tmp;
                if (name.contains("/")) {
                    StringBuilder builder = new StringBuilder();
                    String[] data = name.split("/");
                    for (String sub : data)
                        builder.append(sub).append("/");

                    String sub = builder.toString();
                    tmp = new File(destination, sub.substring(0, sub.length() - 1));
                } else {
                    tmp = new File(destination, name);
                }

                FileUtilities.create(tmp);

                FileOutputStream stream = new FileOutputStream(tmp);
                stream.write(data);
                stream.flush();

                stream.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Re-write the data into the file
     *
     * @param destination the destination folder
     */
    public void restore(final Path destination) {
        if (!directory) {
            try {
                Path tmp = destination;
                if (name.contains("/")) {
                    String[] data = name.split("/");
                    for (String sub : data)
                        tmp = tmp.resolve(sub);
                } else {
                    tmp = tmp.resolve(name);
                }

                PathUtilities.create(tmp);

                FileOutputStream stream = new FileOutputStream(tmp.toFile());
                stream.write(data);
                stream.flush();

                stream.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }
}
