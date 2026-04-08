package com.cardiac.sim.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxVisualizerApp extends Application {
    @Override
    public void start(Stage stage) {
        FxSceneController controller = new FxSceneController(FxVisualizerLauncher.engine());
        Scene scene = controller.buildScene(1280, 860);

        stage.setTitle("Cardiac Morph Simulation");
        stage.setScene(scene);
        stage.show();
        FxVisualizerLauncher.markReady();
    }
}
