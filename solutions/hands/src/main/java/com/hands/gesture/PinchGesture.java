package com.hands.gesture;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.hands.utils.Constants;
import com.hands.utils.HandPoints;
import com.hands.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PinchGesture implements IHandGesture {

    public boolean checkGesture(HandsResult handsResult) {

        List<LandmarkProto.LandmarkList> landmarkList = handsResult.multiHandWorldLandmarks();

        //almeno una mano
        if (landmarkList.size() > 0) {

            HashMap<HandPoints, Integer> targetLevels = new HashMap<HandPoints, Integer>();
            //targetLevels.put(HandPoints.THUMB_TIP, 52);
            //targetLevels.put(HandPoints.INDEX_TIP, 60);
            targetLevels.put(HandPoints.MIDDLE_TIP, 29);
            targetLevels.put(HandPoints.RING_TIP, 27);
            targetLevels.put(HandPoints.PINKY_TIP, 29);

            HashMap<HandPoints, Integer> actualLevels = Utils.fingerLevelsToWrist(Constants.NUMERO_LIVELLI, landmarkList.get(0));

            ArrayList<HandPoints> relevantPoints = new ArrayList<HandPoints>(targetLevels.keySet());

            int thumbTipBaseLevel = Utils.getDistanceLevel(Constants.NUMERO_LIVELLI,
                    landmarkList.get(0).getLandmark(HandPoints.INDEX_TIP.getValue()),
                    landmarkList.get(0).getLandmark(HandPoints.INDEX_BASE.getValue()),
                    landmarkList.get(0));

            return Utils.checkGesture(relevantPoints, targetLevels, actualLevels, 8) &&
                    thumbTipBaseLevel >= 17 && !Utils.indiceMedioAlti(handsResult.multiHandLandmarks().get(0).getLandmarkList());
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

}
