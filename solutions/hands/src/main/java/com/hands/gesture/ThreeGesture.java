package com.hands.gesture;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.hands.utils.Constants;
import com.hands.utils.HandPoints;
import com.hands.utils.Utils;
import com.hands.utils.VectorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThreeGesture implements IHandGesture {

    private static final String NAME = "THUMB_UP";
    private static final int GESTURE_ID = 1;

    private static final long timer = 5000;

    @Override
    public boolean checkGesture(HandsResult handsResult) {

        List<LandmarkProto.LandmarkList> landmarkList = handsResult.multiHandWorldLandmarks();

        if (landmarkList.size() > 0) {
            int errore = 10;

            //impronta pollice in su
            HashMap<HandPoints, Integer> improntaAnalizzata = new HashMap<HandPoints, Integer>(); //fa fatica quando non si vedono parte delle dita
            improntaAnalizzata.put(HandPoints.THUMB_TIP, 48);//valori rilevati empiricamente su 70 livelli totali
            improntaAnalizzata.put(HandPoints.INDEX_TIP, 65);
            improntaAnalizzata.put(HandPoints.MIDDLE_TIP, 67);
            improntaAnalizzata.put(HandPoints.RING_TIP, 39);
            improntaAnalizzata.put(HandPoints.PINKY_TIP, 32);

            HashMap<HandPoints, Integer> actualLevels = Utils.fingerLevelsToWrist(Constants.NUMERO_LIVELLI, landmarkList.get(0));

            ArrayList<HandPoints> relevantPoints = new ArrayList<HandPoints>(improntaAnalizzata.keySet());

            List<Integer> thumbErrors = new ArrayList<Integer>(2);
            thumbErrors.add(10);
            thumbErrors.add(10);
            List<LandmarkProto.NormalizedLandmark> normalizedLandmarkList = new ArrayList<LandmarkProto.NormalizedLandmark>();
            normalizedLandmarkList.add(handsResult.multiHandLandmarks().get(0).getLandmark(HandPoints.THUMB_BASE.getValue()));
            normalizedLandmarkList.add(handsResult.multiHandLandmarks().get(0).getLandmark(HandPoints.THUMB_LOWER.getValue()));
            normalizedLandmarkList.add(handsResult.multiHandLandmarks().get(0).getLandmark(HandPoints.THUMB_UPPER.getValue()));
            normalizedLandmarkList.add(handsResult.multiHandLandmarks().get(0).getLandmark(HandPoints.THUMB_TIP.getValue()));

            return Utils.checkGesture(relevantPoints, improntaAnalizzata, actualLevels, errore) &&
                    VectorUtils.checkInLineNormalized(normalizedLandmarkList, thumbErrors);
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