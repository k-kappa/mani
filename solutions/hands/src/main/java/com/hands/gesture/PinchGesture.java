package com.hands.gesture;

import android.app.Activity;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.hands.utils.Constants;
import com.hands.utils.HandPoints;
import com.hands.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PinchGesture implements IHandGesture {

    private static final String NAME = "PINCH";
    private static final int GESTURE_ID = 2;

    @Override
    public boolean checkGesture(List<LandmarkProto.LandmarkList> landmarkList) {

        if (landmarkList.size() > 0) {

            HashMap<HandPoints, Integer> targetLevels = new HashMap<HandPoints, Integer>();
            //targetLevels.put(HandPoints.THUMB_TIP, 52);
            //targetLevels.put(HandPoints.INDEX_TIP, 60);
            targetLevels.put(HandPoints.MIDDLE_TIP, 29);
            targetLevels.put(HandPoints.RING_TIP, 27);
            targetLevels.put(HandPoints.PINKY_TIP, 29);

            HashMap<HandPoints, Integer> actualLevels = Utils.fingerLevelsToWrist(Constants.NUMERO_LIVELLI, landmarkList.get(0));

            ArrayList<HandPoints> relevantPoints = new ArrayList<HandPoints>(targetLevels.keySet());

            return Utils.checkGesture(relevantPoints, targetLevels, actualLevels, 8);
        }

        return false;
    }

    public float getPinchDistance(List<LandmarkProto.LandmarkList> landmarkList) {
        //range zoom 1.0, 3.0, range pinch 10, 50
        if (landmarkList.size() > 0) {
            int pinchLevel = Utils.pinchLevel(Constants.NUMERO_LIVELLI, landmarkList.get(0));
            return Utils.map(pinchLevel, 10, 50, 1, 3);
        }
        return -1;
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
