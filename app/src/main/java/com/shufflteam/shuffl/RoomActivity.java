package com.shufflteam.shuffl;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomActivity extends AppCompatActivity implements View.OnTouchListener {

    private float dX, dY;
    private Button ball;
    private RelativeLayout ballBox;
    private User currentUser;
    private List<User> otherUsers = new ArrayList<>();
    int numOfOtherUsers = 2;
    private final Random rand = new Random();
    private boolean setup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!setup) {
            setup = true;
            ballBox = (RelativeLayout) findViewById(R.id.ballBox);
            ball = (Button) findViewById(R.id.currentUser);
            ball.setOnTouchListener(this);
            currentUser = new User(ball.getX(), ball.getY(), "vincent", ball);

            for (int i = 0; i < numOfOtherUsers; i++) {
                Button otherBall = new Button(this);
                otherBall.setClickable(false);
                int randX = rand.nextInt(ballBox.getWidth() - ball.getWidth());
                int randY = rand.nextInt(ballBox.getHeight() - ball.getHeight());
                otherUsers.add(new User(randX, randY, String.valueOf((char) ('A' + i)), otherBall));
                Drawable drawable = getDrawable(R.drawable.round_button);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, Color.argb(255, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
                otherBall.setBackground(drawable);
                ballBox.addView(otherBall);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                ball.setPressed(true);
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                float newX = Math.min(Math.max(event.getRawX() + dX, 0), ballBox.getWidth() - ball.getWidth());
                float newY = Math.min(Math.max(event.getRawY() + dY, 0), ballBox.getHeight() - ball.getHeight());

                view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start();
                currentUser.getPosition().setTo(newX, newY);
                updateVolume();
                break;
            default:
                ball.setPressed(false);
                return false;
        }
        return true;
    }

    public void updateVolume(){
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        // Takes distance to speaker / total possible distance
        double screenRatio = currentUser.getPosition().distanceTo(ballBox.getHeight()) / ballBox.getHeight();
        // Sets volume to MAXVOLUME * the above ratio (NEED CONSTANTS)
        // STREAM_MUSIC is dependant on the kind of audio used (If a call, use RING or something)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.floor(screenRatio * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)),0);
    }
}
