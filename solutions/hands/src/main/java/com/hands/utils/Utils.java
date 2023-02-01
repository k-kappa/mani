package com.hands.utils;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.HashMap;
import java.util.List;

public class Utils {

    static {
        System.loadLibrary("hands");
    }

    private static native double get2DistanceWrap(double[] v1, double[] v2);
    private static native double get3DistanceWrap(double[] v1, double[] v2);
    private static native boolean isBetweenIntWrap(int value, int low, int high);
    private static native boolean indiceMedioAltiWrap(double[] y);


    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return get3DistanceWrap(new double[]{x1, y1, z1}, new double[]{x2, y2, z2});
        //return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }

    public static double getDistance(double x1, double y1, double x2, double y2) {
        return get2DistanceWrap(new double[]{x1, y1}, new double[]{x2, y2});
        //return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static double getLandmarkDistance(LandmarkProto.Landmark point1, LandmarkProto.Landmark point2) {
        return getDistance(point1.getX(), point1.getY(), point1.getZ(), point2.getX(), point2.getY(), point2.getZ());
    }

    public static double getLandmarkDistance(LandmarkProto.NormalizedLandmark point1, LandmarkProto.NormalizedLandmark point2) {
        return getDistance(point1.getX(), point1.getY(), point1.getZ(), point2.getX(), point2.getY(), point2.getZ());
    }

    public static boolean isBetween(int value, int low, int high) {
        return isBetweenIntWrap(value, low, high);
        //return value >= low && value <= high;
    }

    public static boolean checkGesture(List<HandPoints> relevantPoints, HashMap<HandPoints, Integer> targetLevels, HashMap<HandPoints, Integer> fingerLevels, int error) {
        for (HandPoints point : relevantPoints) {
            if (!isBetween(fingerLevels.get(point), targetLevels.get(point) - error, targetLevels.get(point) + error)) {
                return false;
            }
        }
        return true;
    }

    public static int getXYDistanceInLevels(int numLevels, LandmarkProto.NormalizedLandmarkList landmarks, float puntoA_X, float puntoA_Y,float puntoB_X , float puntoB_Y) {

        double distanzaMassima = Utils.maxDistance(landmarks);


        return (int) Math.round((getDistance(puntoA_X,puntoA_Y,puntoB_X,puntoB_Y) * numLevels) / distanzaMassima);

    }

    private static double maxDistance(LandmarkProto.NormalizedLandmarkList landmarks) {
        double distanzaMassima = getLandmarkDistance(landmarks.getLandmark(HandPoints.MIDDLE_TIP.getValue()), landmarks.getLandmark(HandPoints.MIDDLE_UPPER.getValue()));
        distanzaMassima += getLandmarkDistance(landmarks.getLandmark(HandPoints.MIDDLE_UPPER.getValue()), landmarks.getLandmark(HandPoints.MIDDLE_LOWER.getValue()));
        distanzaMassima += getLandmarkDistance(landmarks.getLandmark(HandPoints.MIDDLE_LOWER.getValue()), landmarks.getLandmark(HandPoints.MIDDLE_BASE.getValue()));
        distanzaMassima += getLandmarkDistance(landmarks.getLandmark(HandPoints.MIDDLE_BASE.getValue()), landmarks.getLandmark(HandPoints.WRIST.getValue()));
        return distanzaMassima;
    }

    private static double maxDistance(LandmarkProto.LandmarkList landmarks) {
        double distanzaMassima = getLandmarkDistance(landmarks.getLandmark(HandPoints.MIDDLE_TIP.getValue()), landmarks.getLandmark(HandPoints.MIDDLE_UPPER.getValue()));
        distanzaMassima += getLandmarkDistance(landmarks.getLandmark(HandPoints.MIDDLE_UPPER.getValue()), landmarks.getLandmark(HandPoints.MIDDLE_LOWER.getValue()));
        distanzaMassima += getLandmarkDistance(landmarks.getLandmark(HandPoints.MIDDLE_LOWER.getValue()), landmarks.getLandmark(HandPoints.MIDDLE_BASE.getValue()));
        distanzaMassima += getLandmarkDistance(landmarks.getLandmark(HandPoints.MIDDLE_BASE.getValue()), landmarks.getLandmark(HandPoints.WRIST.getValue()));
        return distanzaMassima;
    }

