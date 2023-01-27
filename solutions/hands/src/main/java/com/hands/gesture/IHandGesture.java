package com.hands.gesture;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandsResult;

import java.util.List;

public interface IHandGesture {

    public boolean checkGesture(HandsResult handsResult);

    public String getName();

    public int getGestureId();

    public GestureType getGestureType();
}
