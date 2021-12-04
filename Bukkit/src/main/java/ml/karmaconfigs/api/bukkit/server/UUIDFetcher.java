package ml.karmaconfigs.api.bukkit.server;

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
import ml.karmaconfigs.api.common.utils.URLUtils;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Karma UUID fetcher
 */
public final class UUIDFetcher {

    /**
     * Fetch the UUID
     *
     * @param name the player name
     * @return the name UUID
     */
    public static UUID fetchUUID(final String name) {
        boolean online = true;
        try {
            online = Bukkit.getOnlineMode();
        } catch (Throwable ignored) {}

        if (online) {
            try {
                URL url = URLUtils.getOrBackup(
                        "https://api.mojang.com/users/profiles/minecraft/" + name,
                        "https://minecraft-api.com/api/uuid/" + name,
                        "https://api.minetools.eu/uuid/" + name);

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

                    if (!StringUtils.isNullOrEmpty(result)) {
                        if (!result.contains("-")) {
                            StringBuilder builder = new StringBuilder(result);
                            builder.insert(20, "-");
                            builder.insert(16, "-");
                            builder.insert(12, "-");
                            builder.insert(8, "-");

                            result = builder.toString();
                        }

                        return UUID.fromString(result);
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
    }

    /**
     * Force online UUID fetch
     *
     * @param name the client name
     * @return the name UUID
     */
    public static UUID forceOnline(final String name) {
        try {
            URL url = URLUtils.getOrBackup(
                    "https://api.mojang.com/users/profiles/minecraft/" + name,
                    "https://minecraft-api.com/api/uuid/" + name,
                    "https://api.minetools.eu/uuid/" + name);

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

                if (!StringUtils.isNullOrEmpty(result)) {
                    if (!result.contains("-")) {
                        StringBuilder builder = new StringBuilder(result);
                        builder.insert(20, "-");
                        builder.insert(16, "-");
                        builder.insert(12, "-");
                        builder.insert(8, "-");

                        result = builder.toString();
                    }

                    return UUID.fromString(result);
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
    }
}
