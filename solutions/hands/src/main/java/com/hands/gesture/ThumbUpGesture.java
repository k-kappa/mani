package com.hands.gesture;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.hands.utils.Constants;
import com.hands.utils.HandPoints;
import com.hands.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThumbUpGesture implements IHandGesture {

    private static final String NAME = "THUMB_UP";
    private static final int GESTURE_ID = 1;
    //private static final float thumbLow = 0.125f, thumbHigh = 0.148f;
    //private static final float indexLow = 0.065f, indexHigh = 0.085f;
    //private static final float middleLow = 0.056f, middleHigh = 0.072f;
    //private static final float ringLow = 0.049f, ringHigh = 0.070f;
    //private static final float pinkyLow = 0.058f, pinkyHigh = 0.080f;

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

            //impronta mano aperta
            //HashMap<Integer, Integer> improntaManoAperta = new HashMap<Integer, Integer>();
            //improntaManoAperta.put(4, 48);//valori rilevati empiricamente su 70 livelli totali
            //improntaManoAperta.put(8, 63);
            //improntaManoAperta.put(12, 67);
            //improntaManoAperta.put(16, 64);
            //improntaManoAperta.put(20, 54);

            //impronta OK
            //HashMap<Integer, Integer> improntaOK = new HashMap<Integer, Integer>();
            //improntaOK.put(4, 53);//valori rilevati empiricamente su 70 livelli totali
            //improntaOK.put(8, 40);
            //improntaOK.put(12, 62);
            //improntaOK.put(16, 66);
            //improntaOK.put(20, 60);

            //impronta mano "artigli"    //questa si confonde con la mano aperta...
            //HashMap<Integer, Integer> improntaArtigli = new HashMap<Integer, Integer>();
            //improntaArtigli.put(4, 49);//valori rilevati empiricamente su 70 livelli totali
            //improntaArtigli.put(8, 61);
            //improntaArtigli.put(12, 62);
            //improntaArtigli.put(16, 59);
            //improntaArtigli.put(20, 49);

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