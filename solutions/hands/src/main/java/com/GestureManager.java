package com;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.hands.gesture.IHandGesture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GestureManager {

    HashMap<IHandGesture, ArrayList<TimedAction>> gestureActionsMap;
    ActionExecutor actionExecutor;

    public GestureManager() {
        gestureActionsMap = new HashMap<IHandGesture, ArrayList<TimedAction>>();
        actionExecutor = new ActionExecutor();
    }

    public void registerGestureAction(IHandGesture gesture, TimedAction action) {
        if (gestureActionsMap.containsKey(gesture)) {
            gestureActionsMap.get(gesture).add(action);
        } else {
            ArrayList<TimedAction> actions = new ArrayList<TimedAction>();
            actions.add(action);
            gestureActionsMap.put(gesture, actions);
        }
    }

    public void unregisterGestureAction(IHandGesture gesture, Runnable action) {
        if (gestureActionsMap.containsKey(gesture)) {
            gestureActionsMap.get(gesture).remove(action);
        }
    }

    public void unregisterAllGestureActions(IHandGesture gesture) {
        if (gestureActionsMap.containsKey(gesture)) {
            gestureActionsMap.remove(gesture);
        }
    }

    public void unregisterAllGestureActions() {
        gestureActionsMap.clear();
    }

    private void executeGestureActions(IHandGesture gesture) {
        if (gestureActionsMap.containsKey(gesture)) {
            for (TimedAction action : gestureActionsMap.get(gesture)) {
                this.actionExecutor.executeActionOnce(action);
            }
        }
    }

    public void checkGestures(List<LandmarkProto.LandmarkList> landmarkList) {
        for (IHandGesture gesture : gestureActionsMap.keySet()) {
            if (gesture.checkGesture(landmarkList)) {
                executeGestureActions(gesture);
            }
        }
    }
}
