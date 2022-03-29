package ml.karmaconfigs.api.common.rgb;

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

import com.google.gson.JsonObject;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RGB text component for bukkit/bungee/velocity
 */
public final class RGBTextComponent implements Serializable {

    /**
     * Parse simple rgb
     */
    private final boolean parseSimple;
    /**
     * Parse hex rgb
     */
    private final boolean parseHEX;

    /**
     * Hex pattern
     */
    private final Pattern hexPattern = Pattern.compile("#[a-fA-f0-9]{6}");
    /**
     * RGB pattern
     */
    private final Pattern simplePattern = Pattern.compile("rgb\\([0-9]{1,3},[0-9]{1,3},[0-9]{1,3}\\)", 2);

    /**
     * Initialize the RGB text component
     *
     * @param simple parse simple rgb
     * @param hex parse hex rgb
     */
    public RGBTextComponent(final boolean simple, final boolean hex) {
        this.parseSimple = simple;
        this.parseHEX = hex;
    }

    /**
     * Parse the message to rgb
     *
     * @param message the message to parse
     * @return the parsed message
     */
    public String parse(String message) {
        if (this.parseHEX || this.parseSimple)
            try {
                if (this.parseSimple) {
                    Matcher simpleMatcher = this.simplePattern.matcher(message);
                    while (simpleMatcher.find()) {
                        String un_parsed = message.substring(simpleMatcher.start(), simpleMatcher.end());
                        un_parsed = StringUtils.replaceLast(un_parsed.replaceFirst("\\(", ""), ")", "");
                        String[] rgb = un_parsed.split(",");
                        int red = Integer.parseInt(rgb[0]);
                        int green = Integer.parseInt(rgb[1]);
                        int blue = Integer.parseInt(rgb[2]);
                        message = message.replace(un_parsed, rgbToHex(red, green, blue));
                        simpleMatcher = this.simplePattern.matcher(message);
                    }
                }
                if (this.parseHEX) {
                    Matcher hexMatcher = this.hexPattern.matcher(message);
                    while (hexMatcher.find()) {
                        String hex = message.substring(hexMatcher.start(), hexMatcher.end());
                        message = message.replace(hex, hexToColor(hex));
                        hexMatcher = this.hexPattern.matcher(message);
                    }
                }
            } catch (Throwable ignored) {
            }
        return StringUtils.toColor(message);
    }

    /**
     * Decode the number to its hex
     * variant
     *
     * @param n the number
     * @return the number hex
     */
    private String decToHEX(int n) {
        char[] hexDeciNum = new char[2];
        int i = 0;
        while (n != 0) {
            int temp = n % 16;
            if (temp < 10) {
                hexDeciNum[i] = (char) (temp + 48);
            } else {
                hexDeciNum[i] = (char) (temp + 55);
            }
            i++;
            n /= 16;
        }
        String hexCode = "";
        if (i == 2) {
            hexCode = hexCode + hexDeciNum[0];
            hexCode = hexCode + hexDeciNum[1];
        } else if (i == 1) {
            hexCode = "0";
            hexCode = hexCode + hexDeciNum[0];
        } else if (i == 0) {
            hexCode = "00";
        }
        return hexCode;
    }

    /**
     * Parse the rgb code to hex variant
     *
     * @param R red
     * @param G green
     * @param B blue
     * @return the rgb hex variant
     */
    private String rgbToHex(final int R, final int G, final int B) {
        if (R >= 0 && R <= 255 && G >= 0 && G <= 255 && B >= 0 && B <= 255) {
            String hexCode = "#";
            hexCode = hexCode + decToHEX(R);
            hexCode = hexCode + decToHEX(G);
            hexCode = hexCode + decToHEX(B);
            return hexCode;
        }
        return "-1";
    }

    /**
     * Get the rgb variant from the hex
     *
     * @param hex the hex
     * @return the rgb variant from the hex
     */
    private int[] hexToRGB(final String hex) {
        int red = Integer.valueOf(hex.substring( 1, 3 ), 16);
        int green = Integer.valueOf(hex.substring( 3, 5 ), 16);
        int blue = Integer.valueOf(hex.substring( 5, 7 ), 16);

        return new int[]{red, green, blue};
    }

    /**
     * Parse the hex code to chat color variant
     *
     * @param argument the hex code
     * @return the chat color variant of the hex code
     */
    private String hexToColor(final String argument) {
        try {
            Class<?> inst = Class.forName("net.md_5.bungee.api.ChatColor");
            Method method = inst.getMethod("of", String.class);
            method.setAccessible(true);
            return String.valueOf(method.invoke(inst, argument));
        } catch (Throwable ex) {
            try {
                Class<?> inst = Class.forName("net.kyori.adventure.text.format.TextColor");
                Method method = inst.getMethod("color", int.class, int.class, int.class);
                int[] rgb = hexToRGB(argument);

                Object textColor = method.invoke(inst, rgb[0], rgb[2], rgb[3]);
                Class<?> clazz = textColor.getClass();
                Method examinableName = clazz.getMethod("examinableName");

                return String.valueOf(examinableName.invoke(textColor));
            } catch (Throwable exc) {
                exc.printStackTrace();
            }
        }

        return "";
    }
}
