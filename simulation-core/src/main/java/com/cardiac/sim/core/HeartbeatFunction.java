package com.cardiac.sim.core;

public final class HeartbeatFunction {
    private volatile double frequencyHz;

    public HeartbeatFunction(double frequencyHz) {
        this.frequencyHz = frequencyHz;
    }

    public double sample(double tSeconds) {
        double omega = 2.0 * Math.PI * frequencyHz;
        return Math.sin(omega * tSeconds) + 0.3 * Math.sin(3.0 * omega * tSeconds);
    }

    public double getFrequencyHz() {
        return frequencyHz;
    }

    public void setFrequencyHz(double frequencyHz) {
        this.frequencyHz = frequencyHz;
    }
}
