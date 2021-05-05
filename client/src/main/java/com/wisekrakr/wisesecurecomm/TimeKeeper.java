package com.wisekrakr.wisesecurecomm;

public abstract class TimeKeeper {
    private static double startTime;
    private static double endTime;
    private static double elapsedTime;

    public static double getStartTime() {
        return startTime;
    }

    public static void setStartTime(double startTime) {
        TimeKeeper.startTime = startTime;
    }

    public static double getEndTime() {
        return endTime;
    }

    public static void setEndTime(double endTime) {
        TimeKeeper.endTime = endTime;
    }

    public static double getElapsedTime() {
        return elapsedTime;
    }

    public static void setElapsedTime(double elapsedTime) {
        TimeKeeper.elapsedTime = elapsedTime;
    }
}
