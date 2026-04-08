package com.cardiac.sim.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MeshMath {

    private MeshMath() {
    }

    public static double[] centroid(float[] positions) {
        int count = positions.length / 3;
        double cx = 0.0;
        double cy = 0.0;
        double cz = 0.0;
        for (int i = 0; i < positions.length; i += 3) {
            cx += positions[i];
            cy += positions[i + 1];
            cz += positions[i + 2];
        }
        return new double[]{cx / count, cy / count, cz / count};
    }

    /**
     * Uses signed tetrahedron volume sum from origin: V = sum(dot(a, cross(b,c))/6).
     */
    public static double volume(float[] positions, List<Triangle> triangles) {
        double vol = 0.0;
        for (Triangle t : triangles) {
            int i1 = t.v1() * 3;
            int i2 = t.v2() * 3;
            int i3 = t.v3() * 3;
            double ax = positions[i1];
            double ay = positions[i1 + 1];
            double az = positions[i1 + 2];
            double bx = positions[i2];
            double by = positions[i2 + 1];
            double bz = positions[i2 + 2];
            double cx = positions[i3];
            double cy = positions[i3 + 1];
            double cz = positions[i3 + 2];

            double crossX = by * cz - bz * cy;
            double crossY = bz * cx - bx * cz;
            double crossZ = bx * cy - by * cx;
            vol += (ax * crossX + ay * crossY + az * crossZ) / 6.0;
        }
        return Math.abs(vol);
    }

    public static int[][] buildAdjacency(int vertexCount, List<Triangle> triangles) {
        List<Set<Integer>> neighbors = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            neighbors.add(new HashSet<>());
        }
        for (Triangle t : triangles) {
            addEdge(neighbors, t.v1(), t.v2());
            addEdge(neighbors, t.v2(), t.v3());
            addEdge(neighbors, t.v3(), t.v1());
        }

        int[][] adjacency = new int[vertexCount][];
        for (int i = 0; i < vertexCount; i++) {
            Set<Integer> s = neighbors.get(i);
            int[] row = new int[s.size()];
            int idx = 0;
            for (int n : s) {
                row[idx++] = n;
            }
            adjacency[i] = row;
        }
        return adjacency;
    }

    private static void addEdge(List<Set<Integer>> neighbors, int a, int b) {
        neighbors.get(a).add(b);
        neighbors.get(b).add(a);
    }
}
