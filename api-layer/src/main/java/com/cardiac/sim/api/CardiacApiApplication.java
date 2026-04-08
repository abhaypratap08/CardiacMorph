package com.cardiac.sim.api;

import com.cardiac.sim.core.Mesh;
import com.cardiac.sim.core.MeshFactory;
import com.cardiac.sim.core.SimulationEngine;
import com.cardiac.sim.fx.FxVisualizerLauncher;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CardiacApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardiacApiApplication.class, args);
    }

    @Bean
    public SimulationEngine simulationEngine() {
        Mesh mesh = MeshFactory.createHeartLikeMesh(36, 48, 85, 110, 75);
        return new SimulationEngine(mesh, 1.2);
    }

    @PostConstruct
    public void launchFx() {
        FxVisualizerLauncher.ensureStarted(simulationEngine());
    }
}
