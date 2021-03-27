package com.shufflteam.shuffl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Playlist> playlists;
    private RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

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
//        recyclerAdapter.setOnItemClickListener(new ClickListener<Playlist>(){
//            @Override
//            public void onItemClick(Playlist data) {
//                Toast.makeText(MainActivity.this, data.getName(), Toast.LENGTH_SHORT).show();
//            }
//        });
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void preparePlaylist(){
        Playlist playlist = new Playlist("Test 1");
        playlists.add(playlist);
        playlist = new Playlist("Test 2");
        playlists.add(playlist);
        playlist = new Playlist("Test 3");
        playlists.add(playlist);
        playlist = new Playlist("Test 4");
        playlists.add(playlist);
    }
}