package ml.karmaconfigs.api.common.version;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karmafile.KarmaFile;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class KarmaUpdaterGenerator {
    private final KarmaSource source;

    private final List<String> lines = Collections.synchronizedList(new ArrayList<>());

    private URL updateURL = null;

    public KarmaUpdaterGenerator(KarmaSource owner) {
        this.source = owner;
    }

    public final void addChangelog(String... changelog) {
        this.lines.addAll(Arrays.asList(changelog));
    }

    public final void addChangelog(List<String> changelog) {
        this.lines.addAll(changelog);
    }

    public final void removeChangelog(int... indexes) {
        for (int index : indexes)
            this.lines.remove(index);
    }

    public final void clearChangelog() {
        this.lines.clear();
    }

    public final void setChangelog(String... changelog) {
        this.lines.clear();
        this.lines.addAll(Arrays.asList(changelog));
    }

    public final void setChangelog(List<String> changelog) {
        this.lines.clear();
        this.lines.addAll(changelog);
    }

    public final void setUpdateURL(URL url) {
        this.updateURL = url;
    }

    public final KarmaFile generate(String name) {
        File destination = FileUtilities.getFixedFile(new File(this.source.getDataPath().toFile(), name + ".kupdter"));
        KarmaFile file = new KarmaFile(destination);
        file.create();
        file.set("VERSION", this.source.version());
        file.set("UPDATE", (this.updateURL != null) ? this.updateURL.toString() : "");
        file.set("CHANGELOG", this.lines);
        return file;
    }
}
