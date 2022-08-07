package com.shufflteam.shuffl;

import static com.shufflteam.shuffl.MainActivity.spotify;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kaaes.spotify.webapi.android.SpotifyError;
import retrofit.RetrofitError;

public class RoomActivity extends AppCompatActivity implements View.OnTouchListener {

    private TextView songTitle;
    private RelativeLayout ballBox;
    private Button ball;
    private User currentUser;
    private final List<User> otherUsers = new ArrayList<>();
    private float dX, dY;
    int numOfOtherUsers = 2;
    private final Random rand = new Random();
    private boolean setup = false;
    String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        songTitle = findViewById(R.id.songTitleTextView);
        songTitle.setText("Abc");
        roomId = getIntent().getStringExtra("roomId");
        getCurrentSong();
    }


    private String getCurrentSong() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://shuffl-backend.herokuapp.com/api/rooms";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {


                    System.out.println("Response is: " + response);
                    try {
                        JSONArray obj = new JSONArray(response);
                        for (int i = 0; i < obj.length(); i++) {
                            if (((JSONObject) obj.get(i)).get("_id").equals(roomId)) {
                                System.out.println(((JSONObject) obj.get(i)).get("songPlaying"));
                                try {
                                    songTitle.setText(spotify.getTrack(((JSONObject) obj.get(i)).getString("currentSong")).name);
                                } catch (RetrofitError error) {
                                    SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                                    // Handle error
                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }, error -> System.out.println("That didn't work!"));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return "";
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!setup) {
            setup = true;

            ballBox = findViewById(R.id.ballBox);
            ball = findViewById(R.id.currentUser);
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

    public void updateVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // Takes distance to speaker / total possible distance
        double screenRatio = currentUser.getPosition().distanceTo(ballBox.getHeight()) / ballBox.getHeight();
        // Sets volume to MAXVOLUME * the above ratio (NEED CONSTANTS)
        // STREAM_MUSIC is dependant on the kind of audio used (If a call, use RING or something)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.floor(screenRatio * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
    }
}
