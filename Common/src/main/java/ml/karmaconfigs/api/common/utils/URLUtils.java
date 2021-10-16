package ml.karmaconfigs.api.common.utils;

import org.jetbrains.annotations.Nullable;

import java.net.HttpURLConnection;
import java.net.URL;

public final class URLUtils {
    public static boolean exists(String url) {
        return (getResponseCode(url) == 200);
    }

    public static int getResponseCode(String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setInstanceFollowRedirects(false);
            con.setRequestMethod("HEAD");
            return con.getResponseCode();
        } catch (Throwable e) {
            return 503;
        }
    }

    @Nullable
    public static URL getOrNull(String url) {
        try {
            return new URL(url);
        } catch (Throwable ex) {
            return null;
        }
    }

    @Nullable
    public static URL getOrBackup(String def, String backup) {
        URL deffault = getOrNull(def);
        if (deffault == null)
            return getOrNull(backup);
        return deffault;
    }
}
