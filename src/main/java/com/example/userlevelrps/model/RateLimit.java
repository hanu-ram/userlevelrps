package com.example.userlevelrps.model;

public class RateLimit {
    private final int limit;
    private final int periodMillis;
    private long lastResetTime;
    private int counter;

    public RateLimit(int limit, int periodMillis) {
        this.limit = limit;
        this.periodMillis = periodMillis;
        this.lastResetTime = System.currentTimeMillis();
        this.counter = 0;
    }
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        if (now - lastResetTime > periodMillis) {
            lastResetTime = now;
            counter = 0;
        }

        if (counter < limit) {
            counter++;
            return true;
        }
        return false;
    }
}
