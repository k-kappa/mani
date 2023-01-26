package com.hands.utils;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.List;

public class VectorUtils {

    //function that given a list of 3d landmark points, returns true if the points are
    //on a straight line, given an error for each connection
    public static boolean checkInLine(List<LandmarkProto.Landmark> landmarks, List<Integer> errors) {
        if (landmarks.size() - errors.size() != 2) {
            return false;
        }

        LandmarkProto.Landmark first = landmarks.get(0);
        LandmarkProto.Landmark last = landmarks.get(landmarks.size() - 1);

        for (LandmarkProto.Landmark l : landmarks.subList(1, landmarks.size() - 2)) {
            if (distanceToLine(l, first, last) > errors.get(landmarks.indexOf(l))) {
                return false;
            }
        }

        return true;
    }

    public static double distanceToLine(LandmarkProto.Landmark landmark, LandmarkProto.Landmark landmark1, LandmarkProto.Landmark landmark2) {
        double[] v1 = new double[]{landmark2.getX() - landmark1.getX(), landmark2.getY() - landmark1.getY(), landmark2.getZ() - landmark1.getZ()};
        double[] v2 = new double[]{landmark.getX() - landmark1.getX(), landmark.getY() - landmark1.getY(), landmark.getZ() - landmark1.getZ()};
        double scalar = dotProduct(v1, v2) / dotProduct(v1, v1);
        double[] projection = new double[]{v1[0] * scalar, v1[1] * scalar, v1[2] * scalar};
        double[] distanceVector = new double[]{v2[0] - projection[0], v2[1] - projection[1], v2[2] - projection[2]};
        double distance = Math.sqrt(distanceVector[0] * distanceVector[0] + distanceVector[1] * distanceVector[1] + distanceVector[2] * distanceVector[2]);
        //return distance
        //temp
        //truncate return value to 7 decimal places
        return Math.round(distance * 10000000.0) / 10000000.0;
    }

    public static double distanceToLine(LandmarkProto.NormalizedLandmark landmark, LandmarkProto.NormalizedLandmark landmark1, LandmarkProto.NormalizedLandmark landmark2) {
        double[] v1 = new double[]{landmark2.getX() - landmark1.getX(), landmark2.getY() - landmark1.getY(), landmark2.getZ() - landmark1.getZ()};
        double[] v2 = new double[]{landmark.getX() - landmark1.getX(), landmark.getY() - landmark1.getY(), landmark.getZ() - landmark1.getZ()};
        double scalar = dotProduct(v1, v2) / dotProduct(v1, v1);
        double[] projection = new double[]{v1[0] * scalar, v1[1] * scalar, v1[2] * scalar};
        double[] distanceVector = new double[]{v2[0] - projection[0], v2[1] - projection[1], v2[2] - projection[2]};
        double distance = Math.sqrt(distanceVector[0] * distanceVector[0] + distanceVector[1] * distanceVector[1] + distanceVector[2] * distanceVector[2]);
        //return distance
        //temp
        //truncate return value to 7 decimal places
        return Math.round(distance * 10000000.0) / 10000000.0;
    }

    private static double dotProduct(double[] v1, double[] v2) {
        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }

}
