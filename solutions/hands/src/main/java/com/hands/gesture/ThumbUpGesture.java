package com.hands.gesture;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.hands.utils.Utils;

import java.util.List;

public class ThumbUpGesture implements IHandGesture {

    private static final String NAME = "THUMB_UP";
    private static final int GESTURE_ID = 1;
    private static final float thumbLow = 0.125f, thumbHigh = 0.148f;
    private static final float indexLow = 0.065f, indexHigh = 0.085f;
    private static final float middleLow = 0.056f, middleHigh = 0.072f;
    private static final float ringLow = 0.049f, ringHigh = 0.070f;
    private static final float pinkyLow = 0.058f, pinkyHigh = 0.080f;

    @Override
    public boolean checkGesture(List<LandmarkProto.LandmarkList> landmarkList) {
        if (landmarkList.size() > 0) {
            LandmarkProto.Landmark thumb = landmarkList.get(0).getLandmark(4);
            LandmarkProto.Landmark index = landmarkList.get(0).getLandmark(8);
            LandmarkProto.Landmark middle = landmarkList.get(0).getLandmark(12);
            LandmarkProto.Landmark ring = landmarkList.get(0).getLandmark(16);
            LandmarkProto.Landmark pinky = landmarkList.get(0).getLandmark(20);
            LandmarkProto.Landmark wrist = landmarkList.get(0).getLandmark(0);

            float wristThumbDistance = Utils.getDistance(thumb.getX(), thumb.getY(), thumb.getZ(), wrist.getX(), wrist.getY(), wrist.getZ());
            float wristIndexDistance = Utils.getDistance(index.getX(), index.getY(), index.getZ(), wrist.getX(), wrist.getY(), wrist.getZ());
            float wristMiddleDistance = Utils.getDistance(middle.getX(), middle.getY(), middle.getZ(), wrist.getX(), wrist.getY(), wrist.getZ());
            float wristRingDistance = Utils.getDistance(ring.getX(), ring.getY(), ring.getZ(), wrist.getX(), wrist.getY(), wrist.getZ());
            float wristPinkyDistance = Utils.getDistance(pinky.getX(), pinky.getY(), pinky.getZ(), wrist.getX(), wrist.getY(), wrist.getZ());

            if (Utils.isBetween(wristThumbDistance, thumbLow, thumbHigh) &&
                    Utils.isBetween(wristIndexDistance, indexLow, indexHigh) &&
                    Utils.isBetween(wristMiddleDistance, middleLow, middleHigh) &&
                    Utils.isBetween(wristRingDistance, ringLow, ringHigh) &&
                    Utils.isBetween(wristPinkyDistance, pinkyLow, pinkyHigh)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return this.NAME;
    }

    @Override
    public int getGestureId() {
        return this.GESTURE_ID;
    }

    @Override
    public GestureType getGestureType() {
        return GestureType.STATIC;
    }
}
