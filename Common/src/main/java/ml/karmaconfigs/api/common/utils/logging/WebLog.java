package ml.karmaconfigs.api.common.utils.logging;

import com.google.gson.*;
import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaConfig;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.KarmaLogger;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.enums.LogExtension;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.url.HttpUtil;
import ml.karmaconfigs.api.common.utils.url.Post;
import ml.karmaconfigs.api.common.utils.url.URLUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class WebLog {

    private final static Map<KarmaSource, Instant> last_log_request = new ConcurrentHashMap<>();

    private final static String mclogs = "https://api.mclo.gs/1/log";
    private final static String paste_ee = "https://api.paste.ee/v1/pastes";

    private final KarmaSource source;

    /**
     * Initialize the web logging
     *
     * @param src the source that is uploading
     *               the log
     */
    public WebLog(final KarmaSource src) {
        source = src;
    }

    /**
     * Upload the current log
     *
     * @param target the target were to upload logs
     * @param extra extra POST data. Useful if the WebTarget requires
     *              authentication
     * @param headers the request headers
     * @throws UploadOverflowException if the source is trying to upload logs too fast
     */
    public void upload(final WebTarget target, final Post extra, final Header... headers) throws UploadOverflowException {
        Instant last_upload = last_log_request.getOrDefault(source, null);
        if (last_upload != null) {
            Instant now = Instant.now();

            long seconds = last_upload.getEpochSecond() - now.getEpochSecond();
            if (seconds < 300)
                throw new UploadOverflowException(source, (int) (300 - seconds));
        }

        URL pretty = null;
        URL raw = null;

        boolean success = false;

        URL apiURL;
        switch (target) {
            case MCLO_GS:
                apiURL = URLUtils.getOrNull(mclogs);
                if (apiURL != null) {
                    HttpUtil util = URLUtils.extraUtils(apiURL);

                    if (util != null) {
                        KarmaLogger logger = source.logger();
                        try {
                            Method getLoggerFile = KarmaLogger.class.getDeclaredMethod("getLoggerFile", LogExtension.class);
                            getLoggerFile.setAccessible(true);
                            Path file = (Path) getLoggerFile.invoke(logger, LogExtension.MARKDOWN);
                            getLoggerFile.setAccessible(false);

                            List<String> lines = PathUtilities.readAllLines(file);
                            String singleton = StringUtils.listToString(lines, false).replace("<br>", "");

                            Post post = Post.newPost()
                                    .add("content", singleton);
                            extra.getData().forEach((k) -> post.add(k, extra.get(k)));

                            String response = util.getResponse(post, headers);
                            if (!StringUtils.isNullOrEmpty(response)) {
                                Gson gson = new GsonBuilder().setLenient().create();
                                JsonObject json = gson.fromJson(response, JsonObject.class);

                                if (json.has("success")) {
                                    JsonElement isSuccess = json.get("success");
                                    if (isSuccess.isJsonPrimitive() && isSuccess.getAsJsonPrimitive().isBoolean()) {
                                        success = isSuccess.getAsBoolean();

                                        if (success) {
                                            if (json.has("url")) {
                                                JsonElement logURL = json.get("url");
                                                if (logURL.isJsonPrimitive() && logURL.getAsJsonPrimitive().isString()) {
                                                    pretty = URLUtils.getOrNull(logURL.getAsString());
                                                }
                                            }
                                            if (json.has("raw")) {
                                                JsonElement logURL = json.get("raw");
                                                if (logURL.isJsonPrimitive() && logURL.getAsJsonPrimitive().isString()) {
                                                    raw = URLUtils.getOrNull(logURL.getAsString());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            break;
                        }
                    }
                }
                break;
            case PASTE_EE:
                apiURL = URLUtils.getOrNull(paste_ee);
                if (apiURL != null) {
                    HttpUtil util = URLUtils.extraUtils(apiURL);

                    if (util != null) {
                        KarmaLogger logger = source.logger();
                        try {
                            Method getLoggerFile = KarmaLogger.class.getDeclaredMethod("getLoggerFile", LogExtension.class);
                            getLoggerFile.setAccessible(true);
                            Path file = (Path) getLoggerFile.invoke(logger, LogExtension.MARKDOWN);
                            getLoggerFile.setAccessible(false);

                            List<String> lines = PathUtilities.readAllLines(file);
                            String singleton = StringUtils.listToString(lines, false).replace("<br>", "").replace("\n", "\r");

                            JsonObject data = new JsonObject();
                            JsonArray array = new JsonArray();

                            JsonObject section = new JsonObject();
                            section.addProperty("name", source.name());
                            section.addProperty("syntax", "markdown");
                            section.addProperty("contents", singleton);
                            array.add(section);

                            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

                            data.add("sections", array);
                            data.addProperty("description", "Auto uploaded log by KarmaAPI [" + date + "]");

                            extra.setJson(data);

                            List<Header> reOrganization = new ArrayList<>(Arrays.asList(headers));
                            reOrganization.add(new BasicHeader("Content-Type", "application/json"));
                            boolean needKey = true;
                            for (Header h : reOrganization)
                                if (h.getName().equalsIgnoreCase("X-Auth-Token")) {
                                    needKey = false;
                                }

                            if (needKey) {
                                KarmaConfig config = new KarmaConfig();
                                reOrganization.add(new BasicHeader("X-Auth-Token", config.getAccessKey(target)));
                            }

                            String response = util.getResponse(extra, reOrganization.toArray(new Header[0]));
                            if (!StringUtils.isNullOrEmpty(response)) {
                                Gson gson = new GsonBuilder().setLenient().create();
                                JsonObject json = gson.fromJson(response, JsonObject.class);

                                if (json.has("success")) {
                                    JsonElement isSuccess = json.get("success");
                                    if (isSuccess.isJsonPrimitive() && isSuccess.getAsJsonPrimitive().isBoolean()) {
                                        success = isSuccess.getAsBoolean();

                                        if (success) {
                                            if (json.has("link")) {
                                                JsonElement logURL = json.get("link");
                                                if (logURL.isJsonPrimitive() && logURL.getAsJsonPrimitive().isString()) {
                                                    pretty = URLUtils.getOrNull(logURL.getAsString());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }

        if (pretty == null && raw == null)
            success = false;

        if (success) {
            APISource.getOriginal(false).console().send("Successfully uploaded log for source {0}.", Level.OK, source.name());
            if (pretty != null) {
                APISource.getOriginal(false).console().send("Log URL: {0}", Level.INFO, pretty);
            }
            if (raw != null) {
                APISource.getOriginal(false).console().send("Raw URL: {0}", Level.INFO, raw);
            }

            last_log_request.put(source, Instant.now());
        } else {
            APISource.getOriginal(false).console().send("Something went wrong while uploading log for source {0}.", Level.GRAVE, source.name());
        }
    }
}
