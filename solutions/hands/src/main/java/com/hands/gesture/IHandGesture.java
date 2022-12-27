package com.hands.gesture;

import com.google.mediapipe.formats.proto.LandmarkProto;

import java.util.List;

public interface IHandGesture {

    public boolean checkGesture(List<LandmarkProto.LandmarkList> landmarkList);

    public String getName();

    public int getGestureId();

    public GestureType getGestureType();
}
