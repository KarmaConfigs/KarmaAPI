package ml.karmaconfigs.api;

import ml.karmaconfigs.api.common.karma.KarmaSource;

public class Source implements KarmaSource {

    @Override
    public String name() {
        return "TestSource";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String description() {
        return "Test sources";
    }

    @Override
    public String[] authors() {
        return new String[]{"KarmaDev"};
    }

    @Override
    public String updateURL() {
        return "https://lolololololol.com";
    }
}
