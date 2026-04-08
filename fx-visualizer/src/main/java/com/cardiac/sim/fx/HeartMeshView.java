package com.cardiac.sim.fx;

import com.cardiac.sim.core.Mesh;
import com.cardiac.sim.core.Triangle;
import javafx.collections.ObservableFloatArray;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public final class HeartMeshView {
    private final TriangleMesh triangleMesh;
    private final MeshView meshView;

    public HeartMeshView(Mesh mesh) {
        this.triangleMesh = new TriangleMesh();

        float[] points = new float[mesh.vertices().size() * 3];
        for (int i = 0; i < mesh.vertices().size(); i++) {
            int p = i * 3;
            points[p] = (float) mesh.vertices().get(i).position.x;
            points[p + 1] = (float) mesh.vertices().get(i).position.y;
            points[p + 2] = (float) mesh.vertices().get(i).position.z;
        }
        triangleMesh.getPoints().setAll(points);
        triangleMesh.getTexCoords().setAll(0, 0);

        int[] faces = new int[mesh.triangles().size() * 6];
        for (int i = 0; i < mesh.triangles().size(); i++) {
            Triangle t = mesh.triangles().get(i);
            int f = i * 6;
            faces[f] = t.v1();
            faces[f + 1] = 0;
            faces[f + 2] = t.v2();
            faces[f + 3] = 0;
            faces[f + 4] = t.v3();
            faces[f + 5] = 0;
        }
        triangleMesh.getFaces().setAll(faces);

        this.meshView = new MeshView(triangleMesh);
        this.meshView.setDrawMode(DrawMode.FILL);
        this.meshView.setCullFace(CullFace.BACK);

        PhongMaterial material = new PhongMaterial(Color.web("#d74364"));
        material.setSpecularColor(Color.web("#ff94b0"));
        material.setSpecularPower(32);
        this.meshView.setMaterial(material);
    }

    public void updatePoints(float[] updatedPositions) {
        ObservableFloatArray points = triangleMesh.getPoints();
        points.set(0, updatedPositions, 0, updatedPositions.length);
    }

    public MeshView node() {
        return meshView;
    }
}
