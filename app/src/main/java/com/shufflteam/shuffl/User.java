package com.shufflteam.shuffl;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class User extends AppCompatActivity implements View.OnTouchListener {

    private final Point position;
    private final String username;
    private float dX, dY;
    private Button ball;
    private View ballView;

    public User(String username){
        position = new Point(0,0);
        this.username = username;

        ballView = (View) findViewById(R.id.ballView);
        ball = (Button) findViewById(R.id.user1);
        ball.setOnTouchListener(this);
    }

    public Point getPosition() {
        return position;
    }

    public String getUsername() {
        return username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        System.out.println("Created");
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        System.out.println("Clicked");

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                view.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                position.setTo((event.getRawX() + dX),(event.getRawY() + dY));
                updateVolume();
                break;
            default:
                return false;
        }
        return true;
    }

    public void updateVolume(){
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        // Takes distance to speaker / total possible distance
        double screenRatio = position.distanceTo(5);
        // Sets volume to MAXVOLUME * the above ratio (NEED CONSTANTS)
        // STREAM_MUSIC is dependant on the kind of audio used (If a call, use RING or something)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.round(screenRatio * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)),0);
    }
}