    public static Integer getDistanceLevel(int numLevels, LandmarkProto.Landmark landmark1, LandmarkProto.Landmark landmark2, LandmarkProto.LandmarkList landmarks) {
        double distanzaMassima = Utils.maxDistance(landmarks);

        double distanzaAttuale = Utils.getLandmarkDistance(landmark1, landmark2);

        return (int) (distanzaAttuale / distanzaMassima * numLevels);
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

        double distanzaMassima = getLandmarkDistance(middle_tip, middle_upper);
        distanzaMassima += getLandmarkDistance(middle_upper, middle_lower);
        distanzaMassima += getLandmarkDistance(middle_lower, middle_base);
        distanzaMassima += getLandmarkDistance(middle_base, wrist);

        //pollice
        double distanzaPunto4 = getLandmarkDistance(thumb_tip, wrist);
        mappaLivelli.put(HandPoints.THUMB_TIP, (int) Math.round((distanzaPunto4 * numLevels) / distanzaMassima));

        double distanzaPunto3 = getLandmarkDistance(thumb_upper, wrist);
        mappaLivelli.put(HandPoints.THUMB_UPPER, (int) Math.round((distanzaPunto3 * numLevels) / distanzaMassima));

        double distanzaPunto2 = getLandmarkDistance(thumb_lower, wrist);
        mappaLivelli.put(HandPoints.THUMB_LOWER, (int) Math.round((distanzaPunto2 * numLevels) / distanzaMassima));

        double distanzaPunto1 = getLandmarkDistance(thumb_base, wrist);
        mappaLivelli.put(HandPoints.THUMB_BASE, (int) Math.round((distanzaPunto1 * numLevels) / distanzaMassima));

        //indice
        double distanzaPunto8 = getLandmarkDistance(index_tip, wrist);
        mappaLivelli.put(HandPoints.INDEX_TIP, (int) Math.round((distanzaPunto8 * numLevels) / distanzaMassima));

        double distanzaPunto7 = getLandmarkDistance(index_upper, wrist);
        mappaLivelli.put(HandPoints.INDEX_UPPER, (int) Math.round((distanzaPunto7 * numLevels) / distanzaMassima));

        double distanzaPunto6 = getLandmarkDistance(index_lower, wrist);
        mappaLivelli.put(HandPoints.INDEX_LOWER, (int) Math.round((distanzaPunto6 * numLevels) / distanzaMassima));

        double distanzaPunto5 = getLandmarkDistance(index_base, wrist);
        mappaLivelli.put(HandPoints.INDEX_BASE, (int) Math.round((distanzaPunto5 * numLevels) / distanzaMassima));

        //medio
        double distanzaPunto12 = getLandmarkDistance(middle_tip, wrist);
        mappaLivelli.put(HandPoints.MIDDLE_TIP, (int) Math.round((distanzaPunto12 * numLevels) / distanzaMassima));

        double distanzaPunto11 = getLandmarkDistance(middle_upper, wrist);
        mappaLivelli.put(HandPoints.MIDDLE_UPPER, (int) Math.round((distanzaPunto11 * numLevels) / distanzaMassima));

        double distanzaPunto10 = getLandmarkDistance(middle_lower, wrist);
        mappaLivelli.put(HandPoints.MIDDLE_LOWER, (int) Math.round((distanzaPunto10 * numLevels) / distanzaMassima));

        double distanzaPunto9 = getLandmarkDistance(middle_base, wrist);
        mappaLivelli.put(HandPoints.MIDDLE_BASE, (int) Math.round((distanzaPunto9 * numLevels) / distanzaMassima));

        //anulare
        double distanzaPunto16 = getLandmarkDistance(ring_tip, wrist);
        mappaLivelli.put(HandPoints.RING_TIP, (int) Math.round((distanzaPunto16 * numLevels) / distanzaMassima));

        double distanzaPunto15 = getLandmarkDistance(ring_upper, wrist);
        mappaLivelli.put(HandPoints.RING_UPPER, (int) Math.round((distanzaPunto15 * numLevels) / distanzaMassima));

        double distanzaPunto14 = getLandmarkDistance(ring_lower, wrist);
        mappaLivelli.put(HandPoints.RING_LOWER, (int) Math.round((distanzaPunto14 * numLevels) / distanzaMassima));

        double distanzaPunto13 = getLandmarkDistance(ring_base, wrist);
        mappaLivelli.put(HandPoints.RING_BASE, (int) Math.round((distanzaPunto13 * numLevels) / distanzaMassima));

        //mignolo
        double distanzaPunto20 = getLandmarkDistance(pinky_tip, wrist);
        mappaLivelli.put(HandPoints.PINKY_TIP, (int) Math.round((distanzaPunto20 * numLevels) / distanzaMassima));

        double distanzaPunto19 = getLandmarkDistance(pinky_upper, wrist);
        mappaLivelli.put(HandPoints.PINKY_UPPER, (int) Math.round((distanzaPunto19 * numLevels) / distanzaMassima));

        double distanzaPunto18 = getLandmarkDistance(pinky_lower, wrist);
        mappaLivelli.put(HandPoints.PINKY_LOWER, (int) Math.round((distanzaPunto18 * numLevels) / distanzaMassima));

        double distanzaPunto17 = getLandmarkDistance(pinky_base, wrist);
        mappaLivelli.put(HandPoints.PINKY_BASE, (int) Math.round((distanzaPunto17 * numLevels) / distanzaMassima));


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

        double distanzaMassima = getLandmarkDistance(middle_tip, middle_upper);
        distanzaMassima += getLandmarkDistance(middle_upper, middle_lower);
        distanzaMassima += getLandmarkDistance(middle_lower, middle_base);
        distanzaMassima += getLandmarkDistance(middle_base, wrist);

        double distanzaPinch = getLandmarkDistance(thumb_tip, index_tip);
        return (int) Math.round((distanzaPinch * numLevels) / distanzaMassima);

    }

    //mappa x (range (in_min, in_max)) in range (out_min, out_max)
    public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static boolean indiceMedioAlti(List<LandmarkProto.NormalizedLandmark> landmarks) {
        double[] array = new double[landmarks.size()];
        for (int i = 0; i < 21; i++) {
            array[i] = landmarks.get(i).getY();
        }

        return indiceMedioAltiWrap(array);

        //float y = landmarks.get(8).getY();
        //float y1 = landmarks.get(7).getY();
        //float y2 = landmarks.get(6).getY();
        //
        //float y3 = landmarks.get(12).getY();
        //float y4 = landmarks.get(11).getY();
        //float y5 = landmarks.get(10).getY();
        //
        //for (int i = 0; i <= 20; i++) {
        //    if (i == 8 || i == 7 || i==6 || i==12 || i==11 || i==10) continue;
        //
        //    // + la y è piccola, + è verso l'alto
        //    if (y > landmarks.get(i).getY() || y2 > landmarks.get(i).getY() || y1 > landmarks.get(i).getY() ||
        //            y3 > landmarks.get(i).getY() || y4 > landmarks.get(i).getY() || y5 > landmarks.get(i).getY()){
        //        return false;
        //    }
        //}
        //return true;
    }

}
