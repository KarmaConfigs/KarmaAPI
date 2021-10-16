package ml.karmaconfigs.api.common;

import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class ResourceDownloader {
    private final File destFile;

    private final String url;

    public ResourceDownloader(File destination, String _url) {
        this.destFile = destination;
        this.url = _url;
    }

    public static ResourceDownloader toCache(KarmaSource source, String fileName, String downloadURL, String... sub) {
        File target;
        if (sub.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String dir : sub)
                builder.append(File.separator).append(dir);
            target = new File(source.getDataPath() + File.separator + "cache" + builder, fileName);
        } else {
            target = new File(source.getDataPath() + File.separator + "cache", fileName);
        }
        if (FileUtilities.isValidFile(target))
            return new ResourceDownloader(target, downloadURL);
        throw new RuntimeException("Tried to download invalid resource file");
    }

    public void download() {
        ReadableByteChannel rbc = null;
        InputStream stream = null;
        FileOutputStream output = null;
        HttpURLConnection connection = null;
        try {
            FileUtilities.create(this.destFile);
            URL download_url = new URL(this.url);
            connection = (HttpURLConnection) download_url.openConnection();
            connection.connect();
            stream = connection.getInputStream();
            long destSize = this.destFile.length();
            long connSize = connection.getContentLengthLong();
            if (destSize != connSize) {
                APISource.getConsole().send("&b[ KarmaAPI ] &3Downloading file {0}", new Object[]{this.destFile.getName()});
                rbc = Channels.newChannel(download_url.openStream());
                output = new FileOutputStream(this.destFile);
                output.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
            }
            connection.disconnect();
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rbc != null)
                    rbc.close();
                if (stream != null)
                    stream.close();
                if (output != null)
                    output.close();
                if (connection != null)
                    connection.disconnect();
            } catch (Throwable throwable) {
            }
            APISource.getConsole().send("&b[ KarmaAPI ] &3Downloaded file {0}", new Object[]{this.destFile.getName()});
        }
    }

    public File getDestFile() {
        return this.destFile;
    }
}
