package ml.karmaconfigs.api.common.utils;

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
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.url.HttpUtil;
import ml.karmaconfigs.api.common.utils.url.URLUtils;
import ml.karmaconfigs.api.common.utils.uuid.UUIDType;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Karma UUID fetcher
 *
 * @deprecated Use {@link ml.karmaconfigs.api.common.utils.uuid.UUIDUtil} instead
 */
@Deprecated
public final class UUIDUtil {

    static {
        KarmaAPI.install();
    }

    /**
     * Register a minecraft client into the karma UUID
     * engine database API
     *
     * @param name the client name
     * @deprecated Use {@link ml.karmaconfigs.api.common.utils.uuid.UUIDUtil#registerMinecraftClient(String)} instead
     */
    @Deprecated
    public static void registerMinecraftClient(final String name) {
        URL first = URLUtils.getOrNull("https://karmadev.es/?nick=" + name);
        URL second = URLUtils.getOrNull("https://karmarepo.000webhostapp.com/api/?nick=" + name);

        if (first != null) {
            int response_code = URLUtils.getResponseCode("https://karmadev.es");

            if (response_code == HttpURLConnection.HTTP_OK) {
                HttpUtil utils = URLUtils.extraUtils(first);

                if (utils != null) {
                    utils.push();
                }
            }
        }
        if (second != null) {
            int response_code = URLUtils.getResponseCode("https://karmarepo.000webhostapp.com");

            if (response_code == HttpURLConnection.HTTP_OK) {
                HttpUtil utils = URLUtils.extraUtils(second);

                if (utils != null) {
                    utils.push();
                }
            }
        }
    }

    /**
     * Fetch the UUID
     *
     * @param name the player name
     * @return the name UUID
     * @deprecated Use {@link ml.karmaconfigs.api.common.utils.uuid.UUIDUtil#fetch(String, UUIDType)} instead
     */
    @Deprecated
    public static UUID fetchMinecraftUUID(final String name) {
        try {
            URL url = URLUtils.getOrBackup(
                    "https://api.mojang.com/users/profiles/minecraft/" + name,
                    "https://minecraft-api.com/api/uuid/" + name,
                    "https://api.minetools.eu/uuid/" + name,
                    "https://karmadev.es/?nick=" + name,
                    "https://karmarepo.000webhostapp.com/api/?nick=" + name);

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
                }

                //These APIs provide the UUID in raw text format
                if (urlStr.equalsIgnoreCase("https://minecraft-api.com/api/uuid/" + name)) {
                    String line;
                    while ((line = reader.readLine()) != null)
                        result = line.replaceAll("\\s", "");
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
    }

    /**
     * Force online UUID fetch
     *
     * @param name the client name
     * @return the name UUID
     * @deprecated Use {@link ml.karmaconfigs.api.common.utils.uuid.UUIDUtil#fetch(String, UUIDType)} instead
     */
    @Deprecated
    public static UUID forceMinecraftOffline(final String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
    }

    /**
     * Fetch the client nick
     *
     * @param uuid the UUID to search for
     * @return the nick or null if not available
     * in karma UUID engine database API
     *
     * @deprecated Use {@link ml.karmaconfigs.api.common.utils.uuid.UUIDUtil#fetchNick(UUID)} instead
     */
    @Deprecated
    public static String fetchNick(final UUID uuid) {
        String result = null;
        try {
            URL url = URLUtils.getOrBackup(
                    "https://karmadev.es/api/?fetch=" + uuid,
                    "https://karmarepo.000webhostapp.com/api/?fetch=" + uuid);

            if (url != null) {
                HttpUtil utils = URLUtils.extraUtils(url);
                if (utils != null) {
                    String response = utils.getResponse();

                    if (!StringUtils.isNullOrEmpty(response)) {
                        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                        JsonObject json = gson.fromJson(response, JsonObject.class);

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
     * Get a UUID from a trimmed UUID
     *
     * @param id the trimmed UUID
     * @return the full UUID
     *
     * @deprecated Use {@link ml.karmaconfigs.api.common.utils.uuid.UUIDUtil#fromTrimmed(String)} instead
     */
    @Nullable
    public @Deprecated static UUID fromTrimmed(final String id) {
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
