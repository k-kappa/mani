package com;

public class TimedAction implements Runnable {

    private final Runnable action;
    private final long timer;

    public TimedAction(Runnable action, long timer) {
        this.action = action;
        this.timer = timer;
    }

    public void run() {
        this.action.run();
    }

    public long getTimer() {
        return this.timer;
    }
}
