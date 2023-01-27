package com.hands.gesture;

import android.graphics.Point;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.hands.utils.Constants;
import com.hands.utils.HandPoints;
import com.hands.utils.TimedPoint;
import com.hands.utils.Utils;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScrollPageGesture implements IHandGesture {

    private static final String NAME = "SCROLL";
    private static final int GESTURE_ID = 3;
    //private static final float thumbLow = 0.125f, thumbHigh = 0.148f;
    //private static final float indexLow = 0.065f, indexHigh = 0.085f;
    //private static final float middleLow = 0.056f, middleHigh = 0.072f;
    //private static final float ringLow = 0.049f, ringHigh = 0.070f;
    //private static final float pinkyLow = 0.058f, pinkyHigh = 0.080f;

    private List<TimedPoint> lastTimedPoints = new ArrayList<TimedPoint>();
    private int scrollUp = 0;
    private int scrollDown = 0;

    /*
    public boolean attaccate(List<TimedPoint> t) {

        float erroreSpostamentoX = (float) 0.8;
        for (int i = 0; i < 3; i++){
            if ( !Utils.isBetween(t.get(i).x, t.get(i+1).x, erroreSpostamentoX) || Utils.isBetween(t.get(i).x, t.get(i+1).x, (float) 0.01)){
                return false;
            }
        }
        return true;
    }


    public boolean ditaVicine(LandmarkProto.LandmarkList landmarks, int error) {


        List<TimedPoint> t = new ArrayList<TimedPoint>();
        int k = 0;
        TimedPoint temp = new TimedPoint();
        for (int i = 0; i <= 20; i+=4){
            if (i == 0) continue;
            t.add(k, temp.set(landmarks.getLandmark(i).getX(),landmarks.getLandmark(i).getY()));
            k++;
        }


        float erroreSpostamentoX = (float) 0.04;
        List<Float> velocity = new ArrayList<Float>();


        if (this.lastTimedPoints.isEmpty() && attaccate(t)){
            this.lastTimedPoints = t;
            return false;
        }
        else{

            for (int i = 0; i < 4; i++){
                velocity.add(i,t.get(i).velocityFrom(lastTimedPoints.get(i)));
            }
            for (int i = 0; i < 4; i++){
                // ogni dito deve essere stato abbastanza veloce
                if (velocity.get(i)*100000>50 &&
                    Utils.isBetween(t.get(i).x, lastTimedPoints.get(i).x, erroreSpostamentoX) &&
                    t.get(i).y < lastTimedPoints.get(i).y &&
                    attaccate(t)){

                    System.out.println("------------------------------------------------ " + t.get(i).x + "  " + lastTimedPoints.get(i).x);
                    return true;
                }

                else if (velocity.get(i)*100000>50 &&
                        Utils.isBetween(t.get(i).x, lastTimedPoints.get(i).x, erroreSpostamentoX) &&
                        t.get(i).y > lastTimedPoints.get(i).y &&
                        attaccate(t)){

                    System.out.println("------------------------------------------------ " + t.get(i).x + "  " + lastTimedPoints.get(i).x);
                    return true;
                }
            }


            lastTimedPoints = t;
        }
        /*
        else{
            if (t.y < lastPoint.y && (t1.x < t.x) && (t2.y-t1.y<=0.2 || t1.y-t2.y<=0.2 )){
                scrollUp++;
                scrollDown=0;
            }
            else if (t.y > lastPoint.y && (t1.x < t.x) && (t2.y-t1.y<=0.2 || t1.y-t2.y<=0.2 )) {
                scrollUp = 0;
                scrollDown++;
            }
            else{
                scrollDown = 0;
                scrollUp = 0;
            }

            this.lastPoint = t;
            System.out.println("------------------------------------------------------------" + scrollDown + " " + scrollUp);

            if(scrollUp > 12 || scrollDown > 12){
                return true;
            }
        }*/

        /*
        List<TimedPoint> t = new ArrayList<TimedPoint>();
        int k= 0, i;
        TimedPoint temp;
        for (i = 1; i <= 20; i++){
            if(i%4 == 0){
                temp = new TimedPoint();
                temp.set(landmarks.getLandmark(i).getX(), landmarks.getLandmark(i).getY());
                t.add(k,temp);
                k++;
            }
        }

        if (this.lastTimedPoints.isEmpty()){
            this.lastTimedPoints = t;
            return false;
        }
        else{
            TimedPoint tt;
            k=0;
            for (i = 1; i <= 20; i++){
                if(i%4 == 0){

                    tt = this.lastTimedPoints.get(k);

                    System.out.println("------------------------------------------------------------" + t.get(k).distanceTo(tt));
                    k++;
                    //if(t.get(k).distanceTo(tt) > 3)
                }
            }
        }


        return false;
    }*/


    public boolean indiceAlto(List<LandmarkProto.NormalizedLandmark> landmarks){

        float y = landmarks.get(8).getY();
        float y1 = landmarks.get(7).getY();
        float y2 = landmarks.get(6).getY();
        for (int i = 0; i <= 20; i++){
            if (i == 8 || i == 7 || i==6) continue;

            // + la y è piccola, + è verso l'alto
            if (y > landmarks.get(i).getY() || y2 > landmarks.get(i).getY() || y1 > landmarks.get(i).getY()){

                return false;
            }
        }

        return true;
    }

    private float beforeLast8landmarks = 0;
    private float last8landmarks = 0;

    public boolean scorrimento(List<LandmarkProto.NormalizedLandmark> landmarks, int error){

        List<TimedPoint> t = new ArrayList<TimedPoint>();
        TimedPoint temp = new TimedPoint();

        if (indiceAlto(landmarks) == true){
            for (int i = 0; i <= 20; i++){
                t.add(i, temp.set(landmarks.get(i).getX(),landmarks.get(i).getY()));
            }

            if (lastTimedPoints.isEmpty()){
                lastTimedPoints = t;
                beforeLast8landmarks = last8landmarks;
                last8landmarks = landmarks.get(8).getX();
                return false;
            }
            else {

                if (last8landmarks < 0.4 && last8landmarks > 0.22){
                    if ((lastTimedPoints.get(8).velocityFrom(t.get(8)))*10000 > 20 && (landmarks.get(8).getX() - last8landmarks > 0.2) /*|| (landmarks.get(8).getX() - beforeLast8landmarks > 0.2)*/){
                        lastTimedPoints = t;
                        beforeLast8landmarks = last8landmarks;
                        last8landmarks = landmarks.get(8).getX();
                        return true;

                    }
                }
                else if (last8landmarks > 0.6 && last8landmarks < 0.78){
                    if ((lastTimedPoints.get(8).velocityFrom(t.get(8)))*10000 > 20 && (last8landmarks - landmarks.get(8).getX() > 0.2) /*|| (beforeLast8landmarks - landmarks.get(8).getX() < 0.2)*/){
                        lastTimedPoints = t;
                        beforeLast8landmarks = last8landmarks;
                        last8landmarks = landmarks.get(8).getX();
                        return true;

                    }
                }

                lastTimedPoints = t;
                beforeLast8landmarks = last8landmarks;
                last8landmarks = landmarks.get(8).getX();
            }
        }

        return false;
    }


    public boolean checGesture(List<LandmarkProto.NormalizedLandmarkList> landmarkList) {
        if (landmarkList.size() > 0) {

            int errore = 8;


            if(this.scorrimento(landmarkList.get(0).getLandmarkList(), errore)){
                return true;
            }


        }
        return false;
    }

    @Override
    public boolean checkGesture(List<LandmarkProto.LandmarkList> landmarkList) {
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