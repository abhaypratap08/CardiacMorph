package com.cardiac.sim.api;

import com.cardiac.sim.core.SimulationEngine;
import com.cardiac.sim.core.SimulationSnapshot;
import com.cardiac.sim.fx.FxVisualizerLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {
    private final SimulationEngine engine;

    public SimulationController(SimulationEngine engine) {
        this.engine = engine;
    }

    @GetMapping("/state")
    public Map<String, Object> state() {
        SimulationSnapshot s = engine.snapshot();
        return Map.of(
                "frequencyHz", s.frequencyHz(),
                "heartbeat", s.heartbeat(),
                "volume", s.volume(),
                "vertexCount", s.positions().length / 3
        );
    }

    @PostMapping("/frequency")
    public Map<String, Object> updateFrequency(@RequestBody Map<String, Double> body) {
        double requested = body.getOrDefault("frequencyHz", 1.2);
        FxVisualizerLauncher.runOnFxThread(() -> engine.setFrequencyHz(requested));
        return Map.of("frequencyHz", engine.getFrequencyHz());
    }

    @PostMapping("/amplitude")
    public Map<String, Object> updateAmplitude(@RequestBody Map<String, Double> body) {
        double requested = body.getOrDefault("amplitude", 0.16);
        FxVisualizerLauncher.runOnFxThread(() -> engine.setDeformationAmplitude(requested));
        return Map.of("amplitude", requested);
    }
}
