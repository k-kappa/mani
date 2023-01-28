package com.hands.gesture;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandsResult;
import com.hands.utils.HandPoints;
import com.hands.utils.TimedPoint;
import java.util.ArrayList;
import java.util.List;

public class ScrollPageGesture implements IHandGesture {

    private static final String NAME = "SCROLL";
    private static final int GESTURE_ID = 3;

    private List<TimedPoint> lastTimedPoints = new ArrayList<TimedPoint>();
    private float last8landmarks = 0;
    private int puntaIndice = HandPoints.INDEX_TIP.getValue();

    public boolean indiceAlto(List<LandmarkProto.NormalizedLandmark> landmarks){

        float y = landmarks.get(8).getY();
        float y1 = landmarks.get(7).getY();
        float y2 = landmarks.get(6).getY();

        for (int i = 0; i <= 20; i++) {
            if (i == 8 || i == 7 || i==6) continue;

            // + la y è piccola, + è verso l'alto
            if (y > landmarks.get(i).getY() || y2 > landmarks.get(i).getY() || y1 > landmarks.get(i).getY()){
                return false;
            }
        }
        return true;
    }

    public int scorrimento(List<LandmarkProto.NormalizedLandmark> landmarks, int error){

        List<TimedPoint> t = new ArrayList<TimedPoint>();
        TimedPoint temp = new TimedPoint();

        if (indiceAlto(landmarks)){
            for (int i = 0; i <= 20; i++){
                t.add(i, temp.set(landmarks.get(i).getX(),landmarks.get(i).getY()));
            }

            if (!lastTimedPoints.isEmpty()){
                if (last8landmarks < 0.42 && last8landmarks > 0.22){
                    if ((lastTimedPoints.get(puntaIndice).velocityFrom(t.get(puntaIndice)))*10000 > 20 && landmarks.get(puntaIndice).getX() - last8landmarks > 0.2){
                        lastTimedPoints = t;
                        last8landmarks = landmarks.get(puntaIndice).getX();
                        return 1;

                    }
                }
                else if (last8landmarks > 0.58 && last8landmarks < 0.78){
                    if ((lastTimedPoints.get(puntaIndice).velocityFrom(t.get(puntaIndice)))*10000 > 20 && last8landmarks - landmarks.get(puntaIndice).getX() > 0.2){
                        lastTimedPoints = t;
                        last8landmarks = landmarks.get(puntaIndice).getX();
                        return 2;

                    }
                }
            }
            lastTimedPoints = t;
            last8landmarks = landmarks.get(puntaIndice).getX();
        }
        return 0;
    }


    public int checGesture(List<LandmarkProto.NormalizedLandmarkList> landmarkList) {
        if (landmarkList.size() > 0) {
            int errore = 8;

            return scorrimento(landmarkList.get(0).getLandmarkList(), errore);
        }
        return -1;
    }

    @Override
    public boolean checkGesture(HandsResult sd) {
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
        return GestureType.DYNAMIC;
    }
}