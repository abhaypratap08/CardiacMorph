package com.cardiac.sim.fx;

import com.cardiac.sim.core.SimulationEngine;
import javafx.application.Application;
import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public final class FxVisualizerLauncher {
    private static final AtomicBoolean started = new AtomicBoolean(false);
    private static final CountDownLatch initLatch = new CountDownLatch(1);
    private static volatile SimulationEngine engine;
    private static volatile boolean fxAvailable = false;

    private FxVisualizerLauncher() {
    }

    public static void ensureStarted(SimulationEngine simulationEngine) {
        engine = simulationEngine;
        if (started.compareAndSet(false, true)) {
            Thread fxThread = new Thread(() -> {
                try {
                    Application.launch(FxVisualizerApp.class);
                } catch (Throwable t) {
                    System.err.println("JavaFX startup failed (running API without visualizer): " + t.getMessage());
                    initLatch.countDown();
                }
            }, "javafx-ui");
            fxThread.setDaemon(true);
            fxThread.start();
        }
        try {
            initLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static SimulationEngine engine() {
        return engine;
    }

    static void markReady() {
        fxAvailable = true;
        initLatch.countDown();
    }

    public static void runOnFxThread(Runnable runnable) {
        if (!fxAvailable) {
            runnable.run();
            return;
        }
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }
}
