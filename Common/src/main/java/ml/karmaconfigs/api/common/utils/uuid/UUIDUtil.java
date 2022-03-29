package ml.karmaconfigs.api.common.utils.uuid;

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

import com.google.gson.*;
import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.timer.scheduler.LateScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.worker.AsyncLateScheduler;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.url.HttpUtil;
import ml.karmaconfigs.api.common.utils.url.URLUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Karma UUID fetcher
 */
public final class UUIDUtil {

    static {
        KarmaAPI.install();
    }

    /**
     * Register a minecraft client into the karma UUID
     * engine database API
     *
     * @param name the client name
     */
    public static void registerMinecraftClient(final String name) {
        URL first = URLUtils.getOrNull("https://karmadev.es/?nick=" + name);
        URL second = URLUtils.getOrNull("https://karmarepo.000webhostapp.com/api/?nick=" + name);
        URL third = URLUtils.getOrNull("https://karmaconfigs.ml/api/?nick=" + name);
        URL fourth = URLUtils.getOrNull("https://karmarepo.ml/api/?nick=" + name);

        if (first != null) {
            int response_code = URLUtils.getResponseCode("https://karmadev.es");

            if (response_code == HTTP_OK) {
                HttpUtil utils = URLUtils.extraUtils(first);

                if (utils != null) {
                    utils.push();
                }
            }
        }
        if (second != null) {
            int response_code = URLUtils.getResponseCode("https://karmarepo.000webhostapp.com");

            if (response_code == HTTP_OK) {
                HttpUtil utils = URLUtils.extraUtils(second);

                if (utils != null) {
                    utils.push();
                }
            }
        }
        if (third != null) {
            int response_code = URLUtils.getResponseCode("https://karmaconfigs.ml/");

            if (response_code == HTTP_OK) {
                HttpUtil utils = URLUtils.extraUtils(third);

                if (utils != null) {
                    utils.push();
                }
            }
        }
        if (fourth != null) {
            int response_code = URLUtils.getResponseCode("https://karmarepo.ml/");

            if (response_code == HTTP_OK) {
                HttpUtil utils = URLUtils.extraUtils(fourth);

                if (utils != null) {
                    utils.push();
                }
            }
        }
    }

