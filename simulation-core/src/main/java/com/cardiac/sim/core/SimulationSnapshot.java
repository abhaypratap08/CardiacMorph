package com.cardiac.sim.core;

public record SimulationSnapshot(float[] positions, double heartbeat, double frequencyHz, double volume) {
}
