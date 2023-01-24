package com;

import java.util.HashMap;

public class ActionExecutor {

    HashMap<TimedAction, Long> actionMap = new HashMap<TimedAction, Long>();

    public ActionExecutor() {

    }

    public void executeActionOnce(TimedAction action) {
        if (!actionMap.containsKey(action)) {
            actionMap.put(action, System.currentTimeMillis());
            action.run();
        } else {
            long lastExecution = actionMap.get(action);
            if (System.currentTimeMillis() - lastExecution > action.getTimer()) {
                actionMap.put(action, System.currentTimeMillis());
                action.run();
            }
        }
    }
}
