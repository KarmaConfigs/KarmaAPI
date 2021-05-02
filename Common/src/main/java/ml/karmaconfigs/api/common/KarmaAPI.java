package ml.karmaconfigs.api.common;

import java.io.InputStream;
import java.util.Properties;

public interface KarmaAPI {

    static String getVersion() {
        String version = "-1";

        try {
            InputStream in = KarmaAPI.class.getResourceAsStream("/api.properties");
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);

                version = properties.getProperty("version", "-1");
            }
        } catch (Throwable ignored) {}

        return version;
    }

    static String getCompilerVersion() {
        String version = "15";

        try {
            InputStream in = KarmaAPI.class.getResourceAsStream("/api.properties");
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);

                version = properties.getProperty("java_version", "15");
            }
        } catch (Throwable ignored) {}

        return version;
    }

    static String getBuildDate() {
        String compile_date = "01-01-1999 00:00:00";

        try {
            InputStream in = KarmaAPI.class.getResourceAsStream("/api.properties");
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);

                compile_date = properties.getProperty("compile_date", "01-01-1999 00:00:00");
            }
        } catch (Throwable ignored) {}

        return compile_date;
    }
}
