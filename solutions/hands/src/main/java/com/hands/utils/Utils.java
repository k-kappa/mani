package com.hands.utils;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {

    public static float getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }

    public static boolean isBetween(float value, float low, float high) {
        return value >= low && value <= high;
    }

    public static boolean isBetween(int value, int low, int high) {
        return value >= low && value <= high;
    }

    public static boolean isInsideSphere(float point_x,float point_y,float point_z,float centre_X,float centre_y,float centre_z,float radius){
        return (point_x-centre_X)*(point_x-centre_X)+(point_y-centre_y)*(point_y-centre_y)+(point_z+centre_z)*(point_z+centre_z)<radius*radius;
    }

    public static HashMap<Integer,Integer> returnLevelsOfFingers(int quantitàLivelli, LandmarkProto.LandmarkList raccoltaPunti){//per il momento solo indice
        LandmarkProto.Landmark point_20=raccoltaPunti.getLandmark(20);
        LandmarkProto.Landmark point_19=raccoltaPunti.getLandmark(19);
        LandmarkProto.Landmark point_18=raccoltaPunti.getLandmark(18);
        LandmarkProto.Landmark point_17=raccoltaPunti.getLandmark(17);
        LandmarkProto.Landmark point_16=raccoltaPunti.getLandmark(16);
        LandmarkProto.Landmark point_15=raccoltaPunti.getLandmark(15);
        LandmarkProto.Landmark point_14=raccoltaPunti.getLandmark(14);
        LandmarkProto.Landmark point_13=raccoltaPunti.getLandmark(13);
        LandmarkProto.Landmark point_12=raccoltaPunti.getLandmark(12);
        LandmarkProto.Landmark point_11=raccoltaPunti.getLandmark(11);
        LandmarkProto.Landmark point_10=raccoltaPunti.getLandmark(10);
        LandmarkProto.Landmark point_9=raccoltaPunti.getLandmark(9);
        LandmarkProto.Landmark point_8=raccoltaPunti.getLandmark(8);
        LandmarkProto.Landmark point_7=raccoltaPunti.getLandmark(7);
        LandmarkProto.Landmark point_6=raccoltaPunti.getLandmark(6);
        LandmarkProto.Landmark point_5=raccoltaPunti.getLandmark(5);
        LandmarkProto.Landmark point_4=raccoltaPunti.getLandmark(4);
        LandmarkProto.Landmark point_3=raccoltaPunti.getLandmark(3);
        LandmarkProto.Landmark point_2=raccoltaPunti.getLandmark(2);
        LandmarkProto.Landmark point_1=raccoltaPunti.getLandmark(1);
        LandmarkProto.Landmark point_0=raccoltaPunti.getLandmark(0);

        HashMap<Integer,Integer> mappaLivelli = new HashMap<Integer,Integer>(); //partendo dalla punta del dito fino ad arrivare al polso

        float distanzaMassima = getDistance(point_12.getX(), point_12.getY(),point_12.getZ(),point_11.getX(),point_11.getY(),point_11.getZ());
        distanzaMassima += getDistance(point_11.getX(), point_11.getY(),point_11.getZ(),point_10.getX(),point_10.getY(),point_10.getZ());
        distanzaMassima += getDistance(point_10.getX(), point_10.getY(),point_10.getZ(),point_9.getX(),point_9.getY(),point_9.getZ());
        distanzaMassima += getDistance(point_9.getX(), point_9.getY(),point_9.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());

        //pollice
        float distanzaPunto4 = getDistance(point_4.getX(), point_4.getY(),point_4.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(4,Math.round((distanzaPunto4*quantitàLivelli)/distanzaMassima));

        float distanzaPunto3 = getDistance(point_3.getX(), point_3.getY(),point_3.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(3,Math.round((distanzaPunto3*quantitàLivelli)/distanzaMassima));

        float distanzaPunto2 = getDistance(point_2.getX(), point_2.getY(),point_2.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(2,Math.round((distanzaPunto2*quantitàLivelli)/distanzaMassima));

        float distanzaPunto1 = getDistance(point_1.getX(), point_1.getY(),point_1.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(1,Math.round((distanzaPunto1*quantitàLivelli)/distanzaMassima));

        //indice
        float distanzaPunto8 = getDistance(point_8.getX(), point_8.getY(),point_8.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(8,Math.round((distanzaPunto8*quantitàLivelli)/distanzaMassima));

        float distanzaPunto7 = getDistance(point_7.getX(), point_7.getY(),point_7.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(7,Math.round((distanzaPunto7*quantitàLivelli)/distanzaMassima));

        float distanzaPunto6 = getDistance(point_6.getX(), point_6.getY(),point_6.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(6,Math.round((distanzaPunto6*quantitàLivelli)/distanzaMassima));

        float distanzaPunto5 = getDistance(point_5.getX(), point_5.getY(),point_5.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(5,Math.round((distanzaPunto5*quantitàLivelli)/distanzaMassima));

        //medio
        float distanzaPunto12 = getDistance(point_12.getX(), point_12.getY(),point_12.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(12,Math.round((distanzaPunto12*quantitàLivelli)/distanzaMassima));

        float distanzaPunto11 = getDistance(point_11.getX(), point_11.getY(),point_11.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(11,Math.round((distanzaPunto11*quantitàLivelli)/distanzaMassima));

        float distanzaPunto10 = getDistance(point_10.getX(), point_10.getY(),point_10.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(10,Math.round((distanzaPunto10*quantitàLivelli)/distanzaMassima));

        float distanzaPunto9 = getDistance(point_9.getX(), point_9.getY(),point_9.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(9,Math.round((distanzaPunto9*quantitàLivelli)/distanzaMassima));

        //anulare
        float distanzaPunto16 = getDistance(point_16.getX(), point_16.getY(),point_16.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(16,Math.round((distanzaPunto16*quantitàLivelli)/distanzaMassima));

        float distanzaPunto15 = getDistance(point_15.getX(), point_15.getY(),point_15.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(15,Math.round((distanzaPunto15*quantitàLivelli)/distanzaMassima));

        float distanzaPunto14 = getDistance(point_14.getX(), point_14.getY(),point_14.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(14,Math.round((distanzaPunto14*quantitàLivelli)/distanzaMassima));

        float distanzaPunto13 = getDistance(point_13.getX(), point_13.getY(),point_13.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(13,Math.round((distanzaPunto13*quantitàLivelli)/distanzaMassima));

        //mignolo
        float distanzaPunto20 = getDistance(point_20.getX(), point_20.getY(),point_20.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(20,Math.round((distanzaPunto20*quantitàLivelli)/distanzaMassima));

        float distanzaPunto19 = getDistance(point_19.getX(), point_19.getY(),point_19.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(19,Math.round((distanzaPunto19*quantitàLivelli)/distanzaMassima));

        float distanzaPunto18 = getDistance(point_18.getX(), point_18.getY(),point_18.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(18,Math.round((distanzaPunto18*quantitàLivelli)/distanzaMassima));

        float distanzaPunto17 = getDistance(point_17.getX(), point_17.getY(),point_17.getZ(),point_0.getX(),point_0.getY(),point_0.getZ());
        mappaLivelli.put(17,Math.round((distanzaPunto17*quantitàLivelli)/distanzaMassima));


        return mappaLivelli;
    }
}
