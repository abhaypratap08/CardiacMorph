package com.cardiac.sim.core;

public final class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3() {
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 set(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 addInPlace(Vector3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vector3 subInPlace(Vector3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Vector3 scaleInPlace(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        return this;
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 normalizeInPlace() {
        double len = length();
        if (len > 1e-9) {
            scaleInPlace(1.0 / len);
        }
        return this;
    }
}
