package ml.karmaconfigs.api.common.utils.string.color;

import ml.karmaconfigs.api.common.utils.string.StringUtils;

/**
 * Known console colors
 */
public enum ConsoleColor {
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(0, 0, 0)">Black</p>
     */
    BLACK("0"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(0, 0, 128)">Navy</p>
     */
    DARK_BLUE("1"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(0, 128, 0)">Green</p>
     */
    DARK_GREEN("2"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(0, 128, 128)">Teal</p>
     */
    DARK_AQUA("3"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(128, 0, 0)">Maroon</p>
     */
    DARK_RED("4"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(128, 0, 128)">Purple</p>
     */
    DARK_PURPLE("5"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(128, 128, 0)">Olive</p>
     */
    DARK_YELLOW("6"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(192, 192, 192)">Silver</p>
     */
    GRAY("7"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(128, 128, 128)">Grey</p>
     */
    DARK_GRAY("8"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(0, 95, 255)">Dodger blue 2</p>
     */
    BLUE("9"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(0, 255, 0)">Lime</p>
     */
    GREEN("a"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(0, 255, 255)">Aqua</p>
     */
    AQUA("b"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(255, 0, 0)">Red</p>
     */
    RED("c"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(255, 0, 255)">Fuchsia</p>
     */
    PURPLE("d"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(255, 255, 0)">Yellow</p>
     */
    YELLOW("e"),
    /**
     * Console color<br>
     * <br>
     * <p style="color: rgb(255, 255, 255)">White</p>
     */
    WHITE("f"),
    /**
     * Console color
     * <br>
     * <p style="font-weight: bold">Bold</p>
     */
    BOLD("l"),
    /**
     * Console color
     * <br>
     * <p style="text-decoration: line-through">Strikethrough</p>
     */
    STRIKETHROUGH("m"),
    /**
     * Console color
     * <br>
     * <p style="text-decoration: underline">Underline</p>
     */
    UNDERLINE("n"),
    /**
     * Console color
     * <br>
     * <p style="font-style: italic">Italic</p>
     */
    ITALIC("o"),
    /**
     * Console color
     * <br>
     * <p>Default</p>
     */
    RESET("r"),
    /**
     * Console color
     * <br>
     * <p>Custom</p>
     */
    CUSTOM("r");

    private String color_code;

    /**
     * Initialize the console color
     * 
     * @param code the color code
     */
    ConsoleColor(final String code) {
        color_code = code;
    }

    /**
     * Set the color code
     *
     * @param code the color code
     * @return this instance
     */
    private ConsoleColor setCode(final String code) {
        color_code = code;

        return this;
    }

    /**
     * Get if the current color is custom
     *
     * @return if the current color has a custom color code
     */
    public final boolean isCustom() {
        for (ConsoleColor color : values()) {
            if (color != CUSTOM) {
                if (color_code.equals(color.color_code))
                    return false;
            }
        }
        
        return true;
    }

    /**
     * Get the color code
     *
     * @return the color code
     */
    public final String getCode() {
        return StringUtils.SINGLE_COLOR_IDENTIFIER + color_code;
    }

    /**
     * Create a new custom color
     *
     * @param code the color code
     * @return the custom color code
     */
    public static ConsoleColor customColor(final String code) {
        return ConsoleColor.CUSTOM.setCode(code);
    }

    /**
     * Get a color from the specified color code
     *
     * @param code the color code
     * @return the color
     */
    public static ConsoleColor fromCode(final String code) {
        for (ConsoleColor color : values()) {
            if (color.getCode().equals(code))
                return color;
        }

        return customColor(code);
    }
}
