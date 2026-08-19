package dev.mrfdev.locatorhud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class LocatorHudConfigStore {
    public static final int CURRENT_SCHEMA_VERSION = 2;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DateTimeFormatter BACKUP_TIMESTAMP = DateTimeFormatter
        .ofPattern("uuuuMMdd-HHmmss-SSS", Locale.ROOT)
        .withZone(ZoneOffset.UTC);

    private final Path path;
    private final Clock clock;

    public LocatorHudConfigStore(Path path) {
        this(path, Clock.systemUTC());
    }

    LocatorHudConfigStore(Path path, Clock clock) {
        this.path = Objects.requireNonNull(path, "path").toAbsolutePath().normalize();
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public Path path() {
        return this.path;
    }

    public LoadResult load() {
        if (!Files.isRegularFile(this.path)) {
            return new LoadResult(LocatorHudSettings.defaults(), LoadStatus.MISSING, null, null);
        }

        try (Reader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonObject()) {
                throw new JsonParseException("Configuration root must be a JSON object");
            }

            JsonObject document = root.getAsJsonObject();
            int schemaVersion = schemaVersion(document);
            if (schemaVersion > CURRENT_SCHEMA_VERSION) {
                return new LoadResult(
                    LocatorHudSettings.defaults(),
                    LoadStatus.UNSUPPORTED_FUTURE_SCHEMA,
                    null,
                    "Configuration schema " + schemaVersion + " is newer than supported schema "
                        + CURRENT_SCHEMA_VERSION
                );
            }

            boolean migrated = schemaVersion < CURRENT_SCHEMA_VERSION;
            if (migrated) {
                document.addProperty("schemaVersion", CURRENT_SCHEMA_VERSION);
            }

            LocatorHudSettings settings = GSON.fromJson(document, LocatorHudSettings.class);
            if (settings == null) {
                throw new JsonParseException("Configuration did not contain settings");
            }
            settings.validate();
            return new LoadResult(
                settings,
                migrated ? LoadStatus.MIGRATED : LoadStatus.LOADED,
                null,
                null
            );
        } catch (IOException | RuntimeException exception) {
            Path backup = preserveBrokenFile();
            return new LoadResult(
                LocatorHudSettings.defaults(),
                LoadStatus.RECOVERED_MALFORMED,
                backup,
                describe(exception)
            );
        }
    }

    public SaveResult save(LocatorHudSettings settings) {
        LocatorHudSettings snapshot = Objects.requireNonNull(settings, "settings").copy();
        Path parent = this.path.getParent();
        Path temporary = null;
        try {
            if (parent == null) {
                throw new IOException("Configuration path has no parent directory");
            }
            Files.createDirectories(parent);
            temporary = Files.createTempFile(parent, this.path.getFileName().toString() + ".", ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                JsonObject document = new JsonObject();
                document.addProperty("schemaVersion", CURRENT_SCHEMA_VERSION);
                GSON.toJsonTree(snapshot).getAsJsonObject().entrySet().forEach(
                    entry -> document.add(entry.getKey(), entry.getValue())
                );
                GSON.toJson(document, writer);
            }
            try {
                Files.move(
                    temporary,
                    this.path,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, this.path, StandardCopyOption.REPLACE_EXISTING);
            }
            return SaveResult.saved();
        } catch (IOException | RuntimeException exception) {
            deleteTemporaryFile(temporary);
            return SaveResult.failed(describe(exception));
        }
    }

    private static int schemaVersion(JsonObject document) {
        JsonElement element = document.get("schemaVersion");
        if (element == null) {
            return 0;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new JsonParseException("schemaVersion must be a number");
        }
        int version;
        try {
            version = new BigDecimal(element.getAsString()).intValueExact();
        } catch (NumberFormatException | ArithmeticException exception) {
            throw new JsonParseException("schemaVersion must be an integer", exception);
        }
        if (version < 0) {
            throw new JsonParseException("schemaVersion must not be negative");
        }
        return version;
    }

    private Path preserveBrokenFile() {
        if (!Files.exists(this.path)) {
            return null;
        }
        String fileName = this.path.getFileName().toString();
        String stem = fileName.endsWith(".json")
            ? fileName.substring(0, fileName.length() - ".json".length())
            : fileName;
        String timestamp = BACKUP_TIMESTAMP.format(this.clock.instant());
        for (int suffix = 0; suffix < 100; suffix++) {
            String disambiguator = suffix == 0 ? "" : "." + suffix;
            Path backup = this.path.resolveSibling(
                stem + "." + timestamp + disambiguator + ".broken.json"
            );
            try {
                return Files.move(this.path, backup);
            } catch (java.nio.file.FileAlreadyExistsException ignored) {
                // Try a numbered name without overwriting another recovered file.
            } catch (IOException | RuntimeException exception) {
                return null;
            }
        }
        return null;
    }

    private static void deleteTemporaryFile(Path temporary) {
        if (temporary == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporary);
        } catch (IOException | RuntimeException ignored) {
            // The original save failure is more useful than a cleanup failure.
        }
    }

    private static String describe(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank()
            ? exception.getClass().getSimpleName()
            : exception.getClass().getSimpleName() + ": " + message;
    }

    public enum LoadStatus {
        LOADED,
        MIGRATED,
        MISSING,
        RECOVERED_MALFORMED,
        UNSUPPORTED_FUTURE_SCHEMA
    }

    public record LoadResult(
        LocatorHudSettings settings,
        LoadStatus status,
        Path backupPath,
        String message
    ) {
        public LoadResult {
            Objects.requireNonNull(settings, "settings");
            Objects.requireNonNull(status, "status");
        }

        public boolean requiresWriteBack() {
            return this.status == LoadStatus.MIGRATED
                || this.status == LoadStatus.MISSING
                || (this.status == LoadStatus.RECOVERED_MALFORMED && this.backupPath != null);
        }

        public boolean blocksPersistence() {
            return this.status == LoadStatus.UNSUPPORTED_FUTURE_SCHEMA
                || (this.status == LoadStatus.RECOVERED_MALFORMED && this.backupPath == null);
        }
    }

    public enum SaveStatus {
        SAVED,
        FAILED,
        BLOCKED_PROTECTED_CONFIG
    }

    public record SaveResult(SaveStatus status, String message) {
        public SaveResult {
            Objects.requireNonNull(status, "status");
        }

        public static SaveResult saved() {
            return new SaveResult(SaveStatus.SAVED, null);
        }

        public static SaveResult failed(String message) {
            return new SaveResult(SaveStatus.FAILED, message);
        }

        public static SaveResult blocked(String message) {
            return new SaveResult(SaveStatus.BLOCKED_PROTECTED_CONFIG, message);
        }

        public boolean wasSaved() {
            return this.status == SaveStatus.SAVED;
        }
    }
}
