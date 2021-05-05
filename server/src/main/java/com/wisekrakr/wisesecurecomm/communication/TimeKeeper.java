package com.wisekrakr.wisesecurecomm.communication;

public abstract class TimeKeeper {
    private static long startTime;
    private static long endTime;
    private static long elapsedTime;

    public static long getStartTime() {
        return startTime;
    }

    public static void setStartTime(long startTime) {
        TimeKeeper.startTime = startTime;
    }

    public static long getEndTime() {
        return endTime;
    }

    public static void setEndTime(long endTime) {
        TimeKeeper.endTime = endTime;
    }

    public static long getElapsedTime() {
        return elapsedTime;
    }

    public static void setElapsedTime(long elapsedTime) {
        TimeKeeper.elapsedTime = elapsedTime;
    }
}
