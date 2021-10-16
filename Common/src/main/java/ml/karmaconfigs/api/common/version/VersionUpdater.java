package ml.karmaconfigs.api.common.version;

import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karmafile.KarmaFile;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class VersionUpdater {
    private static final Map<KarmaSource, VersionFetchResult> results = new ConcurrentHashMap<>();
    private KarmaSource source;
    private URL checkURL;
    private VersionCheckType versionType;
    private VersionResolver versionResolver;

    static VersionUpdater instance() {
        return new VersionUpdater() {

        };
    }

    public static VersionBuilder createNewBuilder(KarmaSource owner) {
        return VersionBuilder.instance(owner);
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
                    KarmaFile kFile = new KarmaFile(tempFile);
                    String version = kFile.getString("VERSION", this.source.version());
                    String update = kFile.getString("UPDATE", "");
                    String[] changelog = (String[]) kFile.getStringList("CHANGELOG", new String[0]).toArray((Object[]) new String[0]);
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
                    VersionFetchResult result = new VersionFetchResult(updated, version, this.source.version(), update, changelog, this.versionResolver);
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
            VersionFetchResult result = results.getOrDefault(this.source, null);
            if (result == null) {
                fetch(true).whenComplete((Consumer<VersionFetchResult>) asyncLateScheduler::complete);
            } else {
                asyncLateScheduler.complete(result);
            }
        });
        return asyncLateScheduler;
    }

    public static abstract class VersionBuilder {
        private final KarmaSource source;

        private VersionCheckType versionType = VersionCheckType.NUMBER;

        private VersionResolver versionResolver;

        VersionBuilder(KarmaSource owner) {
            this.source = owner;
        }

        static VersionBuilder instance(KarmaSource owner) {
            return new VersionBuilder(owner) {

            };
        }

        public final VersionBuilder withVersionType(VersionCheckType type) {
            this.versionType = type;
            return this;
        }

        public final VersionBuilder withVersionResolver(VersionResolver resolver) throws IllegalStateException {
            if (this.versionType == VersionCheckType.RESOLVABLE_ID) {
                this.versionResolver = resolver;
            } else {
                throw new IllegalStateException("Cannot set version resolver for non-resolvable version check type builder");
            }
            return this;
        }

        protected KarmaSource getSource() {
            return this.source;
        }

        protected VersionCheckType getType() {
            return this.versionType;
        }

        protected VersionResolver getResolver() {
            return this.versionResolver;
        }

        public VersionUpdater build() throws IllegalArgumentException {
            try {
                if (!StringUtils.isNullOrEmpty(this.source.updateURL()) && this.source.updateURL().endsWith(".kupdter")) {
                    VersionUpdater analyzer = VersionUpdater.instance();
                    analyzer.source = this.source;
                    analyzer.checkURL = new URL(this.source.updateURL());
                    analyzer.versionType = this.versionType;
                    if (this.versionType.equals(VersionCheckType.RESOLVABLE_ID) && this.versionResolver == null)
                        throw new IllegalArgumentException("Cannot build a version updater with null version resolver and using RESOLVABLE_ID version type");
                    analyzer.versionResolver = this.versionResolver;
                    return analyzer;
                }
                throw new IllegalArgumentException("Cannot build a version updater with null/invalid version check URL ( update url must be a .kupdter file )");
            } catch (Throwable ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    }

    public static class VersionFetchResult {
        private final boolean updated;

        private final String latest;

        private final String current;

        private final String update;

        private final String[] changelog;

        private final VersionResolver resolver;

        public VersionFetchResult(KarmaSource source, String latest_version, String downloadURL, String[] changes, VersionResolver solver) {
            this.updated = true;
            this.latest = latest_version;
            this.current = source.version();
            this.update = downloadURL;
            this.changelog = changes;
            this.resolver = solver;
        }

        VersionFetchResult(boolean status, String fetched, String active, String url, String[] changes, VersionResolver solver) {
            this.updated = status;
            this.latest = fetched;
            this.current = active;
            this.update = url;
            this.changelog = changes;
            this.resolver = solver;
        }

        public final boolean isUpdated() {
            return this.updated;
        }

        public final String getLatest() {
            return this.latest;
        }

        public final String getCurrent() {
            return this.current;
        }

        public final String getUpdateURL() {
            return this.update;
        }

        public final String resolve(VersionType type) {
            if (resolver != null) {
                switch (type) {
                    case LATEST:
                        return resolver.resolve(latest);
                    case CURRENT:
                        return resolver.resolve(current);
                }
            } else {
                switch (type) {
                    case CURRENT:
                        return current;
                    case LATEST:
                        return latest;
                }
            }

            return latest;
        }

        public final String[] getChangelog() {
            return this.changelog;
        }

        public enum VersionType {
            CURRENT, LATEST;
        }
    }
}
