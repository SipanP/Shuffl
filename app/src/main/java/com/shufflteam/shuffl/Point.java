package com.shufflteam.shuffl;

public class Point {
    private float x;
    private float y;

    public Point(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setTo(float x, float y){
        this.x = x;
        this.y = y;
    }

    public double distanceTo(Point that){
        return Math.sqrt(Math.pow(2,x - that.getX()) + Math.pow(2,y - that.getY()));
    }

    public double distanceTo(int verticalLine){
        return Math.abs(y-verticalLine);
    }
}
