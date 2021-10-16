package ml.karmaconfigs.api.common.version;

import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.scheduler.LateScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.worker.AsyncLateScheduler;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class LegacyVersionUpdater extends VersionUpdater {
    private static final Map<KarmaSource, VersionUpdater.VersionFetchResult> results = new ConcurrentHashMap<>();
    private KarmaSource source;
    private URL checkURL;
    private VersionCheckType versionType;
    private VersionResolver versionResolver;

    private LegacyVersionUpdater() {
    }

    public static VersionUpdater.VersionBuilder createNewBuilder(KarmaSource owner) {
        return new LegacyVersionBuilder(owner);
    }

    public LateScheduler<VersionFetchResult> fetch(boolean force) {
        AsyncLateScheduler<VersionFetchResult> asyncLateScheduler = new AsyncLateScheduler<>();
        if (force || !results.containsKey(this.source) || results.getOrDefault(this.source, null) == null) {
            APISource.asyncScheduler().queue(() -> {
                try {
                    boolean updated;
                    URLConnection connection = this.checkURL.openConnection();
                    InputStream file = connection.getInputStream();
                    Path temp = Files.createTempFile("kfetcher_", StringUtils.randomString(6, StringUtils.StringGen.NUMBERS_AND_LETTERS, StringUtils.StringType.ALL_UPPER), (FileAttribute<?>[]) new FileAttribute[0]);
                    File tempFile = FileUtilities.getFixedFile(temp.toFile());
                    tempFile.deleteOnExit();
                    if (!tempFile.exists())
                        Files.createFile(temp);
                    Files.copy(file, temp, StandardCopyOption.REPLACE_EXISTING);
                    List<String> lines = Files.readAllLines(tempFile.toPath());
                    String version = lines.get(0);
                    String update = lines.get(1);
                    List<String> changelog = new ArrayList<>();
                    for (int i = 2; i < lines.size(); i++)
                        changelog.add(lines.get(i));
                    switch (this.versionType) {
                        case ID:
                            updated = this.source.version().equals(version);
                            break;
                        case RESOLVABLE_ID:
                            updated = (StringUtils.compareTo(this.versionResolver.resolve(this.source.version()), this.versionResolver.resolve(version)) >= 0);
                            break;
                        default:
                            updated = (StringUtils.compareTo(this.source.version(), version) >= 0);
                            break;
                    }
                    VersionUpdater.VersionFetchResult result = new VersionUpdater.VersionFetchResult(updated, version, this.source.version(), update, changelog.<String>toArray(new String[0]), this.versionResolver);
                    results.put(this.source, result);
                    asyncLateScheduler.complete(result);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    asyncLateScheduler.complete(null, ex);
                }
            });
        } else {
            asyncLateScheduler.complete(results.get(this.source));
        }
        return asyncLateScheduler;
    }

    public LateScheduler<VersionFetchResult> get() {
        AsyncLateScheduler<VersionFetchResult> asyncLateScheduler = new AsyncLateScheduler<>();
        APISource.asyncScheduler().queue(() -> {
            VersionUpdater.VersionFetchResult result = results.getOrDefault(this.source, null);
            if (result == null) {
                fetch(true).whenComplete((Consumer<VersionFetchResult>) asyncLateScheduler::complete);
            } else {
                asyncLateScheduler.complete(result);
            }
        });
        return asyncLateScheduler;
    }

    public static class LegacyVersionBuilder extends VersionUpdater.VersionBuilder {
        LegacyVersionBuilder(KarmaSource owner) {
            super(owner);
        }

        public VersionUpdater build() throws IllegalArgumentException {
            try {
                if (!StringUtils.isNullOrEmpty(getSource().updateURL())) {
                    LegacyVersionUpdater analyzer = new LegacyVersionUpdater();
                    analyzer.source = getSource();
                    analyzer.checkURL = new URL(getSource().updateURL());
                    analyzer.versionType = getType();
                    if (getType().equals(VersionCheckType.RESOLVABLE_ID) && getResolver() == null)
                        throw new IllegalArgumentException("Cannot build a version updater with null version resolver and using RESOLVABLE_ID version type");
                    analyzer.versionResolver = getResolver();
                    return analyzer;
                }
                throw new IllegalArgumentException("Cannot build a version updater with null/invalid version check URL");
            } catch (Throwable ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    }
}
