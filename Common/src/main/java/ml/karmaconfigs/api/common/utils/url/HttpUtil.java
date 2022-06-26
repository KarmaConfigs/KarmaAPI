package ml.karmaconfigs.api.common.utils.url;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ml.karmaconfigs.api.common.JavaVM;
import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Karma HTTP Utilities using org.apache.http implementation
 */
public final class HttpUtil {

    private final URI url;

    private static CloseableHttpClient httpClient = null;

    /**
     * Initialize the HTTP utilities
     *
     * @param target the target URL
     * @throws URISyntaxException if the URI of the URL is not valid
     */
    HttpUtil(final URL target) throws URISyntaxException {
        url = target.toURI();

        if (httpClient == null) {
            httpClient = HttpClientBuilder.create()
                    .disableRedirectHandling()
                    .disableDefaultUserAgent()
                    .setUserAgent("KarmaAPI/" + KarmaAPI.getVersion() + " (" + JavaVM.getSystem().getName() + " " + JavaVM.osVersion() + "; " + JavaVM.osModel() + "; " + JavaVM.osArchitecture() + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
                    .build();
        }
    }

    /**
     * Connect and instantly disconnect from a URL.
     * This method may be called only when actually
     * needed
     */
    public void push() {
        try {
            HttpGet httpget = new HttpGet(url);
            httpClient.execute(httpget);
        } catch (Throwable ignored) {}
    }

    /**
     * Get the response
     *
     * @param data the post data
     * @param headers the request headers
     * @return the url response
     */
    @NotNull
    public String getResponse(final Post data, final Header... headers) {
        String response = "";

        try {
            HttpPost postRequest = new HttpPost(url);

            for (Header h : headers)
                postRequest.addHeader(h);

            if (StringUtils.isNullOrEmpty(data.getJson())) {
                List<NameValuePair> params = new ArrayList<>();
                data.getData().forEach((key) -> params.add(new BasicNameValuePair(key, data.get(key))));

                postRequest.setEntity(new UrlEncodedFormEntity(params));
            } else {
                Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

                JsonObject json = gson.fromJson(data.getJson(), JsonObject.class);

                data.getData().forEach((k) -> {
                    json.addProperty(k, data.get(k));
                });

                String string = gson.toJson(json);

                postRequest.setEntity(new StringEntity(string));
            }

            HttpResponse httpResponse = httpClient.execute(postRequest);
            Header[] contentType = httpResponse.getHeaders("Content-type");

            boolean json = false;
            for (Header header : contentType) {
                if (header.getValue().equalsIgnoreCase("application/json")) {
                    json = true;
                    break;
                }
            }
            Scanner sc = new Scanner(httpResponse.getEntity().getContent());

            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) {
                sb.append(sc.next());
            }

            response = sb.toString();

            if (json) {
                Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                JsonElement object = gson.fromJson(response, JsonElement.class);

                //Set json to pretty print
                response = gson.toJson(object);
            }
        } catch (HttpHostConnectException ex) {
            response = "403 - Connection refused";
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return response;
    }

    /**
     * Get the response
     *
     * @param headers the request headers
     * @return the url response
     */
    @NotNull
    public String getResponse(final Header... headers) {
        String response = "";

        try {
            HttpGet httpget = new HttpGet(url);

            for (Header h : headers)
                httpget.addHeader(h);

            HttpResponse httpresponse = httpClient.execute(httpget);
            Header[] contentType = httpresponse.getHeaders("Content-type");

            boolean json = false;
            for (Header header : contentType) {
                if (header.getValue().equalsIgnoreCase("application/json")) {
                    json = true;
                    break;
                }
            }
            Scanner sc = new Scanner(httpresponse.getEntity().getContent());

            StringBuilder sb = new StringBuilder();
            while(sc.hasNext()) {
                sb.append(sc.next());
            }

            response = sb.toString();

            if (json) {
                Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                JsonElement object = gson.fromJson(response, JsonElement.class);

                //Set json to pretty print
                response = gson.toJson(object);
            }
        } catch (HttpHostConnectException ex) {
            response = "403 - Connection refused";
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return response;
    }
}
