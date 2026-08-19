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

    @Test
    void formatsEightWayDirectionsWithCompactAxisHints() {
        assertEquals("South [+Z]", DirectionNameFormatter.detailed(0.0F));
        assertEquals("Southwest [-X/+Z]", DirectionNameFormatter.detailed(45.0F));
        assertEquals("West [-X]", DirectionNameFormatter.detailed(90.0F));
        assertEquals("Northwest [-X/-Z]", DirectionNameFormatter.detailed(135.0F));
        assertEquals("North [-Z]", DirectionNameFormatter.detailed(180.0F));
        assertEquals("Northeast [+X/-Z]", DirectionNameFormatter.detailed(-135.0F));
        assertEquals("East [+X]", DirectionNameFormatter.detailed(-90.0F));
        assertEquals("Southeast [+X/+Z]", DirectionNameFormatter.detailed(-45.0F));
    }

    @Test
    void wrapsYawAndUsesStableHalfSectorBoundaries() {
        assertEquals("South [+Z]", DirectionNameFormatter.detailed(360.0F));
        assertEquals("South [+Z]", DirectionNameFormatter.detailed(-360.0F));
        assertEquals("North [-Z]", DirectionNameFormatter.detailed(540.0F));
        assertEquals("South [+Z]", DirectionNameFormatter.detailed(22.499F));
        assertEquals("Southwest [-X/+Z]", DirectionNameFormatter.detailed(22.5F));
        assertEquals("South [+Z]", DirectionNameFormatter.detailed(-22.5F));
        assertEquals("Southeast [+X/+Z]", DirectionNameFormatter.detailed(-22.501F));
        assertEquals("Unknown", DirectionNameFormatter.detailed(Float.NaN));
        assertEquals("Unknown", DirectionNameFormatter.detailed(Float.POSITIVE_INFINITY));
    }

    @Test
    void appliesEachDisplayModeWithoutCouplingItToViewAngles() {
        assertEquals(
            "North",
            DirectionNameFormatter.format(ViewDirectionDisplay.ON, "north", -135.0F)
        );
        assertEquals(
            "Northeast [+X/-Z]",
            DirectionNameFormatter.format(ViewDirectionDisplay.WITH_DETAILS, "north", -135.0F)
        );
        assertEquals(
            "",
            DirectionNameFormatter.format(ViewDirectionDisplay.OFF, "north", -135.0F)
        );
    }
}
