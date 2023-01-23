package com.hands.utils;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.HashMap;
import java.util.List;

public class Utils {

    public static float getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }

    public static float getLandmarkDistance(LandmarkProto.Landmark point1, LandmarkProto.Landmark point2) {
        return getDistance(point1.getX(), point1.getY(), point1.getZ(), point2.getX(), point2.getY(), point2.getZ());
    }

    public static boolean isBetween(float value, float low, float high) {
        return value >= low && value <= high;
    }

    public static boolean isBetween(int value, int low, int high) {
        return value >= low && value <= high;
    }

    public static boolean isInsideSphere(float point_x, float point_y, float point_z, float centre_X, float centre_y, float centre_z, float radius) {
        return (point_x - centre_X) * (point_x - centre_X) + (point_y - centre_y) * (point_y - centre_y) + (point_z + centre_z) * (point_z + centre_z) < radius * radius;
    }

    public static boolean checkGesture(List<HandPoints> relevantPoints, HashMap<HandPoints, Integer> targetLevels, HashMap<HandPoints, Integer> fingerLevels, int error) {
        for (HandPoints point : relevantPoints) {
            if (!isBetween(fingerLevels.get(point), targetLevels.get(point) - error, targetLevels.get(point) + error)) {
                return false;
            }
        }
        return true;
    }

    public static HashMap<HandPoints, Integer> fingerLevelsToWrist(int numLevels, LandmarkProto.LandmarkList landmarks) {//per il momento solo indice
        LandmarkProto.Landmark point_20 = landmarks.getLandmark(20);
        LandmarkProto.Landmark point_19 = landmarks.getLandmark(19);
        LandmarkProto.Landmark point_18 = landmarks.getLandmark(18);
        LandmarkProto.Landmark point_17 = landmarks.getLandmark(17);
        LandmarkProto.Landmark point_16 = landmarks.getLandmark(16);
        LandmarkProto.Landmark point_15 = landmarks.getLandmark(15);
        LandmarkProto.Landmark point_14 = landmarks.getLandmark(14);
        LandmarkProto.Landmark point_13 = landmarks.getLandmark(13);
        LandmarkProto.Landmark point_12 = landmarks.getLandmark(12);
        LandmarkProto.Landmark point_11 = landmarks.getLandmark(11);
        LandmarkProto.Landmark point_10 = landmarks.getLandmark(10);
        LandmarkProto.Landmark point_9 = landmarks.getLandmark(9);
        LandmarkProto.Landmark point_8 = landmarks.getLandmark(8);
        LandmarkProto.Landmark point_7 = landmarks.getLandmark(7);
        LandmarkProto.Landmark point_6 = landmarks.getLandmark(6);
        LandmarkProto.Landmark point_5 = landmarks.getLandmark(5);
        LandmarkProto.Landmark point_4 = landmarks.getLandmark(4);
        LandmarkProto.Landmark point_3 = landmarks.getLandmark(3);
        LandmarkProto.Landmark point_2 = landmarks.getLandmark(2);
        LandmarkProto.Landmark point_1 = landmarks.getLandmark(1);
        LandmarkProto.Landmark point_0 = landmarks.getLandmark(0);

        HashMap<HandPoints, Integer> mappaLivelli = new HashMap<HandPoints, Integer>(); //partendo dalla punta del dito fino ad arrivare al polso

        float distanzaMassima = getLandmarkDistance(point_12, point_11);
        distanzaMassima += getLandmarkDistance(point_11, point_10);
        distanzaMassima += getLandmarkDistance(point_10, point_9);
        distanzaMassima += getLandmarkDistance(point_9, point_0);

        //pollice
        float distanzaPunto4 = getLandmarkDistance(point_4, point_0);
        mappaLivelli.put(HandPoints.THUMB_TIP, Math.round((distanzaPunto4 * numLevels) / distanzaMassima));

        float distanzaPunto3 = getLandmarkDistance(point_3, point_0);
        mappaLivelli.put(HandPoints.THUMB_UPPER, Math.round((distanzaPunto3 * numLevels) / distanzaMassima));

        float distanzaPunto2 = getLandmarkDistance(point_2, point_0);
        mappaLivelli.put(HandPoints.THUMB_LOWER, Math.round((distanzaPunto2 * numLevels) / distanzaMassima));

        float distanzaPunto1 = getLandmarkDistance(point_1, point_0);
        mappaLivelli.put(HandPoints.THUMB_BASE, Math.round((distanzaPunto1 * numLevels) / distanzaMassima));

        //indice
        float distanzaPunto8 = getLandmarkDistance(point_8, point_0);
        mappaLivelli.put(HandPoints.INDEX_TIP, Math.round((distanzaPunto8 * numLevels) / distanzaMassima));

        float distanzaPunto7 = getLandmarkDistance(point_7, point_0);
        mappaLivelli.put(HandPoints.INDEX_UPPER, Math.round((distanzaPunto7 * numLevels) / distanzaMassima));

        float distanzaPunto6 = getLandmarkDistance(point_6, point_0);
        mappaLivelli.put(HandPoints.INDEX_LOWER, Math.round((distanzaPunto6 * numLevels) / distanzaMassima));

        float distanzaPunto5 = getLandmarkDistance(point_5, point_0);
        mappaLivelli.put(HandPoints.INDEX_BASE, Math.round((distanzaPunto5 * numLevels) / distanzaMassima));

        //medio
        float distanzaPunto12 = getLandmarkDistance(point_12, point_0);
        mappaLivelli.put(HandPoints.MIDDLE_TIP, Math.round((distanzaPunto12 * numLevels) / distanzaMassima));

        float distanzaPunto11 = getLandmarkDistance(point_11, point_0);
        mappaLivelli.put(HandPoints.MIDDLE_UPPER, Math.round((distanzaPunto11 * numLevels) / distanzaMassima));

        float distanzaPunto10 = getLandmarkDistance(point_10, point_0);
        mappaLivelli.put(HandPoints.MIDDLE_LOWER, Math.round((distanzaPunto10 * numLevels) / distanzaMassima));

        float distanzaPunto9 = getLandmarkDistance(point_9, point_0);
        mappaLivelli.put(HandPoints.MIDDLE_BASE, Math.round((distanzaPunto9 * numLevels) / distanzaMassima));

        //anulare
        float distanzaPunto16 = getLandmarkDistance(point_16, point_0);
        mappaLivelli.put(HandPoints.RING_TIP, Math.round((distanzaPunto16 * numLevels) / distanzaMassima));

        float distanzaPunto15 = getLandmarkDistance(point_15, point_0);
        mappaLivelli.put(HandPoints.RING_UPPER, Math.round((distanzaPunto15 * numLevels) / distanzaMassima));

        float distanzaPunto14 = getLandmarkDistance(point_14, point_0);
        mappaLivelli.put(HandPoints.RING_LOWER, Math.round((distanzaPunto14 * numLevels) / distanzaMassima));

        float distanzaPunto13 = getLandmarkDistance(point_13, point_0);
        mappaLivelli.put(HandPoints.RING_BASE, Math.round((distanzaPunto13 * numLevels) / distanzaMassima));

        //mignolo
        float distanzaPunto20 = getLandmarkDistance(point_20, point_0);
        mappaLivelli.put(HandPoints.PINKY_TIP, Math.round((distanzaPunto20 * numLevels) / distanzaMassima));

        float distanzaPunto19 = getLandmarkDistance(point_19, point_0);
        mappaLivelli.put(HandPoints.PINKY_UPPER, Math.round((distanzaPunto19 * numLevels) / distanzaMassima));

        float distanzaPunto18 = getLandmarkDistance(point_18, point_0);
        mappaLivelli.put(HandPoints.PINKY_LOWER, Math.round((distanzaPunto18 * numLevels) / distanzaMassima));

        float distanzaPunto17 = getLandmarkDistance(point_17, point_0);
        mappaLivelli.put(HandPoints.PINKY_BASE, Math.round((distanzaPunto17 * numLevels) / distanzaMassima));


        return mappaLivelli;
    }

}
