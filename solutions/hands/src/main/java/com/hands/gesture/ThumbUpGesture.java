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

public class ThumbUpGesture implements IHandGesture {

    private static final String NAME = "THUMB_UP";
    private static final int GESTURE_ID = 1;

    private static final long timer = 5000;

    @Override
    public boolean checkGesture(List<LandmarkProto.LandmarkList> landmarkList) {
        if (landmarkList.size() > 0) {
            int errore = 8;

            //impronta pollice in su
            HashMap<HandPoints, Integer> improntaAnalizzata = new HashMap<HandPoints, Integer>(); //fa fatica quando non si vedono parte delle dita
            improntaAnalizzata.put(HandPoints.THUMB_TIP, 52);//valori rilevati empiricamente su 70 livelli totali
            improntaAnalizzata.put(HandPoints.INDEX_TIP, 35);
            improntaAnalizzata.put(HandPoints.MIDDLE_TIP, 29);
            improntaAnalizzata.put(HandPoints.RING_TIP, 27);
            improntaAnalizzata.put(HandPoints.PINKY_TIP, 29);

            HashMap<HandPoints, Integer> actualLevels = Utils.fingerLevelsToWrist(Constants.NUMERO_LIVELLI, landmarkList.get(0));

            ArrayList<HandPoints> relevantPoints = new ArrayList<HandPoints>(improntaAnalizzata.keySet());

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