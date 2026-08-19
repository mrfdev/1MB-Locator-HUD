package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class DirectionNameFormatterTest {
    @Test
    void usesTitleCaseForCardinalDirections() {
        assertEquals("North", DirectionNameFormatter.titleCase("north"));
        assertEquals("South", DirectionNameFormatter.titleCase("SOUTH"));
        assertEquals("East", DirectionNameFormatter.titleCase("east"));
        assertEquals("West", DirectionNameFormatter.titleCase("west"));
    }
}
