package dev.mrfdev.locatorhud;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

final class ViewRowSegmentTest {
    @Test
    void composesEveryDirectionAndAngleCombination() {
        assertEquals(
            List.of(
                ViewRowSegment.DIRECTION,
                ViewRowSegment.OPEN_PARENTHESIS,
                ViewRowSegment.HORIZONTAL_ANGLE,
                ViewRowSegment.DIVIDER,
                ViewRowSegment.VERTICAL_ANGLE,
                ViewRowSegment.CLOSE_PARENTHESIS
            ),
            ViewRowSegment.forSettings(true, true)
        );
        assertEquals(
            List.of(ViewRowSegment.DIRECTION),
            ViewRowSegment.forSettings(true, false)
        );
        assertEquals(
            List.of(
                ViewRowSegment.HORIZONTAL_ANGLE,
                ViewRowSegment.DIVIDER,
                ViewRowSegment.VERTICAL_ANGLE
            ),
            ViewRowSegment.forSettings(false, true)
        );
        assertEquals(List.of(), ViewRowSegment.forSettings(false, false));
    }
}
