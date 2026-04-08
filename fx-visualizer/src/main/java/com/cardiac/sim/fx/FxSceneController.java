package com.cardiac.sim.fx;

import com.cardiac.sim.core.SimulationEngine;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public final class FxSceneController {
    private final SimulationEngine engine;
    private final float[] fxPoints;
    private final HeartMeshView heartMeshView;
    private final EnergyParticleSystem particles;
    private final ECGCanvasView ecg;

    private final Rotate rotateX = new Rotate(-18, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);

    private double lastMouseX;
    private double lastMouseY;

    public FxSceneController(SimulationEngine engine) {
        this.engine = engine;
        this.fxPoints = new float[engine.mesh().vertices().size() * 3];
        this.heartMeshView = new HeartMeshView(engine.mesh());
        this.particles = new EnergyParticleSystem(engine.mesh().vertices().size(), 250);
        this.ecg = new ECGCanvasView(1260, 180, 360);
    }

    public Scene buildScene(int width, int height) {
        Group world = new Group(heartMeshView.node(), particles.node());
        world.getTransforms().addAll(rotateX, rotateY);

        PointLight key = new PointLight(Color.WHITE);
        key.setTranslateX(-160);
        key.setTranslateY(-150);
        key.setTranslateZ(-220);

        PointLight fill = new PointLight(Color.web("#ff7ba3"));
        fill.setTranslateX(220);
        fill.setTranslateY(60);
        fill.setTranslateZ(-100);

        Group root3d = new Group(world, key, fill);
        SubScene subScene = new SubScene(root3d, width, height - 200, true, null);
        subScene.setFill(Color.web("#1b1f2a"));

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-520);
        camera.setNearClip(0.1);
        camera.setFarClip(3000);
        subScene.setCamera(camera);

        BorderPane pane = new BorderPane();
        pane.setCenter(subScene);
        pane.setBottom(ecg.node());

        Scene scene = new Scene(pane, width, height, true);
        addInteraction(scene);
        startLoop();
        return scene;
    }

    private void startLoop() {
        final long[] lastNs = {0};
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNs[0] == 0) {
                    lastNs[0] = now;
                    return;
                }
                double dt = (now - lastNs[0]) / 1_000_000_000.0;
                double t = now / 1_000_000_000.0;
                lastNs[0] = now;

                // Physics pipeline @ ~60FPS: heartbeat -> deformation -> smoothing -> volume constraint.
                engine.update(t, dt);
                engine.copyPositions(fxPoints);
                heartMeshView.updatePoints(fxPoints);
                particles.update(fxPoints, t);

                ecg.push(engine.getLastHeartbeat());
                ecg.render();
            }
        };
        timer.start();
    }

    private void addInteraction(Scene scene) {
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            double dx = e.getSceneX() - lastMouseX;
            double dy = e.getSceneY() - lastMouseY;
            rotateY.setAngle(rotateY.getAngle() + dx * 0.35);
            rotateX.setAngle(rotateX.getAngle() - dy * 0.35);
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });
    }
}
