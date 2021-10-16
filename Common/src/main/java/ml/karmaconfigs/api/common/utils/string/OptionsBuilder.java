package ml.karmaconfigs.api.common.utils.string;

import ml.karmaconfigs.api.common.utils.string.util.TextContent;
import ml.karmaconfigs.api.common.utils.string.util.TextType;

public class OptionsBuilder {

    private int size = (int) Math.min(Math.random(), 20.0D);
    private TextContent content = TextContent.ONLY_LETTERS;
    private TextType type = TextType.ALL_LOWER;

    OptionsBuilder() {}

    public OptionsBuilder withSize(final int sz) {
        size = sz;

        return this;
    }

    public OptionsBuilder withContent(final TextContent ctn) {
        content = ctn;

        return this;
    }

    public OptionsBuilder withType(final TextType tp) {
        type = tp;

        return this;
    }

    public int getSize() {
        return size;
    }

    public TextContent getContent() {
        return content;
    }

    public TextType getType() {
        return type;
    }
}
