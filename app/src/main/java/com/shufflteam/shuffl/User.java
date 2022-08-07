package com.shufflteam.shuffl;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.RelativeLayout;

public class User {

    private final Point position;
    private final String username;

    public User(String username, Button ball) {
        this(0, 0, username, ball);
    }

    public User(float x, float y, String username, Button ball) {
        position = new Point(x, y);
        this.username = username;
        ball.setText(getInitial());
        float scale = ball.getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (50 * scale + 0.5f);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(pixels, pixels);
        rl.leftMargin = (int) x;
        rl.topMargin = (int) y;
        ball.setLayoutParams(rl);
        ball.setBackgroundResource(R.drawable.round_button);
        ball.setGravity(Gravity.CENTER);
        ball.setTextColor(Color.WHITE);
        ball.setTextSize(18);
    }

    public Point getPosition() {
        return position;
    }

    public String getInitial() {
        return username.substring(0, 1).toUpperCase();
    }
}
