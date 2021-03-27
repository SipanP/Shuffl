package com.shufflteam.shuffl;

public class User {
    private final Point position;
    private final String username;

    public User(String username){
        position = new Point(0,0);
        this.username = username;
    }

    public Point getPosition() {
        return position;
    }

    public String getUsername() {
        return username;
    }
}
