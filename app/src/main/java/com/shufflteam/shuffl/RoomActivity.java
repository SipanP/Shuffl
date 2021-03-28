package com.shufflteam.shuffl;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity implements View.OnTouchListener {

    private float dX, dY;
    private Button ball;
    private View ballView;
    private User currentUser;
    private List<User> otherUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        currentUser = new User("VL");
        ballView = (View) findViewById(R.id.ballView);
        ball = (Button) findViewById(R.id.user1);
        ball.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                float newX = Math.min(Math.max(event.getRawX() + dX, ballView.getLeft()), ballView.getRight());
                float newY = Math.min(Math.max(event.getRawY() + dY, ballView.getTop()), ballView.getBottom());
                System.out.println(newY);
                System.out.println(ballView.getBottom());

                view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start();
                currentUser.getPosition().setTo(newX, newY);
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
        double screenRatio = currentUser.getPosition().distanceTo(ballView.getTop()) / ballView.getHeight();
        // Sets volume to MAXVOLUME * the above ratio (NEED CONSTANTS)
        // STREAM_MUSIC is dependant on the kind of audio used (If a call, use RING or something)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.floor(screenRatio * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)),0);
    }
}
