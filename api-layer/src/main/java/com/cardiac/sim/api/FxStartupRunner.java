package com.cardiac.sim.api;

import com.cardiac.sim.core.SimulationEngine;
import com.cardiac.sim.fx.FxVisualizerLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Starts JavaFX once Spring context is fully initialized, avoiding bean init cycles.
 */
@Component
public class FxStartupRunner implements ApplicationRunner {
    private final SimulationEngine simulationEngine;

    public FxStartupRunner(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
    }

    @Override
    public void run(ApplicationArguments args) {
        FxVisualizerLauncher.ensureStarted(simulationEngine);
    }
}
