package ml.karmaconfigs.api.common.utils.string;

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

import ml.karmaconfigs.api.common.utils.string.util.TextContent;
import ml.karmaconfigs.api.common.utils.string.util.TextType;

import java.util.Random;

/**
 * Karma string generator options builder
 */
public class OptionsBuilder {

    /**
     * The text size
     */
    private int size = new Random().nextInt(20);

    /**
     * The text content
     */
    private TextContent content = TextContent.ONLY_LETTERS;

    /**
     * The text type
     */
    private TextType type = TextType.ALL_LOWER;

    /**
     * Initialize the options builder
     */
    OptionsBuilder() {}

    /**
     * Set the text size
     *
     * @param sz the text size
     * @return this instance
     */
    public OptionsBuilder withSize(final int sz) {
        size = sz;

        return this;
    }

    /**
     * Set the text content
     *
     * @param ctn the content
     * @return this instance
     */
    public OptionsBuilder withContent(final TextContent ctn) {
        content = ctn;

        return this;
    }

    /**
     * Set the text type
     *
     * @param tp the text tpye
     * @return this instance
     */
    public OptionsBuilder withType(final TextType tp) {
        type = tp;

        return this;
    }

    /**
     * Get the text size
     *
     * @return the text size
     */
    public int getSize() {
        return size;
    }

    /**
     * Get the text content
     *
     * @return the text content
     */
    public TextContent getContent() {
        return content;
    }

    /**
     * Get the text type
     *
     * @return the text type
     */
    public TextType getType() {
        return type;
    }
}
