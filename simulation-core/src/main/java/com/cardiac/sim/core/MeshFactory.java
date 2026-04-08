package com.cardiac.sim.core;

import java.util.ArrayList;
import java.util.List;

public final class MeshFactory {

    private MeshFactory() {
    }

    /**
     * Generates a heart-ish ellipsoid with a subtle notch at the top and a pointed apex.
     */
    public static Mesh createHeartLikeMesh(int latSegments, int lonSegments, double rx, double ry, double rz) {
        List<Vertex> vertices = new ArrayList<>((latSegments + 1) * (lonSegments + 1));
        List<Triangle> triangles = new ArrayList<>(latSegments * lonSegments * 2);

        for (int lat = 0; lat <= latSegments; lat++) {
            double v = (double) lat / latSegments;
            double theta = Math.PI * v;
            double sinTheta = Math.sin(theta);
            double cosTheta = Math.cos(theta);

            for (int lon = 0; lon <= lonSegments; lon++) {
                double u = (double) lon / lonSegments;
                double phi = 2.0 * Math.PI * u;
                double sinPhi = Math.sin(phi);
                double cosPhi = Math.cos(phi);

                double notch = 1.0 - 0.18 * Math.exp(-Math.pow((theta - 0.55), 2) / 0.04) * Math.exp(-Math.pow(sinPhi, 2) / 0.15);
                double apexTaper = 1.0 - 0.23 * Math.pow(Math.max(0.0, cosTheta), 2);

                double x = rx * sinTheta * cosPhi * notch;
                double y = ry * cosTheta * apexTaper;
                double z = rz * sinTheta * sinPhi;

                Vector3 pos = new Vector3(x, y, z);
                Vector3 normal = new Vector3(x / (rx * rx), y / (ry * ry), z / (rz * rz)).normalizeInPlace();
                vertices.add(new Vertex(pos, normal));
            }
        }

        int row = lonSegments + 1;
        for (int lat = 0; lat < latSegments; lat++) {
            for (int lon = 0; lon < lonSegments; lon++) {
                int a = lat * row + lon;
                int b = a + 1;
                int c = (lat + 1) * row + lon;
                int d = c + 1;
                triangles.add(new Triangle(a, c, b));
                triangles.add(new Triangle(b, c, d));
            }
        }
        return new Mesh(vertices, triangles);
    }
}
