package com.shufflteam.shuffl;

public class User {

    private final Point position;
    private final String username;


    public User(String username) {
        this(0, 0, username);
    }

    public User(float x, float y, String username) {
        position = new Point(x, y);
        this.username = username;
    }

    public Point getPosition() {
        return position;
    }
}
