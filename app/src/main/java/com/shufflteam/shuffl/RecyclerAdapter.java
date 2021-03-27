package com.shufflteam.shuffl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    private List<com.shufflteam.shuffl.Playlist> playlists;
    RecyclerAdapter(List<com.shufflteam.shuffl.Playlist> movieList){
        this.playlists = movieList;
    }
    @Override
    public com.shufflteam.shuffl.RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(com.shufflteam.shuffl.RecyclerAdapter.MyViewHolder holder, final int position) {
        final com.shufflteam.shuffl.Playlist movie = playlists.get(position);
        holder.name.setText(movie.getName());
    }
    @Override
    public int getItemCount() {
        return playlists.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
