package dev.mrfdev.locatorhud.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class ConfigSaveDebouncerTest {
    @Test
    void becomesDueOnlyAfterTheConfiguredQuietPeriod() {
        ConfigSaveDebouncer debouncer = new ConfigSaveDebouncer(3);

        debouncer.request();

        assertTrue(debouncer.pending());
        assertFalse(debouncer.tick());
        assertFalse(debouncer.tick());
        assertTrue(debouncer.tick());
        assertFalse(debouncer.pending());
        assertFalse(debouncer.tick());
    }

    @Test
    void anotherChangeRestartsTheQuietPeriod() {
        ConfigSaveDebouncer debouncer = new ConfigSaveDebouncer(2);

        debouncer.request();
        assertFalse(debouncer.tick());
        debouncer.request();

        assertFalse(debouncer.tick());
        assertTrue(debouncer.tick());
    }

    @Test
    void pendingWorkCanBeTakenOnceForAnExplicitFlush() {
        ConfigSaveDebouncer debouncer = new ConfigSaveDebouncer(5);

        assertFalse(debouncer.takePending());
        debouncer.request();
        assertTrue(debouncer.takePending());
        assertFalse(debouncer.takePending());
        assertFalse(debouncer.pending());
    }

    @Test
    void cancellationDiscardsPendingWork() {
        ConfigSaveDebouncer debouncer = new ConfigSaveDebouncer(1);

        debouncer.request();
        debouncer.cancel();

        assertFalse(debouncer.pending());
        assertFalse(debouncer.tick());
    }

    @Test
    void rejectsNonPositiveDelays() {
        assertThrows(IllegalArgumentException.class, () -> new ConfigSaveDebouncer(0));
        assertThrows(IllegalArgumentException.class, () -> new ConfigSaveDebouncer(-1));
    }
}
