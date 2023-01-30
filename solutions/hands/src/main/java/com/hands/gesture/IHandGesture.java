package com.hands.gesture;

import com.google.mediapipe.solutions.hands.HandsResult;

public interface IHandGesture {

    boolean checkGesture(HandsResult handsResult);

}
