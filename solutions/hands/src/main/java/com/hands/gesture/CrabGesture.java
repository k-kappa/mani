package com.hands.gesture;

import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.hands.utils.Constants;
import com.hands.utils.HandPoints;
import com.hands.utils.Utils;
import com.hands.utils.VectorUtils;

import java.util.ArrayList;
import java.util.List;

public class CrabGesture implements IHandGesture {//qui lavoriamo solo su coordinate bidimensionali

    private final ArrayList<Float> delta;//x e y
    private final ArrayList<Float> puntiPrec;//x e y del frame precedente
    private boolean firstTime;

    public CrabGesture() {
        firstTime = true;
        delta = new ArrayList<Float>();
        puntiPrec = new ArrayList<Float>();
        delta.add(0, 0f);//x
        delta.add(1, 0f);//y
        puntiPrec.add(0, 0f);
        puntiPrec.add(1, 0f);
    }

    //@Override
    public boolean checkGesture(HandsResult handsResult) {

        ImmutableList<LandmarkProto.NormalizedLandmarkList> landmarkList = handsResult.multiHandLandmarks();

        if (landmarkList.size() > 0) {


            int range = 7;

            float puntoThumbX = landmarkList.get(0).getLandmark(HandPoints.THUMB_TIP.getValue()).getX();
            float puntoThumbY = landmarkList.get(0).getLandmark(HandPoints.THUMB_TIP.getValue()).getY();
            float puntoIndexX = landmarkList.get(0).getLandmark(HandPoints.INDEX_TIP.getValue()).getX();
            float puntoIndexY = landmarkList.get(0).getLandmark(HandPoints.INDEX_TIP.getValue()).getY();
            boolean flagCrab = Utils.getXYDistanceInLevels(Constants.NUMERO_LIVELLI, landmarkList.get(0), puntoThumbX, puntoThumbY, puntoIndexX, puntoIndexY) <= range;


            if (firstTime) {
                firstTime = false;
                puntiPrec.add(0, puntoThumbX);
                puntiPrec.add(1, puntoThumbY);
            }


            if (flagCrab) {
                delta.add(0, puntoThumbX - puntiPrec.get(0));//x
                delta.add(1, puntoThumbY - puntiPrec.get(1));//y   //qui delta rappresenta un vettore
            } else {
                this.clearDelta();
            }

            List<Integer> indexError = new ArrayList<Integer>(2);
            indexError.add(43);
            indexError.add(43);
            List<LandmarkProto.NormalizedLandmark> normalizedLandmarkList = new ArrayList<LandmarkProto.NormalizedLandmark>();
            normalizedLandmarkList.add(handsResult.multiHandLandmarks().get(0).getLandmark(HandPoints.INDEX_BASE.getValue()));
            normalizedLandmarkList.add(handsResult.multiHandLandmarks().get(0).getLandmark(HandPoints.INDEX_LOWER.getValue()));
            normalizedLandmarkList.add(handsResult.multiHandLandmarks().get(0).getLandmark(HandPoints.INDEX_UPPER.getValue()));
            normalizedLandmarkList.add(handsResult.multiHandLandmarks().get(0).getLandmark(HandPoints.INDEX_TIP.getValue()));


            return flagCrab && !VectorUtils.checkInLineNormalized(normalizedLandmarkList, indexError);
        }
        return false;
    }

    public float getVettoreX() {
        if (delta.size() > 2) {
            float temp = delta.get(0);
            Log.println(Log.DEBUG, "debugg", "x " + delta.get(0) * 10000);
            //delta.add(0,0f);
            return temp;
        } else {
            return 0;
        }
    }

    public float getVettoreY() {
        if (delta.size() > 2) {
            float temp = delta.get(1);
            Log.println(Log.DEBUG, "debugg", "y " + delta.get(1) * 10000);
            //delta.add(1,0f);
            return temp;
        } else {
            return 0;
        }
    }

    public void clearDelta() {
        delta.clear();
        firstTime = true;
    }

}