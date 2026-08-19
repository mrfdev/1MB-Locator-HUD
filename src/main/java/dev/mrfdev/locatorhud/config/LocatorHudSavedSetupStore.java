package dev.mrfdev.locatorhud.config;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public final class LocatorHudSavedSetupStore {
    private final LocatorHudConfigStore delegate;
    private String persistenceBlockReason;

    public LocatorHudSavedSetupStore(Path path) {
        this.delegate = new LocatorHudConfigStore(path);
    }

    LocatorHudSavedSetupStore(LocatorHudConfigStore delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    public Path path() {
        return this.delegate.path();
    }

    public LoadResult load() {
        LocatorHudConfigStore.LoadResult result = this.delegate.load();
        this.persistenceBlockReason = result.blocksPersistence()
            ? result.message()
            : null;
        Optional<LocatorHudSettings> settings = switch (result.status()) {
            case LOADED, MIGRATED -> Optional.of(result.settings().copy());
            case MISSING, RECOVERED_MALFORMED, UNSUPPORTED_FUTURE_SCHEMA -> Optional.empty();
        };
        return new LoadResult(
            settings,
            result.status(),
            result.backupPath(),
            result.message()
        );
    }

    public LocatorHudConfigStore.SaveResult save(LocatorHudSettings settings) {
        if (this.persistenceBlockReason != null) {
            return LocatorHudConfigStore.SaveResult.blocked(this.persistenceBlockReason);
        }
        return this.delegate.save(settings);
    }

    public record LoadResult(
        Optional<LocatorHudSettings> settings,
        LocatorHudConfigStore.LoadStatus status,
        Path backupPath,
        String message
    ) {
        public LoadResult {
            Objects.requireNonNull(settings, "settings");
            Objects.requireNonNull(status, "status");
        }

        public boolean requiresWriteBack() {
            return this.status == LocatorHudConfigStore.LoadStatus.MIGRATED
                && this.settings.isPresent();
        }
    }
}
