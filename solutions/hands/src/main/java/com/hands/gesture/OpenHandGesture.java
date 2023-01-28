package com.hands.gesture;

import android.app.Activity;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.hands.utils.Constants;
import com.hands.utils.HandPoints;
import com.hands.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpenHandGesture implements IHandGesture {

    private static final String NAME = "OPEN_HAND";
    private static final int GESTURE_ID = 4;

    @Override
    public boolean checkGesture(HandsResult handsResult) {

        List<LandmarkProto.LandmarkList> landmarkList = handsResult.multiHandWorldLandmarks();

        if (landmarkList.size() > 0) {
            int errore = 6;

            HashMap<HandPoints, Integer> improntaAnalizzata = new HashMap<>();
            improntaAnalizzata.put(HandPoints.THUMB_TIP, 35);//valori rilevati empiricamente su 70 livelli totali
            improntaAnalizzata.put(HandPoints.INDEX_TIP, 63);
            improntaAnalizzata.put(HandPoints.MIDDLE_TIP, 67);
            improntaAnalizzata.put(HandPoints.RING_TIP, 62);
            improntaAnalizzata.put(HandPoints.PINKY_TIP, 54);

            HashMap<HandPoints, Integer> actualLevels = Utils.fingerLevelsToWrist(Constants.NUMERO_LIVELLI, landmarkList.get(0));

            ArrayList<HandPoints> relevantPoints = new ArrayList<>(improntaAnalizzata.keySet());

            return Utils.checkGesture(relevantPoints, improntaAnalizzata, actualLevels, errore);
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

