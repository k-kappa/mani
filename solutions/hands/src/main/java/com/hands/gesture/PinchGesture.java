package com.hands.gesture;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.hands.utils.Constants;
import com.hands.utils.HandPoints;
import com.hands.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PinchGesture implements IHandGesture {

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

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getGestureId() {
        return 0;
    }

    @Override
    public GestureType getGestureType() {
        return null;
    }
}
