package com.example.myapplication;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MethodTesting {

    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int EAST = 3;
    private static final int WEST = 4;

    @Test
    public void testCheckRegion() {
        FloatingViewService floatingViewService = new FloatingViewService();
        assertAll("check Region method",
                () -> assertEquals(floatingViewService.checkRegion(0, 0), NORTH),
                () -> assertEquals(floatingViewService.checkRegion(100, -300), NORTH),
                () -> assertEquals(floatingViewService.checkRegion(0, -200), NORTH),
                () -> assertEquals(floatingViewService.checkRegion(-50, -240), NORTH),
                () -> assertEquals(floatingViewService.checkRegion(-300, -150), WEST),
                () -> assertEquals(floatingViewService.checkRegion(-270, 0), WEST),
                () -> assertEquals(floatingViewService.checkRegion(-400, 80), WEST),
                () -> assertEquals(floatingViewService.checkRegion(-100, 350), SOUTH),
                () -> assertEquals(floatingViewService.checkRegion(0, 200), SOUTH),
                () -> assertEquals(floatingViewService.checkRegion(100, 250), SOUTH),
                () -> assertEquals(floatingViewService.checkRegion(320, 120), EAST),
                () -> assertEquals(floatingViewService.checkRegion(350, 0), EAST),
                () -> assertEquals(floatingViewService.checkRegion(250, -150), EAST)
        );
    }
}
