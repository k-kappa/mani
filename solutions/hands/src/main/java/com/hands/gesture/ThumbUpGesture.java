package com.hands.gesture;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.hands.utils.Utils;

import java.util.HashMap;
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

            //Raccolgo i punti di mio interesse
            LandmarkProto.Landmark point_4 =landmarkList.get(0).getLandmark(4);
            LandmarkProto.Landmark point_8 =landmarkList.get(0).getLandmark(8);
            LandmarkProto.Landmark point_12 =landmarkList.get(0).getLandmark(12);
            LandmarkProto.Landmark point_16 =landmarkList.get(0).getLandmark(16);
            LandmarkProto.Landmark point_20 =landmarkList.get(0).getLandmark(20);
            LandmarkProto.Landmark point_5 =landmarkList.get(0).getLandmark(5);
            LandmarkProto.Landmark point_6 =landmarkList.get(0).getLandmark(6);
            //if (Utils.isBetween(wristThumbDistance, thumbLow, thumbHigh) &&
            //        Utils.isBetween(wristIndexDistance, indexLow, indexHigh) &&
            //        Utils.isBetween(wristMiddleDistance, middleLow, middleHigh) &&
            //        Utils.isBetween(wristRingDistance, ringLow, ringHigh) &&
            //        Utils.isBetween(wristPinkyDistance, pinkyLow, pinkyHigh)) {
            //    return true;
            //}
            //calcolo il raggio della sfera usando la falange prossimale(punto 5 e 6)
            float radius = Utils.getDistance(point_5.getX(),point_5.getY(),point_5.getZ(),point_6.getX(),point_6.getY(),point_6.getZ());

            //if(Utils.isInsideSphere(point_6.getX(),point_6.getY(),point_6.getZ(),point_8.getX(),point_8.getY(),point_8.getZ(),radius) //verifico se il punto 6 e 5 si trovano all'interno
            /*&& Utils.isInsideSphere(point_5.getX(),point_5.getY(),point_5.getZ(),point_8.getX(),point_8.getY(),point_8.getZ(),radius) //mentre verifico che il pollice(punto 4) si trovi al di fuori di tale sfera
                    && !Utils.isInsideSphere(point_4.getX(),point_4.getY(),point_4.getZ(),point_8.getX(),point_8.getY(),point_8.getZ(),radius)){
                return true;
            }*/



           int numeroLivelli = 70;
            int errore = 8;

            //////////impronta pollice in su
            HashMap<Integer,Integer> improntaPollice = new HashMap<Integer,Integer>();
            improntaPollice.put(4,61);//valori rilevati empiricamente su 70 livelli totali
            improntaPollice.put(8,34);
            improntaPollice.put(12,48);
            improntaPollice.put(16,43);
            improntaPollice.put(20,39);

            /////////impronta mano aperta
            HashMap<Integer,Integer> improntaManoAperta = new HashMap<Integer,Integer>();
            improntaManoAperta.put(4,48);//valori rilevati empiricamente su 70 livelli totali
            improntaManoAperta.put(8,63);
            improntaManoAperta.put(12,67);
            improntaManoAperta.put(16,64);
            improntaManoAperta.put(20,54);

            /////////impronta OK
            HashMap<Integer,Integer> improntaOK = new HashMap<Integer,Integer>();
            improntaOK.put(4,53);//valori rilevati empiricamente su 70 livelli totali
            improntaOK.put(8,40);
            improntaOK.put(12,62);
            improntaOK.put(16,66);
            improntaOK.put(20,60);

            /////////impronta mano "artigli"    //questa si confonde con la mano aperta...
            HashMap<Integer,Integer> improntaArtigli = new HashMap<Integer,Integer>();
            improntaArtigli.put(4,49);//valori rilevati empiricamente su 70 livelli totali
            improntaArtigli.put(8,61);
            improntaArtigli.put(12,62);
            improntaArtigli.put(16,59);
            improntaArtigli.put(20,49);

            HashMap<Integer,Integer> improntaAnalizzata = improntaOK;

            if(Utils.isBetween( Utils.returnLevelsOfFingers(numeroLivelli,landmarkList.get(0)).get(4),improntaAnalizzata.get(4)-errore,improntaAnalizzata.get(4)+errore) &&
                    Utils.isBetween( Utils.returnLevelsOfFingers(numeroLivelli,landmarkList.get(0)).get(8),improntaAnalizzata.get(8)-errore,improntaAnalizzata.get(8)+errore) &&
                    Utils.isBetween( Utils.returnLevelsOfFingers(numeroLivelli,landmarkList.get(0)).get(12),improntaAnalizzata.get(12)-errore,improntaAnalizzata.get(12)+errore) &&
                    Utils.isBetween( Utils.returnLevelsOfFingers(numeroLivelli,landmarkList.get(0)).get(16),improntaAnalizzata.get(16)-errore,improntaAnalizzata.get(16)+errore) &&
                    Utils.isBetween( Utils.returnLevelsOfFingers(numeroLivelli,landmarkList.get(0)).get(20),improntaAnalizzata.get(20)-errore,improntaAnalizzata.get(20)+errore)){
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