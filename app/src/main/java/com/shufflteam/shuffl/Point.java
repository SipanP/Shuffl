package com.shufflteam.shuffl;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setTo(int x, int y){
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
