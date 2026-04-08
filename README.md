# CardiacMorph Real-Time Heart Pump Simulation

A multi-module Java system combining:
- **simulation-core**: mesh + cardiac deformation physics
- **fx-visualizer**: JavaFX 3D rendering (`TriangleMesh`) + energy particles + live ECG
- **api-layer**: Spring Boot API to inspect and tune simulation parameters at runtime

## Project Structure

```text
CardiacMorph/
├── pom.xml
├── README.md
├── simulation-core/
│   └── src/main/java/com/cardiac/sim/core/
│       ├── Vector3.java
│       ├── Vertex.java
│       ├── Triangle.java
│       ├── Mesh.java
│       ├── MeshFactory.java
│       ├── MeshMath.java
│       ├── HeartbeatFunction.java
│       ├── SimulationEngine.java
│       └── SimulationSnapshot.java
├── fx-visualizer/
│   └── src/main/java/com/cardiac/sim/fx/
│       ├── FxVisualizerLauncher.java
│       ├── FxVisualizerApp.java
│       ├── FxSceneController.java
│       ├── HeartMeshView.java
│       ├── EnergyParticleSystem.java
│       └── ECGCanvasView.java
└── api-layer/
    └── src/main/java/com/cardiac/sim/api/
        ├── CardiacApiApplication.java
        └── SimulationController.java
```

## Simulation Pipeline (per frame @ ~60 FPS)

`heartbeat -> radial deformation -> Laplacian smoothing -> volume preservation -> rendering`

### Heartbeat
Uses:

`f(t) = sin(ωt) + 0.3 sin(3ωt)` where `ω = 2πf` and `f` is clamped to **[1.0, 1.5] Hz**.

### Deformation
- Contraction is radial towards centroid.
- Vertex-local sinusoidal noise gives non-uniform organic movement.

### Smoothing
- One-pass Laplacian smoothing with precomputed mesh adjacency.

### Volume Constraint
- Volume computed via signed tetrahedron summation over triangles.
- Uniform correction scale `s = cbrt(targetVolume/currentVolume)` applied around current centroid.

## Rendering

- JavaFX `TriangleMesh` updated in place (`ObservableFloatArray.set(...)`).
- `PhongMaterial` in red/pink tones.
- Two-point light setup.
- Mouse drag camera/object rotation controls.
- Energy particles: glowing small spheres bound to sampled mesh vertices with procedural jitter.
- ECG strip: canvas-based live waveform from the exact same heartbeat signal.

## Spring API

Base URL: `http://localhost:8080/api/simulation`

- `GET /state`
  - Returns heartbeat, frequency, volume, vertex count.
- `POST /frequency`
  - Body: `{ "frequencyHz": 1.3 }`
- `POST /amplitude`
  - Body: `{ "amplitude": 0.18 }`

Spring-to-JavaFX updates are marshaled with `Platform.runLater(...)` via `FxVisualizerLauncher.runOnFxThread(...)`.

## Run

### 1) Build all modules

```bash
mvn -pl api-layer -am package
```

### 2) Run API + JavaFX visualizer together

```bash
mvn -pl api-layer spring-boot:run
```

When Spring starts, it also launches JavaFX and the heart starts pumping in real time.

## Notes on Performance

- Frame loop mutates preallocated arrays (`float[]`) to reduce GC churn.
- Adjacency and per-vertex random phases are precomputed once.
- Mesh updates write directly into JavaFX mesh buffers.
