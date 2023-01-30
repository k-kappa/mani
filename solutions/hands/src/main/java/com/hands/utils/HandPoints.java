package com.hands.utils;

public enum HandPoints {
    WRIST(0),
    THUMB_BASE(1),
    THUMB_LOWER(2),
    THUMB_UPPER(3),
    THUMB_TIP(4),
    INDEX_BASE(5),
    INDEX_LOWER(6),
    INDEX_UPPER(7),
    INDEX_TIP(8),
    MIDDLE_BASE(9),
    MIDDLE_LOWER(10),
    MIDDLE_UPPER(11),
    MIDDLE_TIP(12),
    RING_BASE(13),
    RING_LOWER(14),
    RING_UPPER(15),
    RING_TIP(16),
    PINKY_BASE(17),
    PINKY_LOWER(18),
    PINKY_UPPER(19),
    PINKY_TIP(20);

    private final int value;

    HandPoints(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
