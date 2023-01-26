package com.hands.utils;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.HashMap;
import java.util.List;

public class Utils {

    public static float getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }

    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static float getLandmarkDistance(LandmarkProto.Landmark point1, LandmarkProto.Landmark point2) {
        return getDistance(point1.getX(), point1.getY(), point1.getZ(), point2.getX(), point2.getY(), point2.getZ());
    }

    public static float getLandmarkDistance(LandmarkProto.NormalizedLandmark point1, LandmarkProto.NormalizedLandmark point2) {
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
    
    public static  int getXYDistanceInLevels(int numLevels, LandmarkProto.NormalizedLandmarkList landmarks, float puntoA_X, float puntoA_Y,float puntoB_X , float puntoB_Y){
        LandmarkProto.NormalizedLandmark point_12 = landmarks.getLandmark(12);
        LandmarkProto.NormalizedLandmark point_11 = landmarks.getLandmark(11);
        LandmarkProto.NormalizedLandmark point_10 = landmarks.getLandmark(10);
        LandmarkProto.NormalizedLandmark point_9 = landmarks.getLandmark(9);
        LandmarkProto.NormalizedLandmark point_0 = landmarks.getLandmark(0);

        float distanzaMassima = getLandmarkDistance(point_12, point_11);
        distanzaMassima += getLandmarkDistance(point_11, point_10);
        distanzaMassima += getLandmarkDistance(point_10, point_9);
        distanzaMassima += getLandmarkDistance(point_9, point_0);

        return Math.round((getDistance(puntoA_X,puntoA_Y,puntoB_X,puntoB_Y) * numLevels) / distanzaMassima);
    }

    public static HashMap<HandPoints, Integer> fingerLevelsToWrist(int numLevels, LandmarkProto.LandmarkList landmarks) {//per il momento solo indice
        LandmarkProto.Landmark pinky_tip = landmarks.getLandmark(HandPoints.PINKY_TIP.getValue());
        LandmarkProto.Landmark pinky_upper = landmarks.getLandmark(HandPoints.PINKY_UPPER.getValue());
        LandmarkProto.Landmark pinky_lower = landmarks.getLandmark(HandPoints.PINKY_LOWER.getValue());
        LandmarkProto.Landmark pinky_base = landmarks.getLandmark(HandPoints.PINKY_BASE.getValue());
        LandmarkProto.Landmark ring_tip = landmarks.getLandmark(HandPoints.RING_TIP.getValue());
        LandmarkProto.Landmark ring_upper = landmarks.getLandmark(HandPoints.RING_UPPER.getValue());
        LandmarkProto.Landmark ring_lower = landmarks.getLandmark(HandPoints.RING_LOWER.getValue());
        LandmarkProto.Landmark ring_base = landmarks.getLandmark(HandPoints.RING_BASE.getValue());
        LandmarkProto.Landmark middle_tip = landmarks.getLandmark(HandPoints.MIDDLE_TIP.getValue());
        LandmarkProto.Landmark middle_upper = landmarks.getLandmark(HandPoints.MIDDLE_UPPER.getValue());
        LandmarkProto.Landmark middle_lower = landmarks.getLandmark(HandPoints.MIDDLE_LOWER.getValue());
        LandmarkProto.Landmark middle_base = landmarks.getLandmark(HandPoints.MIDDLE_BASE.getValue());
        LandmarkProto.Landmark index_tip = landmarks.getLandmark(HandPoints.INDEX_TIP.getValue());
        LandmarkProto.Landmark index_upper = landmarks.getLandmark(HandPoints.INDEX_UPPER.getValue());
        LandmarkProto.Landmark index_lower = landmarks.getLandmark(HandPoints.INDEX_LOWER.getValue());
        LandmarkProto.Landmark index_base = landmarks.getLandmark(HandPoints.INDEX_BASE.getValue());
        LandmarkProto.Landmark thumb_tip = landmarks.getLandmark(HandPoints.THUMB_TIP.getValue());
        LandmarkProto.Landmark thumb_upper = landmarks.getLandmark(HandPoints.THUMB_UPPER.getValue());
        LandmarkProto.Landmark thumb_lower = landmarks.getLandmark(HandPoints.THUMB_LOWER.getValue());
        LandmarkProto.Landmark thumb_base = landmarks.getLandmark(HandPoints.THUMB_BASE.getValue());
        LandmarkProto.Landmark wrist = landmarks.getLandmark(HandPoints.WRIST.getValue());

        HashMap<HandPoints, Integer> mappaLivelli = new HashMap<HandPoints, Integer>(); //partendo dalla punta del dito fino ad arrivare al polso

        float distanzaMassima = getLandmarkDistance(middle_tip, middle_upper);
        distanzaMassima += getLandmarkDistance(middle_upper, middle_lower);
        distanzaMassima += getLandmarkDistance(middle_lower, middle_base);
        distanzaMassima += getLandmarkDistance(middle_base, wrist);

        //pollice
        float distanzaPunto4 = getLandmarkDistance(thumb_tip, wrist);
        mappaLivelli.put(HandPoints.THUMB_TIP, Math.round((distanzaPunto4 * numLevels) / distanzaMassima));

        float distanzaPunto3 = getLandmarkDistance(thumb_upper, wrist);
        mappaLivelli.put(HandPoints.THUMB_UPPER, Math.round((distanzaPunto3 * numLevels) / distanzaMassima));

        float distanzaPunto2 = getLandmarkDistance(thumb_lower, wrist);
        mappaLivelli.put(HandPoints.THUMB_LOWER, Math.round((distanzaPunto2 * numLevels) / distanzaMassima));

        float distanzaPunto1 = getLandmarkDistance(thumb_base, wrist);
        mappaLivelli.put(HandPoints.THUMB_BASE, Math.round((distanzaPunto1 * numLevels) / distanzaMassima));

        //indice
        float distanzaPunto8 = getLandmarkDistance(index_tip, wrist);
        mappaLivelli.put(HandPoints.INDEX_TIP, Math.round((distanzaPunto8 * numLevels) / distanzaMassima));

        float distanzaPunto7 = getLandmarkDistance(index_upper, wrist);
        mappaLivelli.put(HandPoints.INDEX_UPPER, Math.round((distanzaPunto7 * numLevels) / distanzaMassima));

        float distanzaPunto6 = getLandmarkDistance(index_lower, wrist);
        mappaLivelli.put(HandPoints.INDEX_LOWER, Math.round((distanzaPunto6 * numLevels) / distanzaMassima));

        float distanzaPunto5 = getLandmarkDistance(index_base, wrist);
        mappaLivelli.put(HandPoints.INDEX_BASE, Math.round((distanzaPunto5 * numLevels) / distanzaMassima));

        //medio
        float distanzaPunto12 = getLandmarkDistance(middle_tip, wrist);
        mappaLivelli.put(HandPoints.MIDDLE_TIP, Math.round((distanzaPunto12 * numLevels) / distanzaMassima));

        float distanzaPunto11 = getLandmarkDistance(middle_upper, wrist);
        mappaLivelli.put(HandPoints.MIDDLE_UPPER, Math.round((distanzaPunto11 * numLevels) / distanzaMassima));

        float distanzaPunto10 = getLandmarkDistance(middle_lower, wrist);
        mappaLivelli.put(HandPoints.MIDDLE_LOWER, Math.round((distanzaPunto10 * numLevels) / distanzaMassima));

        float distanzaPunto9 = getLandmarkDistance(middle_base, wrist);
        mappaLivelli.put(HandPoints.MIDDLE_BASE, Math.round((distanzaPunto9 * numLevels) / distanzaMassima));

        //anulare
        float distanzaPunto16 = getLandmarkDistance(ring_tip, wrist);
        mappaLivelli.put(HandPoints.RING_TIP, Math.round((distanzaPunto16 * numLevels) / distanzaMassima));

        float distanzaPunto15 = getLandmarkDistance(ring_upper, wrist);
        mappaLivelli.put(HandPoints.RING_UPPER, Math.round((distanzaPunto15 * numLevels) / distanzaMassima));

        float distanzaPunto14 = getLandmarkDistance(ring_lower, wrist);
        mappaLivelli.put(HandPoints.RING_LOWER, Math.round((distanzaPunto14 * numLevels) / distanzaMassima));

        float distanzaPunto13 = getLandmarkDistance(ring_base, wrist);
        mappaLivelli.put(HandPoints.RING_BASE, Math.round((distanzaPunto13 * numLevels) / distanzaMassima));

        //mignolo
        float distanzaPunto20 = getLandmarkDistance(pinky_tip, wrist);
        mappaLivelli.put(HandPoints.PINKY_TIP, Math.round((distanzaPunto20 * numLevels) / distanzaMassima));

        float distanzaPunto19 = getLandmarkDistance(pinky_upper, wrist);
        mappaLivelli.put(HandPoints.PINKY_UPPER, Math.round((distanzaPunto19 * numLevels) / distanzaMassima));

        float distanzaPunto18 = getLandmarkDistance(pinky_lower, wrist);
        mappaLivelli.put(HandPoints.PINKY_LOWER, Math.round((distanzaPunto18 * numLevels) / distanzaMassima));

        float distanzaPunto17 = getLandmarkDistance(pinky_base, wrist);
        mappaLivelli.put(HandPoints.PINKY_BASE, Math.round((distanzaPunto17 * numLevels) / distanzaMassima));


        return mappaLivelli;
    }

    public static int pinchLevel(int numLevels, LandmarkProto.LandmarkList landmarks) {

        LandmarkProto.Landmark thumb_tip = landmarks.getLandmark(HandPoints.THUMB_TIP.getValue());
        LandmarkProto.Landmark index_tip = landmarks.getLandmark(HandPoints.INDEX_TIP.getValue());
        LandmarkProto.Landmark middle_tip = landmarks.getLandmark(HandPoints.MIDDLE_TIP.getValue());
        LandmarkProto.Landmark wrist = landmarks.getLandmark(HandPoints.WRIST.getValue());
        LandmarkProto.Landmark middle_upper = landmarks.getLandmark(HandPoints.MIDDLE_UPPER.getValue());
        LandmarkProto.Landmark middle_lower = landmarks.getLandmark(HandPoints.MIDDLE_LOWER.getValue());
        LandmarkProto.Landmark middle_base = landmarks.getLandmark(HandPoints.MIDDLE_BASE.getValue());

        float distanzaMassima = getLandmarkDistance(middle_tip, middle_upper);
        distanzaMassima += getLandmarkDistance(middle_upper, middle_lower);
        distanzaMassima += getLandmarkDistance(middle_lower, middle_base);
        distanzaMassima += getLandmarkDistance(middle_base, wrist);

        float distanzaPinch = getLandmarkDistance(thumb_tip, index_tip);
        return Math.round((distanzaPinch * numLevels) / distanzaMassima);

    }

    //mappa x (range (in_min, in_max)) in range (out_min, out_max)
    public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

}
