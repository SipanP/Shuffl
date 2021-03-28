package com.shufflteam.shuffl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import android.util.Log;


public class MainActivity extends AppCompatActivity {


    private static final String CLIENT_ID = "2e42d37ff4024cffa8b868e7a30061ca";
    private static final String REDIRECT_URI = "com.shufflteam.shuffl://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    private List<Playlist> playlists;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playlists = new ArrayList<>();
        preparePlaylist();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(playlists);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter.setOnItemClickListener(new ClickListener<Playlist>(){
            @Override
            public void onItemClick(Playlist data) {
                Toast.makeText(MainActivity.this, data.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void preparePlaylist(){
        Playlist playlist = new Playlist("Test 1", R.drawable.playlist_image);
        playlists.add(playlist);
        playlist = new Playlist("Test 2", R.drawable.playlist_image);
        playlists.add(playlist);
        playlist = new Playlist("Test 3", R.drawable.playlist_image);
        playlists.add(playlist);
        playlist = new Playlist("Test 4", R.drawable.playlist_image);
        playlists.add(playlist);
        playlist = new Playlist("Test 5", R.drawable.playlist_image);
        playlists.add(playlist);
        playlist = new Playlist("Test 6", R.drawable.playlist_image);
        playlists.add(playlist);
        playlist = new Playlist("Test 7", R.drawable.playlist_image);
        playlists.add(playlist);
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
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
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
    }
}