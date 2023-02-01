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

public class ThumbUpGesture implements IHandGesture {

    public boolean checkGesture(HandsResult handsResult) {

        List<LandmarkProto.LandmarkList> landmarkList = handsResult.multiHandWorldLandmarks();

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

}