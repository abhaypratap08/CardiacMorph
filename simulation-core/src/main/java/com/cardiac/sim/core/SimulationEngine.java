package com.cardiac.sim.core;

import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Stateful simulation loop data: arrays are preallocated and mutated in place.
 */
public final class SimulationEngine {
    private final Mesh mesh;
    private final List<Triangle> triangles;
    private final HeartbeatFunction heartbeatFunction;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final float[] basePositions;
    private final float[] positions;
    private final float[] scratch;
    private final float[] noisePhase;
    private final int[][] adjacency;

    private final double targetVolume;
    private volatile double lastHeartbeat;
    private volatile double lastVolume;

    private volatile double deformationAmplitude = 0.16;
    private volatile double smoothingAlpha = 0.16;

    public SimulationEngine(Mesh mesh, double initialFrequencyHz) {
        this.mesh = mesh;
        this.triangles = mesh.triangles();
        this.heartbeatFunction = new HeartbeatFunction(initialFrequencyHz);

        int vertexCount = mesh.vertices().size();
        this.basePositions = new float[vertexCount * 3];
        this.positions = new float[vertexCount * 3];
        this.scratch = new float[vertexCount * 3];
        this.noisePhase = new float[vertexCount];

        SplittableRandom random = new SplittableRandom(42);
        for (int i = 0; i < vertexCount; i++) {
            Vertex v = mesh.vertices().get(i);
            int p = i * 3;
            basePositions[p] = (float) v.position.x;
            basePositions[p + 1] = (float) v.position.y;
            basePositions[p + 2] = (float) v.position.z;
            positions[p] = basePositions[p];
            positions[p + 1] = basePositions[p + 1];
            positions[p + 2] = basePositions[p + 2];
            noisePhase[i] = (float) (random.nextDouble() * Math.PI * 2.0);
        }

        this.adjacency = MeshMath.buildAdjacency(vertexCount, triangles);
        this.targetVolume = MeshMath.volume(basePositions, triangles);
        this.lastVolume = targetVolume;
    }

    public Mesh mesh() {
        return mesh;
    }

    public void update(double tSeconds, double dtSeconds) {
        lock.writeLock().lock();
        try {
            double beat = heartbeatFunction.sample(tSeconds);
            lastHeartbeat = beat;
            double beatFactor = Math.max(0.0, beat);
            double[] c = MeshMath.centroid(basePositions);

            for (int i = 0; i < noisePhase.length; i++) {
                int p = i * 3;
                double bx = basePositions[p];
                double by = basePositions[p + 1];
                double bz = basePositions[p + 2];

                double dx = bx - c[0];
                double dy = by - c[1];
                double dz = bz - c[2];

                double phase = noisePhase[i] + tSeconds * 2.7;
                double localNoise = 0.9 + 0.12 * Math.sin(phase) + 0.06 * Math.sin(phase * 2.2);
                double contraction = 1.0 - deformationAmplitude * beatFactor * localNoise;

                positions[p] = (float) (c[0] + dx * contraction);
                positions[p + 1] = (float) (c[1] + dy * contraction);
                positions[p + 2] = (float) (c[2] + dz * contraction);
            }

            laplacianSmooth();
            preserveVolume();
            lastVolume = MeshMath.volume(positions, triangles);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void laplacianSmooth() {
        for (int i = 0; i < adjacency.length; i++) {
            int p = i * 3;
            int[] n = adjacency[i];
            double ax = 0.0;
            double ay = 0.0;
            double az = 0.0;
            for (int v : n) {
                int idx = v * 3;
                ax += positions[idx];
                ay += positions[idx + 1];
                az += positions[idx + 2];
            }
            double inv = n.length == 0 ? 0.0 : 1.0 / n.length;
            ax *= inv;
            ay *= inv;
            az *= inv;

            scratch[p] = (float) (positions[p] * (1.0 - smoothingAlpha) + ax * smoothingAlpha);
            scratch[p + 1] = (float) (positions[p + 1] * (1.0 - smoothingAlpha) + ay * smoothingAlpha);
            scratch[p + 2] = (float) (positions[p + 2] * (1.0 - smoothingAlpha) + az * smoothingAlpha);
        }

        System.arraycopy(scratch, 0, positions, 0, positions.length);
    }

    private void preserveVolume() {
        double currentVolume = MeshMath.volume(positions, triangles);
        if (currentVolume < 1e-8) {
            return;
        }
        double scale = Math.cbrt(targetVolume / currentVolume);
        double[] c = MeshMath.centroid(positions);
        for (int i = 0; i < positions.length; i += 3) {
            positions[i] = (float) (c[0] + (positions[i] - c[0]) * scale);
            positions[i + 1] = (float) (c[1] + (positions[i + 1] - c[1]) * scale);
            positions[i + 2] = (float) (c[2] + (positions[i + 2] - c[2]) * scale);
        }
    }

    public SimulationSnapshot snapshot() {
        lock.readLock().lock();
        try {
            float[] copy = new float[positions.length];
            System.arraycopy(positions, 0, copy, 0, positions.length);
            return new SimulationSnapshot(copy, lastHeartbeat, heartbeatFunction.getFrequencyHz(), lastVolume);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void copyPositions(float[] target) {
        lock.readLock().lock();
        try {
            System.arraycopy(positions, 0, target, 0, positions.length);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setFrequencyHz(double frequencyHz) {
        heartbeatFunction.setFrequencyHz(Math.max(1.0, Math.min(1.5, frequencyHz)));
    }

    public void setDeformationAmplitude(double deformationAmplitude) {
        this.deformationAmplitude = Math.max(0.05, Math.min(0.35, deformationAmplitude));
    }

    public double getFrequencyHz() {
        return heartbeatFunction.getFrequencyHz();
    }

    public double getLastHeartbeat() {
        return lastHeartbeat;
    }
}
