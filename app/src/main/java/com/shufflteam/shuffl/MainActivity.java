package com.shufflteam.shuffl;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.RetrofitError;


public class MainActivity extends AppCompatActivity {


    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    private static final String CLIENT_ID = "2e42d37ff4024cffa8b868e7a30061ca";
    private static final String REDIRECT_URI = "com.shufflteam.shuffl://callback";
    public static String currentRoomId;
    static SpotifyService spotify;
    private SpotifyAppRemote mSpotifyAppRemote;

    public static String joinRoom() {
        return "6060672f9bab6800153ba661";
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, "com.shufflteam.shuffl://callback")
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

//    private void preparePlaylist(){
//        PlaylistCard playlistCard = new PlaylistCard("Test 1", R.drawable.playlist_image);
//        playlistCards.add(playlistCard);
//        playlistCard = new PlaylistCard("Test 2", R.drawable.playlist_image);
//        playlistCards.add(playlistCard);
//        playlistCard = new PlaylistCard("Test 3", R.drawable.playlist_image);
//        playlistCards.add(playlistCard);
//        playlistCard = new PlaylistCard("Test 4", R.drawable.playlist_image);
//        playlistCards.add(playlistCard);
//        playlistCard = new PlaylistCard("Test 5", R.drawable.playlist_image);
//        playlistCards.add(playlistCard);
//        playlistCard = new PlaylistCard("Test 6", R.drawable.playlist_image);
//        playlistCards.add(playlistCard);
//        playlistCard = new PlaylistCard("Test 7", R.drawable.playlist_image);
//        playlistCards.add(playlistCard);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (response.getError() != null && response.getError().isEmpty()) {
            System.out.println("Error");
//            setResponse(response.getError());
        }
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            String mAccessToken = response.getAccessToken();
            System.out.println(mAccessToken);

            SpotifyApi spotifyApi = new SpotifyApi();

            if (mAccessToken != null) {
                spotifyApi.setAccessToken(mAccessToken);
            } else {
                System.out.println("No valid access token");
            }
            spotify = spotifyApi.getService();


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            List<PlaylistCard> playlistCards = new ArrayList<>();

            try {
                for (PlaylistTrack track : spotify.getPlaylistTracks("225dv6jfkmgoylbeqvjatv3sy", "5Jf7ydhHna8Xt75Wzbk5nL").items) {
                    System.out.println(track.track.name);
                }
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                // Handle error
            }


            String currentUserID = null;
            try {
                currentUserID = spotify.getMe().id;
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                // Handle error
            }
            try {
                Pager<PlaylistSimple> playlistPager = spotify.getPlaylists(currentUserID);
                for (PlaylistSimple playlist : playlistPager.items) {

                    Bitmap x;


                    HttpURLConnection connection = (HttpURLConnection) new URL(playlist.images.get(0).url).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();

                    x = BitmapFactory.decodeStream(input);
                    Drawable pic = new BitmapDrawable(Resources.getSystem(), x);

                    PlaylistCard playlistCard = new PlaylistCard(playlist.name, pic);
                    playlistCards.add(playlistCard);
                    System.out.println(playlist.name);
                }
            } catch (RetrofitError | MalformedURLException error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError((RetrofitError) error);
                // Handle error
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Set up the playlist cards.

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(playlistCards);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
//        recyclerAdapter.setOnItemClickListener(new ClickListener<Playlist>(){
//            @Override
//            public void onItemClick(Playlist data) {
//                startActivity(new Intent(MainActivity.this, RoomActivity.class));
//            }
//        });
            recyclerView.setAdapter(recyclerAdapter);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to Spotify API.
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {
        // Play a playlist
        try {
            mSpotifyAppRemote.getPlayerApi().play(spotify.getPlaylistTracks("spotify", "37i9dQZF1DX0XUsuxWHRQd").items.get(0).track.uri);
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            // Handle error
        }

        System.out.println("Testing");
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });


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
                            System.out.println(((JSONObject) obj.get(i)).get("_id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }, error -> System.out.println("That didn't work!"));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


        JSONObject postData = new JSONObject();
        try {
            postData.put("roomType", "Jazz");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, System.out::println, Throwable::printStackTrace);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }
}