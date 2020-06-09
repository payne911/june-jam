package com.marvelousbob.client.network.test;

import lombok.Getter;

public class IncrementalAverage {

    private double current;
    private int counter;

    @Getter
    private long min, max;

    public IncrementalAverage(int current) {
        this.current = current;
        this.counter = 1;
        this.min = 0;
        this.max = 0;
    }

    public IncrementalAverage() {
        this(0);
    }

    public double getAverage() {
        return current;
    }

    public void addToRunningAverage(long num) {
        evaluateMin(num);
        evaluateMax(num);
        current = current + (num - current) / (double) counter++;
    }

    public void evaluateMin(long num) {
        min = Math.min(min, num);
    }

    public void evaluateMax(long num) {
        max = Math.max(max, num);
    }

    @Override
    public String toString() {
        return "min: " + min
                + "\nmax: " + max
                + "\naverage: " + current
                + "\ncounter: " + counter;
    }
}
