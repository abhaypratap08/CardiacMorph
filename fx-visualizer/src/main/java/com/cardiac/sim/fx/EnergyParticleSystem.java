package com.cardiac.sim.fx;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import java.util.SplittableRandom;

public final class EnergyParticleSystem {
    private final Group group = new Group();
    private final int[] boundVertices;
    private final Sphere[] particles;
    private final float[] phase;

    public EnergyParticleSystem(int vertexCount, int particleCount) {
        this.boundVertices = new int[particleCount];
        this.particles = new Sphere[particleCount];
        this.phase = new float[particleCount];

        SplittableRandom random = new SplittableRandom(77);
        PhongMaterial glow = new PhongMaterial(Color.web("#ffb3c8"));
        glow.setSpecularColor(Color.WHITE);
        glow.setSelfIlluminationMap(null);

        for (int i = 0; i < particleCount; i++) {
            boundVertices[i] = random.nextInt(vertexCount);
            phase[i] = (float) (random.nextDouble() * Math.PI * 2.0);
            Sphere s = new Sphere(1.2);
            s.setMaterial(glow);
            s.setOpacity(0.82);
            particles[i] = s;
            group.getChildren().add(s);
        }
    }

    public void update(float[] positions, double t) {
        for (int i = 0; i < particles.length; i++) {
            int v = boundVertices[i] * 3;
            double wiggle = 2.0 + Math.sin(t * 3.5 + phase[i]) * 1.1;
            particles[i].setTranslateX(positions[v] + wiggle * Math.cos(phase[i] + t));
            particles[i].setTranslateY(positions[v + 1] + wiggle * Math.sin(phase[i] + t * 1.2));
            particles[i].setTranslateZ(positions[v + 2] + wiggle * Math.cos(phase[i] + t * 0.6));
        }
    }

    public Group node() {
        return group;
    }
}
