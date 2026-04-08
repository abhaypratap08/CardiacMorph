package com.cardiac.sim.fx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;

public final class ECGCanvasView {
    private final Canvas canvas;
    private final double[] samples;
    private int cursor = 0;

    public ECGCanvasView(double width, double height, int sampleCount) {
        this.canvas = new Canvas(width, height);
        this.samples = new double[sampleCount];
    }

    public void push(double heartbeat) {
        samples[cursor] = heartbeat;
        cursor = (cursor + 1) % samples.length;
    }

    public void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.setFill(Color.web("#111319"));
        gc.fillRect(0, 0, w, h);

        gc.setStroke(Color.web("#8b93a8"));
        gc.strokeLine(0, h / 2.0, w, h / 2.0);

        gc.setStroke(Color.web("#6aff9b"));
        gc.setLineWidth(1.8);

        double dx = w / (samples.length - 1.0);
        int idx = cursor;
        double prevX = 0;
        double prevY = map(samples[idx], h);
        for (int i = 1; i < samples.length; i++) {
            idx = (idx + 1) % samples.length;
            double x = i * dx;
            double y = map(samples[idx], h);
            gc.strokeLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }

    private double map(double v, double h) {
        return h * 0.5 - v * h * 0.28;
    }

    public Canvas node() {
        return canvas;
    }

    public void clear() {
        Arrays.fill(samples, 0.0);
    }
}
