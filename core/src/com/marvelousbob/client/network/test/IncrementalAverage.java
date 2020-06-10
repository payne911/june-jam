package com.marvelousbob.client.network.test;

import lombok.Getter;

public class IncrementalAverage {

    @Getter
    private double average;
    private int counter;

    @Getter
    private long min, max;

    public IncrementalAverage(int current) {
        this.average = current;
        this.counter = 0;
        this.min = Long.MAX_VALUE;
        this.max = 0;
    }

    public IncrementalAverage() {
        this(0);
    }

    public void addToRunningAverage(long sentMsgTimestamp) {
        long delta = System.currentTimeMillis() - sentMsgTimestamp;
        evaluateMin(delta);
        evaluateMax(delta);
        average = average + (delta - average) / (double) ++counter;
        System.out.println(counter + " : " + delta);
    }

    public void evaluateMin(long num) {
        min = Math.min(min, num);
    }

    public void evaluateMax(long num) {
        max = Math.max(max, num);
    }

    @Override
    public String toString() {
        return ("""
                min: %d ms
                max: %d ms
                average: %.3f ms
                counter: %d""").formatted(min, max, average, counter);
    }
}
