package com.shufflteam.shuffl;

public class Point {
    private float x;
    private float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public double distanceTo(int verticalLine) {
        return Math.abs(y - verticalLine);
    }
}
