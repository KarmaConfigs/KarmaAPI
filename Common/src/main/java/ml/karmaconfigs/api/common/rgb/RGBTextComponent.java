package ml.karmaconfigs.api.common.rgb;

import ml.karmaconfigs.api.common.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class RGBTextComponent implements Serializable {

    private final boolean parseSimple;
    private final boolean parseHEX;

    private final Pattern hexPattern = Pattern.compile("#[a-fA-f0-9]{6}");
    private final Pattern simplePattern = Pattern.compile("rgb\\([0-9]{1,3},[0-9]{1,3},[0-9]{1,3}\\)", Pattern.CASE_INSENSITIVE);

    /**
     * Initialize the RGB text component
     *
     * @param simple scan for simple RGB
     * @param hex scan for hex
     */
    public RGBTextComponent(final boolean simple, final boolean hex) {
        parseSimple = simple;
        parseHEX = hex;
    }

    /**
     * Try to parse the hex or simple rgb color
     * depending on configuration
     *
     * @param message the message to parse
     * @return the parsed message
     */
    public final String parse(String message) {
        if (parseHEX || parseSimple) {
            try {
                if (parseSimple) {
                    Matcher simpleMatcher = simplePattern.matcher(message);

                    while (simpleMatcher.find()) {
                        String un_parsed = message.substring(simpleMatcher.start(), simpleMatcher.end());
                        un_parsed = StringUtils.replaceLast(un_parsed.replaceFirst("\\(", ""), ")", "");

                        String[] rgb = un_parsed.split(",");

                        int red = Integer.parseInt(rgb[0]);
                        int green = Integer.parseInt(rgb[1]);
                        int blue = Integer.parseInt(rgb[2]);

                        message = message.replace(un_parsed, rgbToHex(red, green, blue));
                        simpleMatcher = simplePattern.matcher(message);
                    }
                }

                if (parseHEX) {
                    Matcher hexMatcher = hexPattern.matcher(message);

                    while (hexMatcher.find()) {
                        String hex = message.substring(hexMatcher.start(), hexMatcher.end());

                        message = message.replace(hex, hexToColor(hex));
                        hexMatcher = hexPattern.matcher(message);
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        return StringUtils.toColor(message);
    }

    private String decToHEX(int n) {
        char[] hexDeciNum = new char[2];

        int i = 0;
        while (n != 0) {
            int temp;
            temp = n % 16;

            if (temp < 10) {
                hexDeciNum[i] = (char) (temp + 48);
            } else {
                hexDeciNum[i] = (char) (temp + 55);
            }
            i++;

            n = n / 16;
        }

        String hexCode = "";
        if (i == 2) {
            hexCode+=hexDeciNum[0];
            hexCode+=hexDeciNum[1];
        } else {
            if (i == 1) {
                hexCode = "0";
                hexCode+=hexDeciNum[0];
            } else {
                if (i == 0) hexCode = "00";
            }
        }

        return hexCode;
    }

    private String rgbToHex(final int R, final int G, final int B) {
        if ((R >= 0 && R <= 255)
                && (G >= 0 && G <= 255)
                && (B >= 0 && B <= 255)) {

            String hexCode = "#";
            hexCode += decToHEX(R);
            hexCode += decToHEX(G);
            hexCode += decToHEX(B);

            return hexCode;
        }
        else
            return "-1";
    }

    private String hexToColor(final String argument) {
        try {
            Class<?> inst = Class.forName("net.md_5.bungee.api.ChatColor");

            Method method = inst.getMethod("of", String.class);
            method.setAccessible(true);

            return String.valueOf(method.invoke(inst, argument));
        } catch (Throwable ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