    /**
     * Fetch minecraft UUID
     *
     * @param name the minecraft name
     * @param type the uuid type
     * @return the minecraft uuid
     */
    public static UUID fetch(final String name, final UUIDType type) {
        switch (type) {
            case ONLINE:
                try {
                    URL url = URLUtils.getOrBackup(
                            /*"https://api.mojang.com/users/profiles/minecraft/" + name,
                            "https://minecraft-api.com/api/uuid/" + name,
                            "https://api.minetools.eu/uuid/" + name,
                            "https://karmadev.es/?nick=" + name,
                            "https://karmarepo.000webhostapp.com/api/?nick=" + name,*/
                            "https://karmaconfigs.ml/api/?nick=" + name,
                            "https://karmarepo.ml/api/?nick=" + name);

                    if (url != null) {
                        String urlStr = url.toString();
                        String result = null;

                        InputStream response = url.openStream();
                        InputStreamReader responseReader = new InputStreamReader(response, StandardCharsets.UTF_8);
                        BufferedReader reader = new BufferedReader(responseReader);
                        //These APIs provide the UUID in a json format
                        if (urlStr.equalsIgnoreCase("https://api.mojang.com/users/profiles/minecraft/" + name) ||
                                urlStr.equalsIgnoreCase("https://api.minetools.eu/uuid/" + name)) {

                            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                            JsonObject json = gson.fromJson(reader, JsonObject.class);

                            if (json.has("id")) {
                                JsonElement element = json.get("id");
                                if (element.isJsonPrimitive()) {
                                    JsonPrimitive primitive = element.getAsJsonPrimitive();
                                    if (primitive.isString()) {
                                        result = primitive.getAsString();

                                        if (result.equalsIgnoreCase("null"))
                                            result = null;
                                    }
                                }
                            }
                        } else {
                            //These APIs provide the UUID in raw text format
                            if (urlStr.equalsIgnoreCase("https://minecraft-api.com/api/uuid/" + name)) {
                                String line;
                                while ((line = reader.readLine()) != null)
                                    result = line.replaceAll("\\s", "");
                            } else {
                                //OKA API
                                Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                                JsonObject json = gson.fromJson(reader, JsonObject.class);

                                if (json.has("online")) {
                                    JsonArray online = json.getAsJsonArray("online");
                                    for (JsonElement sub : online) {
                                        if (sub.isJsonObject()) {
                                            JsonObject data = sub.getAsJsonObject();
                                            if (data.has("data")) {
                                                JsonElement onlineData = data.get("data");
                                                if (onlineData.isJsonObject()) {
                                                    data = onlineData.getAsJsonObject();

                                                    if (data.has("short")) {
                                                        result = data.get("short").getAsString();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (StringUtils.isNullOrEmpty(result)) {
                                        JsonArray offline = json.getAsJsonArray("offline");
                                        for (JsonElement sub : offline) {
                                            if (sub.isJsonObject()) {
                                                JsonObject data = sub.getAsJsonObject();
                                                if (data.has("data")) {
                                                    JsonElement offlineData = data.get("data");
                                                    if (offlineData.isJsonObject()) {
                                                        data = offlineData.getAsJsonObject();

                                                        if (data.has("short")) {
                                                            result = data.get("short").getAsString();
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        reader.close();
                        responseReader.close();
                        response.close();

                        if (result != null && result.equalsIgnoreCase("Playernotfound!"))
                            result = null;

                        return fromTrimmed(result);
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }

                return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
            case OFFLINE:
            default:
                return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        }
    }

    /**
     * Fetch the client nick
     *
     * @param uuid the UUID to search for
     * @return the nick or null if not available
     * in karma UUID engine database API
     */
    public static String fetchNick(final UUID uuid) {
        String result = null;
        try {
            URL url = URLUtils.getOrBackup(
                    "https://karmadev.es/api/?fetch=" + uuid,
                    "https://karmarepo.000webhostapp.com/api/?fetch=" + uuid,
                    "https://karmaconfigs.ml/api/?fetch=" + uuid,
                    "https://karmarepo.ml/api/?fetch=" + uuid);

            if (url != null) {
                HttpUtil utils = URLUtils.extraUtils(url);
                if (utils != null) {
                    String response = utils.getResponse();

                    if (!StringUtils.isNullOrEmpty(response)) {
                        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                        JsonObject json = gson.fromJson(response, JsonObject.class);

                        //For UUID cases, it's impossible to give more than 2 results
                        if (json.has("name")) {
                            JsonElement element = json.get("name");
                            if (element.isJsonPrimitive()) {
                                JsonPrimitive primitive = element.getAsJsonPrimitive();
                                if (primitive.isString()) {
                                    result = primitive.getAsString();

                                    //There's a player named "Unknown", the error query is unknown, not Unknown, so we must make sure the result is unknown and not Unknown
                                    if (result.equals("unknown"))
                                        result = null;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * Generate an online karma response for the specified name
     *
     * @param name the name
     * @return the name OKA response
     */
    @Nullable
    public static OKAResponse fetchOKA(final String name) {
        OKAResponse result = null;

        if (!name.equalsIgnoreCase("@all")) {
            try {
                URL url = URLUtils.getOrBackup(
                        "https://karmadev.es/api/?fetch=" + name,
                        "https://karmarepo.000webhostapp.com/api/?fetch=" + name,
                        "https://karmaconfigs.ml/api/?fetch=" + name,
                        "https://karmarepo.ml/api/?fetch=" + name);

                if (url != null) {
                    HttpUtil utils = URLUtils.extraUtils(url);
                    if (utils != null) {
                        String response = utils.getResponse();

                        if (!StringUtils.isNullOrEmpty(response)) {
                            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                            JsonObject json = gson.fromJson(response, JsonObject.class);

                            //More than 1 result
                            if (json.has("stored")) {
                                JsonArray data = json.getAsJsonArray("fetched");
                                for (JsonElement element : data) {
                                    if (element.isJsonObject()) {
                                        JsonObject object = element.getAsJsonObject();

                                        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                                            String nick = entry.getKey();
                                            UUID off = null;
                                            UUID on = null;

                                            JsonObject info = entry.getValue().getAsJsonObject();

                                            JsonArray offline = info.getAsJsonArray("offline");
                                            JsonArray online = info.getAsJsonArray("online");

                                            JsonObject offlineData = offline.get(0).getAsJsonObject().get("data").getAsJsonObject();
                                            JsonObject onlineData = online.get(0).getAsJsonObject().get("data").getAsJsonObject();

                                            try {
                                                off = UUID.fromString(offlineData.get("id").getAsString());
                                            } catch (Throwable ignored) {}
                                            try {
                                                on = UUID.fromString(onlineData.get("id").getAsString());
                                            } catch (Throwable ignored) {}

                                            if (nick.equals(name)) {
                                                result = new OKAResponse(name, off, on);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                System.out.println(gson.toJson(json));

                                if (json.has("name")) {
                                    String nick = json.get("name").getAsString();
                                    UUID off = null;
                                    UUID on = null;

                                    JsonArray offline = json.get("offline").getAsJsonArray();
                                    JsonArray online = json.get("online").getAsJsonArray();

                                    JsonObject offlineData = offline.get(0).getAsJsonObject().get("data").getAsJsonObject();
                                    JsonObject onlineData = online.get(0).getAsJsonObject().get("data").getAsJsonObject();
                                    try {
                                        off = UUID.fromString(offlineData.get("id").getAsString());
                                    } catch (Throwable ignored) {
                                    }
                                    try {
                                        on = UUID.fromString(onlineData.get("id").getAsString());
                                    } catch (Throwable ignored) {
                                    }

                                    result = new OKAResponse(nick, off, on);
                                }
                            }
                        }
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Generate an online karma response for the specified uuid
     *
     * @param id the uuid
     * @return the name OKA response
     */
    @Nullable
    public static OKAResponse fetchOKAID(final UUID id) {
        String nick = fetchNick(id);
        return fetchOKA(nick);
    }

    /**
     * Get the stored users amount
     *
     * @return the stored users amount in the OKA database
     */
    public static LateScheduler<Integer> getStored() {
        LateScheduler<Integer> result = new AsyncLateScheduler<>();
        KarmaAPI.source(false).async().queue(() -> {
            int stored = -1;
            Throwable error = null;

            try {
                URL url = URLUtils.getOrBackup(
                        "https://karmadev.es/api/?fetch=@all",
                        "https://karmarepo.000webhostapp.com/api/?fetch=@all",
                        "https://karmaconfigs.ml/api/?fetch=@all",
                        "https://karmarepo.ml/api/?fetch=@all");

                if (url != null) {
                    HttpUtil utils = URLUtils.extraUtils(url);
                    if (utils != null) {
                        String response = utils.getResponse();

                        if (!StringUtils.isNullOrEmpty(response)) {
                            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                            JsonObject json = gson.fromJson(response, JsonObject.class);

                            if (json.has("stored")) {
                                JsonElement element = json.get("stored");
                                if (element.isJsonPrimitive()) {
                                    JsonPrimitive primitive = element.getAsJsonPrimitive();
                                    if (primitive.isNumber()) {
                                        stored = primitive.getAsNumber().intValue();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable ex) {
                error = ex;
            }

            result.complete(stored, error);
        });

        return result;
    }

    /**
     * Fetch all stored users in the OKA database
     *
     * @return all the stored users
     */
    public static LateScheduler<Set<OKAResponse>> fetchAll() {
        LateScheduler<Set<OKAResponse>> result = new AsyncLateScheduler<>();

        KarmaAPI.source(false).async().queue(() -> {
            Set<OKAResponse> okaData = new HashSet<>();

            try {
                URL url = URLUtils.getOrBackup(
                        "https://karmadev.es/api/?fetch=@all",
                        "https://karmarepo.000webhostapp.com/api/?fetch=@all",
                        "https://karmaconfigs.ml/api/?fetch=@all",
                        "https://karmarepo.ml/api/?fetch=@all");

            /*
            Testing purposes

            URL url = URLUtils.getOrNull("http://localhost/api/?fetch=@all");
             */
                if (url != null) {
                    HttpUtil utils = URLUtils.extraUtils(url);
                    if (utils != null) {
                        String response = utils.getResponse();

                        if (!StringUtils.isNullOrEmpty(response)) {
                            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                            JsonObject json = gson.fromJson(response, JsonObject.class);

                            if (json.has("fetched")) {
                                JsonElement element = json.get("fetched");
                                if (element.isJsonArray()) {
                                    JsonArray array = element.getAsJsonArray();

                                    for (JsonElement data : array) {
                                        if (data.isJsonObject()) {
                                            JsonObject obj = data.getAsJsonObject();

                                            obj.entrySet().forEach((account) -> {
                                                String nick = account.getKey();
                                                UUID off = null;
                                                UUID on = null;

                                                JsonObject info = account.getValue().getAsJsonObject();

                                                JsonArray offline = info.getAsJsonArray("offline");
                                                JsonArray online = info.getAsJsonArray("online");

                                                JsonObject offlineData = offline.get(0).getAsJsonObject().get("data").getAsJsonObject();
                                                JsonObject onlineData = online.get(0).getAsJsonObject().get("data").getAsJsonObject();

                                                try {
                                                    off = UUID.fromString(offlineData.get("id").getAsString());
                                                } catch (Throwable ignored) {}
                                                try {
                                                    on = UUID.fromString(onlineData.get("id").getAsString());
                                                } catch (Throwable ignored) {}

                                                OKAResponse tmp = new OKAResponse(nick, off, on);
                                                okaData.add(tmp);
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {}

            result.complete(okaData);
        });

        return result;
    }

    /**
     * Get a UUID from a trimmed UUID
     *
     * @param id the trimmed UUID
     * @return the full UUID
     */
    @Nullable
    public static UUID fromTrimmed(final String id) {
        UUID result;
        if (!StringUtils.isNullOrEmpty(id)) {
            if (!id.contains("-")) {
                StringBuilder builder = new StringBuilder(id);
                builder.insert(20, "-");
                builder.insert(16, "-");
                builder.insert(12, "-");
                builder.insert(8, "-");
                result = UUID.fromString(builder.toString());
            } else {
                result = UUID.fromString(id);
            }
        } else {
            result = null;
        }

        return result;
    }
}
