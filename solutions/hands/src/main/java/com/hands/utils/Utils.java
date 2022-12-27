package com.hands.utils;

public class Utils {

    public static float getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }

    public static boolean isBetween(float value, float low, float high) {
        return value >= low && value <= high;
    }
}
