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
    private TimedPoint lastPoint, lastPoint1;
    private int scrollUp = 0;
    private int scrollDown = 0;

    public boolean ditaVicine(LandmarkProto.LandmarkList landmarks, int error) {


        TimedPoint t = new TimedPoint();
        t.set(landmarks.getLandmark(12).getX(),landmarks.getLandmark(12).getY());



        int cost = 5;
        if (this.lastPoint == null){
            this.lastPoint = t;
            return false;
        }
        else{

            float x = t.velocityFrom(lastPoint) * 10000;
            if (x>10){
                System.out.println("------------------------------------------------------------" + x);

            }

            if((t.x - lastPoint.x <= 0.07 || lastPoint.x - t.x <= 0.07) && lastPoint.y > t.y - 0.5){
                if(x > 13){
                    return true;
                }
            }
            else if((t.x - lastPoint.x <= 0.07 || lastPoint.x - t.x <= 0.07) && lastPoint.y < t.y + 0.5){
                if(x > 13){
                    return true;
                }
            }
            lastPoint = t;
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
        */

        return false;
    }

    @Override
    public boolean checkGesture(List<LandmarkProto.LandmarkList> landmarkList) {
        if (landmarkList.size() > 0) {



            int errore = 8;

            if(this.ditaVicine(landmarkList.get(0), errore)){
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
        return GestureType.DYNAMIC;
    }
}