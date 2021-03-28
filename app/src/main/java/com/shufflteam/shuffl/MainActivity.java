package com.shufflteam.shuffl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.SavedTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
//import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {



    private static final String CLIENT_ID = "2e42d37ff4024cffa8b868e7a30061ca";
    private static final String REDIRECT_URI = "com.shufflteam.shuffl://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    private List<PlaylistCard> playlistCards;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;


    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    private String mAccessToken;

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, "com.shufflteam.shuffl://callback")
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    SpotifyService spotify;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (response.getError() != null && response.getError().isEmpty()) {
            System.out.println("Error");
//            setResponse(response.getError());
        }
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();
            System.out.println(mAccessToken);

            final String accessToken = mAccessToken;

            SpotifyApi spotifyApi = new SpotifyApi();

            if (accessToken != null) {
                spotifyApi.setAccessToken(accessToken);
            } else {
                System.out.println("No valid access token");
            }
            spotify = spotifyApi.getService();


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            playlistCards = new ArrayList<>();

            try {
                for (PlaylistTrack track : spotify.getPlaylistTracks("225dv6jfkmgoylbeqvjatv3sy", "5Jf7ydhHna8Xt75Wzbk5nL").items) {
                    System.out.println(track.track.name);
                }
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                // handle error
            }


            String currentUserID = null;
            try {
                currentUserID = spotify.getMe().id;
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                // handle error
            }
            try {
                Pager<PlaylistSimple> playlistPager = spotify.getPlaylists(currentUserID);
                for (PlaylistSimple playlist : playlistPager.items){
                    PlaylistCard playlistCard = new PlaylistCard(playlist.name, R.drawable.playlist_image);
                    playlistCards.add(playlistCard);
                    System.out.println(playlist.name);
                }
            } catch (RetrofitError error) {
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                // handle error
            }

            // Set up the playlist cards.

            recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
            recyclerAdapter = new RecyclerAdapter(playlistCards);
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

    private void preparePlaylist(){
        PlaylistCard playlistCard = new PlaylistCard("Test 1", R.drawable.playlist_image);
        playlistCards.add(playlistCard);
        playlistCard = new PlaylistCard("Test 2", R.drawable.playlist_image);
        playlistCards.add(playlistCard);
        playlistCard = new PlaylistCard("Test 3", R.drawable.playlist_image);
        playlistCards.add(playlistCard);
        playlistCard = new PlaylistCard("Test 4", R.drawable.playlist_image);
        playlistCards.add(playlistCard);
        playlistCard = new PlaylistCard("Test 5", R.drawable.playlist_image);
        playlistCards.add(playlistCard);
        playlistCard = new PlaylistCard("Test 6", R.drawable.playlist_image);
        playlistCards.add(playlistCard);
        playlistCard = new PlaylistCard("Test 7", R.drawable.playlist_image);
        playlistCards.add(playlistCard);
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
//            mSpotifyAppRemote.getPlayerApi().play(spotify.getPlaylistTracks("225dv6jfkmgoylbeqvjatv3sy", "5Jf7ydhHna8Xt75Wzbk5nL").items.get(0).track.uri);
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            // handle error
        }

        //mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
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
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        System.out.println("Response is: "+ response);
                        try {
                            JSONArray obj = new JSONArray(response);
                            for(int i = 0; i < obj.length(); i++) {
                                System.out.println(((JSONObject) obj.get(i)).get("_id"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


        JSONObject postData = new JSONObject();
        try {
            postData.put("roomType", "Jazz");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }
}