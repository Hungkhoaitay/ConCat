package com.example.myapplication;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Local Unit Test:
 * Check region Method: Test whether system can determine the correct app region
 * and return the corresponding data
 */
public class MethodTesting {

    private static final int REGION_1 = -1;
    private static final int REGION_2 = -2;
    private static final int REGION_3 = -3;
    private static final int REGION_4 = 1;
    private static final int REGION_5 = 2;
    private static final int REGION_6 = 3;

    private static final int sampleMinX = -300;
    private static final int sampleMaxX = 300;
    private static final int sampleMinY = -600;
    private static final int sampleMaxY = 600;

    @Test
    public void testCheckRegion() {
        FloatingViewService floatingViewService = new FloatingViewService();
        floatingViewService.minX = sampleMinX;
        floatingViewService.maxX = sampleMaxX;
        floatingViewService.minY = sampleMinY;
        floatingViewService.maxY = sampleMaxY;
        assertAll("check Region method",
                () -> assertEquals(floatingViewService.checkRegion(-250, -100), REGION_1),
                () -> assertEquals(floatingViewService.checkRegion(-50, -200), REGION_2),
                () -> assertEquals(floatingViewService.checkRegion(70, -140), REGION_2),
                () -> assertEquals(floatingViewService.checkRegion(350, -230), REGION_3),
                () -> assertEquals(floatingViewService.checkRegion(-250, 160), REGION_4),
                () -> assertEquals(floatingViewService.checkRegion(-80, 300), REGION_5),
                () -> assertEquals(floatingViewService.checkRegion(90, 240), REGION_5),
                () -> assertEquals(floatingViewService.checkRegion(400, 300), REGION_6),
                () -> assertEquals(floatingViewService.checkRegion(0, 0), REGION_5),
                () -> assertEquals(floatingViewService.checkRegion(200, 0), REGION_6)
        );
    }
}
