package ml.karmaconfigs.api.loader;

import ml.karmaconfigs.api.common.karma.KarmaSource;

class Source implements KarmaSource {

    /**
     * Karma source name
     *
     * @return the source name
     */
    @Override
    public String name() {
        return "APILoader";
    }

    /**
     * Karma source version
     *
     * @return the source version
     */
    @Override
    public String version() {
        return "1.0.0" /* For now will be always 1.0.0 */;
    }

    /**
     * Karma source description
     *
     * @return the source description
     */
    @Override
    public String description() {
        return "A loader to load a set of projects using KarmaAPI.";
    }

    /**
     * Karma source authors
     *
     * @return the source authors
     */
    @Override
    public String[] authors() {
        return new String[]{"KarmaDev"};
    }

    /**
     * Karma source update URL
     *
     * @return the source update URL
     */
    @Override
    public String updateURL() {
        return "";
    }
}
